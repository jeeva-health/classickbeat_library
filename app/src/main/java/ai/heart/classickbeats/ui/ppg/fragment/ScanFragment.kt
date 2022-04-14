package ai.heart.classickbeats.ui.ppg.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.compute.ProcessingData
import ai.heart.classickbeats.databinding.FragmentScanBinding
import ai.heart.classickbeats.domain.CameraReading
import ai.heart.classickbeats.graph.RunningGraph
import ai.heart.classickbeats.model.Constants.SCAN_DURATION
import ai.heart.classickbeats.navigateToHeartRateFragment
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.common.CircleProgressBar
import ai.heart.classickbeats.ui.ppg.AccelerometerListener
import ai.heart.classickbeats.ui.ppg.PixelAnalyzer
import ai.heart.classickbeats.ui.ppg.viewmodel.MonitorViewModel
import ai.heart.classickbeats.ui.ppg.viewmodel.ScanViewModel
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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import com.github.mikephil.charting.charts.LineChart
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@AndroidEntryPoint
class ScanFragment : Fragment(R.layout.fragment_scan) {

    private val binding by viewBinding(FragmentScanBinding::bind)

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    private val scanViewModel: ScanViewModel by activityViewModels()

    private lateinit var navController: NavController

    private var chart: LineChart? = null

    private lateinit var sensorManager: SensorManager

    private var mAccelerometer: Sensor? = null

    private var countdownType: Int = 0

    private var camera: CameraDevice? = null
    private var session: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

    private val requiredPermissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pixelAnalyzer = PixelAnalyzer(requireContext())

        fps = monitorViewModel.fps

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (mAccelerometer == null) {
            TODO("No Accelerometer found")
        }

        accelerometerListener = AccelerometerListener(
            accelerationHandler = handleAcceleration,
            recordValue = recordAccelerationValue
        )

        monitorViewModel.fetchUser()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //redirect to my health fragment delete after test
        binding.heartRateTv.setOnClickListener{
            findNavController().navigate(ScanFragmentDirections.actionScanFragmentToMyHealthFragment())
        }


        setLightStatusBar()

        navController = findNavController()

        // To make sure the bottom navigation is correctly set
        requireActivity().navigateToHeartRateFragment()

