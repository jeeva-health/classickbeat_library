package ai.heart.classickbeats.ui.ppg.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanBinding
import ai.heart.classickbeats.lib.model.CameraReading
import ai.heart.classickbeats.lib.process.ProcessImage
import ai.heart.classickbeats.model.Constants.SCAN_DURATION
import ai.heart.classickbeats.model.Constants.SPLIT_SCAN_DURATION
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.ppg.AccelerometerListener
import ai.heart.classickbeats.ui.ppg.PixelAnalyzer
import ai.heart.classickbeats.ui.ppg.viewmodel.MonitorViewModel
import ai.heart.classickbeats.ui.widgets.CircleProgressBar
import ai.heart.classickbeats.utils.*
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.text.format.DateUtils
import android.util.Range
import android.view.Surface
import android.view.TextureView
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ScanFragment : Fragment(R.layout.fragment_scan) {

    private val binding by viewBinding(FragmentScanBinding::bind)

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    private lateinit var sensorManager: SensorManager

    private var mAccelerometer: Sensor? = null

    private var countdownType: Int = 0

    private var isIntermediatedProcessing = false

    private var camera: CameraDevice? = null
    private var session: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

    private val requiredPermissions = listOf(
        Manifest.permission.CAMERA,
    )

    private val permissionToRequest = mutableListOf<String>()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionResult ->

            requiredPermissions.forEach { permission ->
                if (permissionResult[permission] == true) {
                    permissionToRequest.remove(permission)
                }
            }
            if (ifAllMustPermissionsAreGranted()) {
                handleStartButtonClick()
            }
        }

    private fun ifAllMustPermissionsAreGranted() = permissionToRequest.isEmpty()

    private fun requestForPermissions() {
        if (ifAllMustPermissionsAreGranted()) {
            handleStartButtonClick()
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

    private var pixelAnalyzer: PixelAnalyzer? = null

    private lateinit var textureView: TextureView

    private lateinit var circularProgressBar: CircleProgressBar

    private var width: Int = 0
    private var height: Int = 0

    private lateinit var accelerometerListener: AccelerometerListener

    private var fps = 30

    private var imageCounter = 0

    private var badImageCounter = 0

    private val processImage = ProcessImage()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pixelAnalyzer = PixelAnalyzer(requireContext())

        fps = monitorViewModel.fps

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (mAccelerometer == null) {
            TODO("No Accelerometer found")
        }

        accelerometerListener = AccelerometerListener(handleAcceleration)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLightStatusBar()

        checkPermission(Manifest.permission.CAMERA)

        // For circular boundary of image view
        binding.viewFinderLayout.clipToOutline = true

        textureView = binding.viewFinder

        circularProgressBar = binding.circularProgressBar

        textureView.surfaceTextureListener = surfaceTextureListener

        startBackgroundThread()

        binding.startBtn.setSafeOnClickListener {
            requestForPermissions()
        }

        monitorViewModel.timerProgress.observe(viewLifecycleOwner, EventObserver {
            updateScanMessage(countdownType, it)
            if (it == 0) {
                if (countdownType == 0) {
                    binding.countdown.visibility = View.GONE
                    startScanning()
                } else {
                    if (monitorViewModel.isProcessing) {
                        endScanning()
                    }
                }
            } else if (it == SPLIT_SCAN_DURATION && !isIntermediatedProcessing) {
                isIntermediatedProcessing = true
                endSplitScanning()
            } else {
                if (countdownType == 0) {
                    binding.countdown.text = it.toString()
                } else {
                    val progress = ((SCAN_DURATION - it) * 100 / SCAN_DURATION).toFloat()
                    circularProgressBar.setProgressWithAnimation(progress)
                    circularProgressBar.invalidate()
                    circularProgressBar.requestLayout()
                }
            }
        })
    }

    private fun handleStartButtonClick() {
        binding.startBtn.visibility = View.GONE
        binding.countdown.visibility = View.VISIBLE
        binding.viewFinderLayout.visibility = View.VISIBLE
        startInitialCountdown()
    }

    private fun startInitialCountdown() {
        countdownType = 0
        monitorViewModel.startTimer(5 * DateUtils.SECOND_IN_MILLIS)
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            this@ScanFragment.width = width
            this@ScanFragment.height = height
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
                this@ScanFragment.camera = camera
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

            override fun onDisconnected(camera: CameraDevice) {
                TODO("Handle camera onDisconnect event")
            }

            override fun onError(camera: CameraDevice, error: Int) {
                TODO("Handle camera onError event")
            }
        }

    private val stateSessionCallback: CameraCaptureSession.StateCallback =
        object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                this@ScanFragment.session = session
                Timber.i("Session Start")

                try {
                    session.setRepeatingRequest(
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

    private val onImageAvailableListener =
        ImageReader.OnImageAvailableListener { reader: ImageReader ->
            val img = reader.acquireLatestImage() ?: return@OnImageAvailableListener
            if (monitorViewModel.isProcessing) {
                imageCounter++
                if (imageCounter >= fps * 1) {
                    val cameraReading: CameraReading? = pixelAnalyzer?.processImageRenderScript(img)
                    processImage.processImage(cameraReading!!)
                }
            }
            img.close()
        }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        try {
            val cameraFacing = CameraCharacteristics.LENS_FACING_BACK
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
            builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH)
            val texture = textureView.surfaceTexture
            texture?.setDefaultBufferSize(width, height)
            builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON)
            builder.addTarget(imageReader!!.surface)
            builder.addTarget(Surface(texture))
            builder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_OFF)
            builder.set(CaptureRequest.CONTROL_AWB_LOCK, true)
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
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            Timber.e(e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startScanning() {
        if (monitorViewModel.isProcessing) {
            Timber.e("scanning already running")
        } else {
            countdownType = 1
            monitorViewModel.resetReadings()
            monitorViewModel.isProcessing = true
            monitorViewModel.startTimer()
        }
    }

    private fun endSplitScanning() {
        lifecycleScope.launchWhenResumed {
            Timber.i("TrackTime: endSplitScanning called")
            val timeListImmutable = monitorViewModel.timeList.toList()
            val centeredSignalListImmutable = monitorViewModel.centeredSignal.toList()
            monitorViewModel.calculateResultSplit(timeListImmutable, centeredSignalListImmutable)
        }
    }

    private fun endScanning() {
        Timber.i("endScanning called")
        monitorViewModel.isProcessing = false
        monitorViewModel.endScanHandling()
        session?.abortCaptures()
        camera?.close()
        stopBackgroundThread()
        imageCounter = 0
    }

    private fun endIncompleteScanning() {
        monitorViewModel.isProcessing = false
        monitorViewModel.endTimer()
        session?.abortCaptures()
        camera?.close()
        stopBackgroundThread()
        imageCounter = 0
        badImageCounter = 0

        binding.circularProgressBar.setProgressWithAnimation(0.0f)
        binding.heartRate.text = "_ _"
    }

    override fun onResume() {
        super.onResume()
        mAccelerometer?.also { accelerometer ->
            sensorManager.registerListener(
                accelerometerListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(accelerometerListener)
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

    private val handleAcceleration = fun() {
        if (monitorViewModel.isProcessing) {
            Timber.i("Moving too much!")
            endIncompleteScanning()
        }
    }

    private fun updateScanMessage(countdownType: Int, countdownTime: Int) {
        val messageId = if (countdownType == 0) {
            R.string.scan_message_1
        } else {
            val time = SCAN_DURATION - countdownTime
            when {
                time <= 5 -> R.string.scan_message_2
                time <= 10 -> R.string.scan_message_3
                time <= 15 -> R.string.scan_message_4
                time <= 22 -> R.string.scan_message_5
                time <= 30 -> R.string.scan_message_6
                time <= 35 -> R.string.scan_message_7
                time <= 43 -> R.string.scan_message_8
                time <= 50 -> R.string.scan_message_9
                time <= 57 -> R.string.scan_message_10
                else -> R.string.scan_message_11
            }
        }
        binding.message.text = getString(messageId)
    }
}
