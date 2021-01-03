package ai.heart.classickbeats.monitor

import ai.heart.classickbeats.MainActivity
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanBinding
import ai.heart.classickbeats.utils.EventObserver
import ai.heart.classickbeats.utils.viewBinding
import ai.heart.classickbeats.widgets.CircleProgressBar
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context.CAMERA_SERVICE
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Range
import android.view.Surface
import android.view.TextureView
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chaquo.python.Python
import timber.log.Timber
import java.lang.Boolean


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

    private val mean1List = mutableListOf<Double>()
    private val mean2List = mutableListOf<Double>()
    private val timeList = mutableListOf<Int>()
    private val fps = 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pixelAnalyzer = PixelAnalyzer(requireContext(), monitorViewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).hideSystemUI()

        navController = findNavController()

        textureView = binding.viewFinder
        cameraCaptureButton = binding.cameraCaptureButton
        circularProgressBar = binding.circularProgressBar

        textureView.surfaceTextureListener = surfaceTextureListener

        cameraCaptureButton.setOnLongClickListener {
            it.visibility = View.GONE
            binding.countdownAnimation.visibility = View.VISIBLE
            binding.countdownAnimation.playAnimation()
            binding.countdownAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    binding.countdownAnimation.visibility = View.GONE
                    circularProgressBar.visibility = View.VISIBLE
                    monitorViewModel.startTimer()
                    startScanning()
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationRepeat(p0: Animator?) {
                }

            })
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

                try {
                    session?.setRepeatingRequest(
                        createCaptureRequest()!!,
                        null,
                        mBackgroundHandler
                    )
                } catch (e: CameraAccessException) {
                    Timber.e(e)
                }
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {}
        }

    var imageCounter = 0

    private val onImageAvailableListener =
        ImageReader.OnImageAvailableListener { reader: ImageReader ->
            if (monitorViewModel.isProcessing){
                imageCounter++
            }
            val img = reader.acquireLatestImage() ?: return@OnImageAvailableListener
            if (imageCounter >= fps*3) {
                val means = when (navArgs.testType) {
                    TestType.HEART_RATE -> pixelAnalyzer?.processImageHeart(img) ?: Triple(0.0, 0.0, 0)
                    TestType.OXYGEN_SATURATION -> pixelAnalyzer?.processImage(img) ?: Triple(0.0, 0.0, 0)
                }
                // val gMean = pixelAnalyzer?.processImageHeart(img) ?: Pair(0.0, 0)
                mean1List.add(means.first)
                mean2List.add(means.second)
                timeList.add(means.third)
            }
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
                characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES)
                    ?.forEach { range ->
                        Timber.i("Supported FPS range: (${range.lower} - ${range.upper})")
                    }
                val map =
                    characteristics[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]
                val fpsRange =
                    map!!.highSpeedVideoFpsRanges // this range intends available fps range of device's camera.


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
            val texture = textureView.surfaceTexture
            texture?.setDefaultBufferSize(width, height)
            builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON)
            builder.addTarget(imageReader!!.surface)
             builder.addTarget(Surface(texture)!!)
            builder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_OFF)
            builder.set(CaptureRequest.CONTROL_AWB_LOCK, Boolean.TRUE)
            builder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, Range.create(fps, fps))
//            builder.set(CaptureRequest.SENSOR_SENSITIVITY, 50);
//            builder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, 10000000);
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
        monitorViewModel.isProcessing = true
        monitorViewModel.startTimer()
    }

    private fun endScanning() {
        monitorViewModel.isProcessing = false
        session?.abortCaptures()
        camera?.close()
        stopBackgroundThread()
        monitorViewModel.endTimer()
        val result = calculate()
        monitorViewModel.hearRateResult = result
        when (navArgs.testType) {
            TestType.HEART_RATE -> navigateToHeartResultFragment()
            //TestType.OXYGEN_SATURATION -> navigateToOxygenResultFragment()
            TestType.OXYGEN_SATURATION -> navigateToHeartResultFragment()
        }
        imageCounter = 0
    }

    private fun calculate(): HeartRateResult {
        val mean1Array: Array<Double> = mean1List.toTypedArray()
        val mean2Array: Array<Double> = mean2List.toTypedArray()
        val timeArray: Array<Int> = timeList.toTypedArray()
        val python: Python = Python.getInstance()
        val filePyObject = python.getModule("HeartStats")
        val classPyObject = filePyObject.callAttr("HeartStats")
        val response = classPyObject.callAttr("HR_stats", mean1Array, mean2Array, timeArray).asList()
        val bpm = response[0].toDouble()
        val hrv = response[1].toDouble()
        val afib = when (response[2].toDouble()) {
            0.0 -> "Not Detected"
            1.0 -> "Possible"
            else -> "Detected"
        }
        val quality = response[3].toDouble()
        val qualityStr = when {
            quality <= 1e-5 -> "PERFECT Quality Recording, Good job!"
            quality <= 1e-4 -> "Good Quality Recording, Good job!"
            quality <= 1e-3 -> "Decent Quality Recording!"
            quality <= 2e-2 -> "Poor Quality Recording. Please record again!"
            else -> "Extremely poor signal quality. Please record again!"
        }
        val result = HeartRateResult(bpm = bpm, hrv = hrv, aFib = afib, quality = qualityStr)
        return result
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