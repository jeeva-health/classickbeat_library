package ai.heart.classickbeats.monitor

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanBinding
import ai.heart.classickbeats.utils.viewBinding
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.text.format.DateUtils
import android.util.Size
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*


@AndroidEntryPoint
class ScanFragment : Fragment(R.layout.fragment_scan) {

    private val binding by viewBinding(FragmentScanBinding::bind)

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    private var camera: CameraDevice? = null
    private var session: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null
    private var cameraID: String? = null
    private var imageDimension: Size? = null
    private var cameraManager: CameraManager? = null

    private var pixelAnalyzer: PixelAnalyzer? = null

    private val requiredPermissions = listOf(Manifest.permission.CAMERA)
    private val permissionToRequest = mutableListOf<String>()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionResult ->

            requiredPermissions.forEach { permission ->
                if (permissionResult[permission] == true) {
                    permissionToRequest.remove(permission)
                }
            }
            if (ifAllMustPermissionsAreGranted()) {
                startBackgroundThread()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pixelAnalyzer = PixelAnalyzer(requireContext(), monitorViewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission(Manifest.permission.CAMERA)
        requestForPermissions()


        binding.cameraCaptureButton.setOnLongClickListener {
            it.visibility = View.GONE
            binding.circularProgressBar.visibility = View.VISIBLE
            monitorViewModel.startTimer()
            startScanning()
            true
        }

        monitorViewModel.timerProgress.observe(viewLifecycleOwner, {
            if (it == 0) {
                binding.circularProgressBar.visibility = View.GONE
                binding.cameraCaptureButton.visibility = View.VISIBLE
                endScanning()
            } else {
                val progress = ((SCAN_DURATION - it) * 100 / SCAN_DURATION).toFloat()
                binding.circularProgressBar.setProgress(progress)
                binding.circularProgressBar.invalidate()
            }
        })
    }

    private fun ifAllMustPermissionsAreGranted() = permissionToRequest.isEmpty()

    private fun requestForPermissions() {
        if (ifAllMustPermissionsAreGranted()) {
            startBackgroundThread()
        } else {
            requestPermissionLauncher.launch(permissionToRequest.toTypedArray())
        }
    }

    private fun checkPermission(permission: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionToRequest.add(permission)
        }
    }

    private val cameraStateCallback: CameraDevice.StateCallback =
        object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                this@ScanFragment.camera = camera
                try {
                    camera.createCaptureSession(
                        listOf(imageReader!!.surface),
                        stateSessionCallback,
                        mBackgroundHandler
                    )
                } catch (e: CameraAccessException) {
                    Timber.e(e)
                }
            }

            override fun onDisconnected(camera: CameraDevice) {}
            override fun onError(camera: CameraDevice, error: Int) {}
        }

    private val stateSessionCallback: CameraCaptureSession.StateCallback =
        object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                this@ScanFragment.session = session
                try {
                    session.setRepeatingRequest(
                        createCaptureRequest()!!,
                        null,
                        mBackgroundHandler
                    )
                    mBackgroundHandler?.postDelayed(
                        { stopTakingImage() },
                        SCAN_DURATION * DateUtils.SECOND_IN_MILLIS
                    )
                } catch (e: CameraAccessException) {
                    Timber.e(e)
                }
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {}
        }

    private val onImageAvailableListener =
        ImageReader.OnImageAvailableListener { reader: ImageReader ->
            val img = reader.acquireLatestImage()
            pixelAnalyzer?.processImage(img)
            img.close()
        }

    @SuppressLint("MissingPermission")
    fun startTakingImages() {
        cameraManager = requireActivity().getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            cameraID = getCamera(cameraManager!!)
            val characteristics = cameraManager?.getCameraCharacteristics(cameraID!!)
            val map = characteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            imageDimension = map.getOutputSizes(SurfaceTexture::class.java)[0]
            cameraManager?.setTorchMode(cameraID!!, true)
            cameraManager?.openCamera(cameraID!!, cameraStateCallback, null)
            imageReader = ImageReader.newInstance(
                320,
                240,
                ImageFormat.YUV_420_888,
                30
            )
            imageReader?.setOnImageAvailableListener(onImageAvailableListener, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            Timber.e(e)
        }
    }

    private fun getCamera(manager: CameraManager): String? {
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId!!)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing == lensFacing) {
                    return cameraId
                }
            }
        } catch (e: CameraAccessException) {
            Timber.e(e)
        }
        return null
    }

    fun createCaptureRequest(): CaptureRequest? {
        return try {
            val builder: CaptureRequest.Builder =
                camera!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH)
            builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON)
            builder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_OFF)
            builder.set(CaptureRequest.CONTROL_AWB_LOCK, true)
            builder.addTarget(imageReader!!.surface)
            builder.build()
        } catch (e: CameraAccessException) {
            Timber.e(e)
            null
        }
    }

    fun stopTakingImage() {
        try {
            session?.abortCaptures()
            session?.close()
            camera?.close()
        } catch (e: CameraAccessException) {
            Timber.e(e)
        }
    }

    private fun closeCamera() {
        try {
            session?.abortCaptures()
            session?.close()
            camera?.close()
            stopBackgroundThread();
        } catch (e: CameraAccessException) {
            Timber.e(e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        closeCamera()
    }

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera Background")
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            Timber.e(e)
        }
    }

    private fun startScanning() {
        monitorViewModel.isProcessing = true
        binding.switchCamera.visibility = View.GONE
        startTakingImages()
    }

    private fun endScanning() {
        monitorViewModel.isProcessing = false
        binding.switchCamera.visibility = View.VISIBLE
    }
}