        // If its first scan show the scan tutorial dialog fragment
        scanViewModel.isFirstTimeScanCompleted.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                navigateToScanTutorialFragment()
            }
        })

        // Resets the dynamic heart rate
        updateDynamicHeartRate(-1)

        checkPermission(Manifest.permission.CAMERA)
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        // For circular boundary of image view
        binding.viewFinderLayout.clipToOutline = true

        chart = binding.lineChart.apply {
            setDrawGridBackground(false)
            description.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.isEnabled = false
            xAxis.isEnabled = false
            legend.isEnabled = false
            setNoDataText("")
            invalidate()
            requestLayout()
        }

        textureView = binding.viewFinder

        circularProgressBar = binding.circularProgressBar

        textureView.surfaceTextureListener = surfaceTextureListener

        startBackgroundThread()

        binding.startBtn.setSafeOnClickListener {
            requestForPermissions()
        }

        binding.info.setSafeOnClickListener {
            val action = ScanFragmentDirections.actionScanFragmentToHrvInfoBottomSheetFragment()
            navController.navigate(action)
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
            } else {
                if (countdownType == 0) {
                    binding.countdown.text = it.toString()
                } else {
                    val progress = ((SCAN_DURATION - it + 1) * 100 / SCAN_DURATION).toFloat()
                    circularProgressBar.setProgressWithAnimation(progress)
                    circularProgressBar.invalidate()
                    circularProgressBar.requestLayout()
                }
            }
        })

        monitorViewModel.dynamicGraphCoordinates.observe(viewLifecycleOwner, EventObserver {
            val (x, y) = it
            RunningGraph.addEntry(chart!!, x, y)
        })
    }

    private fun handleStartButtonClick() {
        binding.startBtn.visibility = View.GONE
        binding.countdown.visibility = View.VISIBLE
        binding.viewFinderLayout.visibility = View.VISIBLE
        startInitialCountdown()
        hideBottomNavigation()
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
                    cameraReading?.apply {
                        if (green / red > 0.5 || blue / red > 0.5) {
                            badImageCounter++
                        } else {
                            badImageCounter = 0
                        }
                        if (badImageCounter >= 45) {
                            postOnMainLooper {
                                showLongToast("Please place the finger on the camera and flash completely")
                                endIncompleteScanning()
                            }
                        }
                        monitorViewModel.addFrameDataToList(cameraReading)
                        monitorViewModel.calculateCenteredSignal()
                    }

                    //Calculating dynamic BPM
                    if (imageCounter % (5 * fps) == 0 && imageCounter > (6 * fps)) {
                        lifecycleScope.launchWhenResumed {
                            val dynamicBPM = calculateDynamicBPM(
                                monitorViewModel.centeredSignal.toList(),
                                monitorViewModel.timeList.toList()
                            )
                            postOnMainLooper {
                                updateDynamicHeartRate(dynamicBPM)
                            }
                        }
                    }
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

    private fun endScanning() {
        Timber.i("endScanning called")
        monitorViewModel.isProcessing = false
        monitorViewModel.endScanHandling()
        session?.abortCaptures()
        camera?.close()
        stopBackgroundThread()
        imageCounter = 0
        navigateToScanQuestionFragment()
        scanViewModel.setFirstScanCompleted()
    }

    private fun endIncompleteScanning() {
        monitorViewModel.isProcessing = false
        monitorViewModel.endTimer()
        session?.abortCaptures()
        camera?.close()
        stopBackgroundThread()
        imageCounter = 0
        badImageCounter = 0
        chart?.data?.clearValues()

        binding.circularProgressBar.setProgressWithAnimation(0.0f)
        binding.heartRate.text = "_ _"

        lifecycleScope.launchWhenResumed {
            delay(2000)
            postOnMainLooper {
                navController.navigate(R.id.scanFragment)
            }
        }
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

        // TODO(Ritesh: move to appropriate location)
        scanViewModel.getPpgHistoryDataByCount(10)
        scanViewModel.getPpgHistoryDataByDuration(-100)
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
            showLongToast("Moving too much!")
            endIncompleteScanning()
        }
    }

    private val recordAccelerationValue = fun(x: Float, y: Float, z: Float, timeStamp: Long) {
        if (monitorViewModel.isProcessing) {
            monitorViewModel.addAccelerationReading(x, y, z, timeStamp)
        }
    }

    private suspend fun calculateDynamicBPM(
        centeredSignal: List<Double>,
        timeStamp: List<Int>
    ) = withContext(Dispatchers.Default) {

        Timber.i("TrackTime: Updating Dynamic BPM in thread.")
        val windowSize = 101

        // TODO(Harsh: check if leveledSignal is needed for dynamic bpm, else split below function)
        val leveledSignal = ProcessingData.computeLeveledSignal(
            timeList = timeStamp,
            centeredSignalList = centeredSignal,
            windowSize = windowSize
        )
        val (ibiList, _) = ProcessingData.calculateIbiListAndQuality(
            leveledSignal,
            monitorViewModel.fInterp
        )
        val (meanNN, _, _, _, _) = ProcessingData.calculatePulseStats(ibiList)
        val bpm = (60 * 1000.0) / meanNN

        return@withContext bpm.toInt()
    }

    private fun updateDynamicHeartRate(bpm: Int) {
        var heartRateStr = "_ _"
        if (bpm >= 0) {
            heartRateStr = "$bpm"
        }
        binding.heartRate.text = heartRateStr
    }

    private fun navigateToScanQuestionFragment() {
        val action = ScanFragmentDirections.actionScanFragmentToScanQuestionFragment()
        navController.navigate(action)
    }

    private fun navigateToScanTutorialFragment() {
        val action = ScanFragmentDirections.actionScanFragmentToScanTutorialFragment()
        navController.navigate(action)
    }

    private fun updateScanMessage(countdownType: Int, countdownTime: Int) {
        val messageId = if (countdownType == 0) {
            R.string.scan_message_1
        } else {
            val time = SCAN_DURATION - countdownTime
            when {
                time <= 5 -> R.string.scan_message_3
                time <= 9 -> R.string.scan_message_5
                time <= 13 -> R.string.scan_message_7
                time <= 17 -> R.string.scan_message_8
                time <= 22 -> R.string.scan_message_9
                time <= 27 -> R.string.scan_message_10
                else -> R.string.scan_message_11
            }
        }
        binding.message.text = getString(messageId)
    }
}