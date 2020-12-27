package ai.heart.classickbeats.monitor

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanBinding
import ai.heart.classickbeats.utils.EventObserver
import ai.heart.classickbeats.utils.viewBinding
import ai.heart.classickbeats.widgets.CircleProgressBar
import android.annotation.SuppressLint
import android.content.Context.CAMERA_SERVICE
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import timber.log.Timber
import java.lang.Boolean
import java.util.*


class ScanFragmentBorrowed : Fragment(R.layout.fragment_scan) {

    private val binding by viewBinding(FragmentScanBinding::bind)

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    private val navArgs: ScanFragmentBorrowedArgs by navArgs()

    private lateinit var navController: NavController

    private var camera: CameraDevice? = null
    private var session: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

    private var pixelAnalyzer: PixelAnalyzer? = null

    private lateinit var textureView: TextureView
    private lateinit var cameraCaptureButton: AppCompatImageButton
    private lateinit var circularProgressBar: CircleProgressBar

    private var width: Int = 0
    private var height: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pixelAnalyzer = PixelAnalyzer(requireContext(), monitorViewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        textureView = binding.viewFinder
        cameraCaptureButton = binding.cameraCaptureButton
        circularProgressBar = binding.circularProgressBar

        textureView.surfaceTextureListener = surfaceTextureListener

        cameraCaptureButton.setOnLongClickListener {
            it.visibility = View.GONE
            circularProgressBar.visibility = View.VISIBLE
            startScanning()
            true
        }

        startBackgroundThread()

        monitorViewModel.timerProgress.observe(viewLifecycleOwner, EventObserver {
            if (it == 0) {
                circularProgressBar.visibility = View.GONE
                cameraCaptureButton.visibility = View.VISIBLE
                endScanning()
            } else {
                val progress = ((SCAN_DURATION - it) * 100 / SCAN_DURATION).toFloat()
                circularProgressBar.setProgress(progress)
                circularProgressBar.invalidate()
            }
        })
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            this@ScanFragmentBorrowed.width = width
            this@ScanFragmentBorrowed.height = height
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(
            texture: SurfaceTexture,
            width: Int,
            height: Int
        ) {
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture) = true
        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) = Unit
    }

    private val cameraStateCallback: CameraDevice.StateCallback =
        object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                this@ScanFragmentBorrowed.camera = camera
                Timber.i("Camera Open Called")
                try {
                    imageReader = ImageReader.newInstance(
                        320,
                        240,
                        ImageFormat.YUV_420_888,
                        30
                    )
                    imageReader?.setOnImageAvailableListener(
                        onImageAvailableListener,
                        mBackgroundHandler
                    )

                    val texture = textureView.surfaceTexture
                    texture?.setDefaultBufferSize(width, height)

                    camera.createCaptureSession(
                        listOf(Surface(texture!!), imageReader?.surface),
                        stateSessionCallback,
                        mBackgroundHandler
                    )
                } catch (e: CameraAccessException) {
                    Timber.e("Failed Camera Session $e")
                }
            }

            override fun onDisconnected(camera: CameraDevice) {}
            override fun onError(camera: CameraDevice, error: Int) {}
        }

    private val stateSessionCallback: CameraCaptureSession.StateCallback =
        object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                this@ScanFragmentBorrowed.session = session
                Timber.i("Session Start")
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {}
        }

    private val onImageAvailableListener =
        ImageReader.OnImageAvailableListener { reader: ImageReader ->
            val img = reader.acquireLatestImage() ?: return@OnImageAvailableListener
            pixelAnalyzer?.processImage(img)
            img.close()
        }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        try {
            val cameraFacing = when (navArgs.testType) {
                TestType.HEART_RATE -> CameraCharacteristics.LENS_FACING_BACK
                TestType.OXYGEN_SATURATION -> CameraCharacteristics.LENS_FACING_FRONT
            }
            val cameraManager =
                requireActivity().getSystemService(CAMERA_SERVICE) as CameraManager
            val cameraID: String = getCamera(cameraManager, cameraFacing)!!
            cameraManager.openCamera(cameraID, cameraStateCallback, null)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun getCamera(manager: CameraManager, cameraFacing: Int): String? {
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId!!)
                if (cameraFacing == characteristics.get(CameraCharacteristics.LENS_FACING)) {
                    return cameraId
                }
            }
        } catch (e: CameraAccessException) {
            Timber.e(e)
        }
        return null
    }

    private fun createCaptureRequest(): CaptureRequest? {
        return try {
            val builder = camera!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            if (navArgs.testType == TestType.HEART_RATE) {
                builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH)
            }
            builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON)
            builder.addTarget(imageReader!!.surface)
            builder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_OFF)
            builder.set(CaptureRequest.CONTROL_AWB_LOCK, Boolean.TRUE)
            builder.build()
        } catch (e: CameraAccessException) {
            Timber.e(e)
            null
        }
    }

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera Background")
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        mBackgroundThread!!.quitSafely()
        try {
            mBackgroundThread!!.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            Timber.e(e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startScanning() {
        try {
            session?.setRepeatingRequest(
                createCaptureRequest()!!,
                null,
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            Timber.e(e)
        }

        monitorViewModel.isProcessing = true
        monitorViewModel.startTimer()
    }

    private fun endScanning() {
        monitorViewModel.isProcessing = false
        session?.abortCaptures()
        camera?.close()
        stopBackgroundThread()
        monitorViewModel.endTimer()
        when (navArgs.testType) {
            TestType.HEART_RATE -> navigateToHeartResultFragment()
            TestType.OXYGEN_SATURATION -> navigateToOxygenResultFragment()
        }
    }

    private fun navigateToHeartResultFragment() {
        val action =
            ScanFragmentBorrowedDirections.actionScanFragmentBorrowedToHeartResultFragment()
        navController.navigate(action)
    }

    private fun navigateToOxygenResultFragment() {
        val action =
            ScanFragmentBorrowedDirections.actionScanFragmentBorrowedToOxygenResultFragment()
        navController.navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            session?.abortCaptures()
            camera?.close()
            stopBackgroundThread()
            monitorViewModel.endTimer()
        } catch (e: CameraAccessException) {
            Timber.e(e)
        }
    }
}