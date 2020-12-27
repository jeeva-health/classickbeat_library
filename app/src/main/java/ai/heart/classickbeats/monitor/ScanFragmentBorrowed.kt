package ai.heart.classickbeats.monitor

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanBorrowedBinding
import ai.heart.classickbeats.utils.viewBinding
import android.annotation.SuppressLint
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import timber.log.Timber
import java.lang.Boolean
import java.util.*


class ScanFragmentBorrowed : Fragment(R.layout.fragment_scan_borrowed) {

    private val binding by viewBinding(FragmentScanBorrowedBinding::bind)

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    private lateinit var navController: NavController

    private val CAMERA_FACING = CameraCharacteristics.LENS_FACING_BACK
    private var camera: CameraDevice? = null
    private var session: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null
    var imageView: ImageView? = null
    private var processBtn: Button? = null
    private val recordTimeInMilliSeconds = 33 * 1000 // second * milli

    private var pixelAnalyzer: PixelAnalyzer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pixelAnalyzer = PixelAnalyzer(requireContext(), monitorViewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        imageView = binding.imageView
        processBtn = binding.processBtn

        processBtn!!.setOnClickListener {
            startProcess()
            processBtn!!.text = "PROCESS STARTED"
        }

        startBackgroundThread()
    }

    private val cameraStateCallback: CameraDevice.StateCallback =
        object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                this@ScanFragmentBorrowed.camera = camera
                Timber.e("Camera Open Called")
                try {
                    camera.createCaptureSession(
                        Arrays.asList(imageReader!!.surface),
                        stateSessionCallback,
                        mBackgroundHandler
                    )
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                    Timber.e("Failed Camera Session")
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
                try {
                    this@ScanFragmentBorrowed.session?.setRepeatingRequest(
                        createCaptureRequest()!!,
                        null,
                        mBackgroundHandler
                    )
                    mBackgroundHandler!!.postDelayed(
                        { stopTakingImage() },
                        recordTimeInMilliSeconds.toLong()
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


    private fun startProcess() {
        if (requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
        ) {
            startTakingImages()
        } else {
            Timber.e("Mobile Don't Have Flash")
        }
    }

    private fun getCamera(manager: CameraManager): String? {
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId!!)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)!!
                if (facing == CAMERA_FACING) {
                    return cameraId
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return null
    }

    private fun createCaptureRequest(): CaptureRequest? {
        return try {
            val builder = camera!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH)
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

    fun stopTakingImage() {
        processBtn!!.text = "START PROCESS"
        try {
            session!!.abortCaptures()
            session!!.close()
            camera!!.close()
        } catch (e: CameraAccessException) {
            Timber.e(e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startTakingImages() {
        val cameraManager =
            requireActivity().getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            val cameraID: String = getCamera(cameraManager)!!
            cameraManager.setTorchMode(cameraID, true)
            cameraManager.openCamera(cameraID, cameraStateCallback, null)
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
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            session?.abortCaptures()
            camera?.close()
            stopBackgroundThread()
        } catch (e: CameraAccessException) {
            Timber.e(e)
        }
    }
}