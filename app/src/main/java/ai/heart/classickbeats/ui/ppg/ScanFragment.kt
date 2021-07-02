package ai.heart.classickbeats.ui.ppg

import ai.heart.classickbeats.MainActivity
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanBinding
import ai.heart.classickbeats.domain.CameraReading
import ai.heart.classickbeats.graph.RunningGraph
import ai.heart.classickbeats.shared.result.EventObserver
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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.psambit9791.jdsp.signal.peaks.FindPeak
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.Boolean
import java.util.*

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
    private var localTimeLast = 0

    private var badImageCounter = 0

    val movAvgSmall = mutableListOf<Double>()
    val movAvgLarge = mutableListOf<Double>()
    val movWindowSmall = mutableListOf<Double>()
    val movWindowLarge = mutableListOf<Double>()

    // Keep window sizes odd
    val smallWindow = fps / 10
    val largeWindow = fps + 1
    val offset = (largeWindow - 1) / 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pixelAnalyzer = PixelAnalyzer(requireContext(), monitorViewModel)
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

        (requireActivity() as MainActivity).navigateToHeartRateFragment()

        scanViewModel.isFirstTimeScanCompleted.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                navigateToScanTutorialFragment()
            }
        })

        updateDynamicHeartRate(-1)

        checkPermission(Manifest.permission.CAMERA)
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        chart = binding.lineChart.apply {
            setDrawGridBackground(false)
            description.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.isEnabled = false
            xAxis.isEnabled = false
            legend.isEnabled = false
            setNoDataText("")
            invalidate()
        }

        navController = findNavController()

        textureView = binding.viewFinder
        binding.viewFinderLayout.clipToOutline = true

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
            Timber.i("Timer: $it")
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
                    val progress = ((SCAN_DURATION - it) * 100 / SCAN_DURATION).toFloat()
                    circularProgressBar.setProgressWithAnimation(progress)
                    circularProgressBar.invalidate()
                }
            }
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

            override fun onDisconnected(camera: CameraDevice) {}
            override fun onError(camera: CameraDevice, error: Int) {}
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

    var imageCounter = 0

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
                                showLongToast("Please keep finger properly")
                                endIncompleteScanning()
                            }
                        }
                        Timber.i("badImageCounter: $badImageCounter")
                        monitorViewModel.mean1List.add(red)
                        monitorViewModel.mean2List.add(green)
                        monitorViewModel.mean3List.add(blue)
                        monitorViewModel.timeList.add(timeStamp)

                        // Calculating running moving averages and centered signal
                        monitorViewModel.processData.runningMovAvg(
                            red,
                            smallWindow,
                            movWindowSmall,
                            movAvgSmall
                        )
                        if (movAvgSmall.size > 0) {
                            monitorViewModel.processData.runningMovAvg(
                                movAvgSmall.last(),
                                largeWindow,
                                movWindowLarge,
                                movAvgLarge
                            )
                            if (movAvgSmall.size >= largeWindow) {
                                val x =
                                    -1.0 * (movAvgSmall[movAvgSmall.size - offset] - movAvgLarge.last())
                                monitorViewModel.centeredSignal.add(x)
                                RunningGraph.addEntry(
                                    chart!!,
                                    monitorViewModel.centeredSignal.size,
                                    x
                                )
                            }
                        }
                        // Timber.i("Total time: $totalTimeElapsed, Local Time: $localTimeElapsed")
                        Timber.i("Size Mov Avgs: ${movAvgSmall.size}, ${movAvgLarge.size}, ${monitorViewModel.centeredSignal.size}")

                        //Calculating dynamic BPM
                        val totalTimeElapsed = timeStamp - monitorViewModel.timeList[0]
                        val localTimeElapsed = timeStamp - localTimeLast
                        Timber.i("Total time: $totalTimeElapsed, Local Time: $localTimeElapsed")
                        if (totalTimeElapsed >= 6000 && localTimeElapsed >= 2000) {
                            val dynamicBPM = calculateDynamicBPM(
//                                monitorViewModel.centeredSignal.takeLast(150),
//                                monitorViewModel.timeList.takeLast(150)
                                monitorViewModel.centeredSignal,
                                monitorViewModel.timeList
                            )
                            postOnMainLooper {
                                updateDynamicHeartRate(dynamicBPM)
                            }
                            localTimeLast = monitorViewModel.timeList.last()
                        }
//                        chart?.let {
//                            RunningGraph.addEntry(it, monitorViewModel.mean1List.size, red)
//                        }
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
            monitorViewModel.isProcessing = true
            monitorViewModel.startTimer()
        }
    }

    private fun endScanning() {
        monitorViewModel.isProcessing = false
        session?.abortCaptures()
        camera?.close()
        stopBackgroundThread()
        monitorViewModel.endTimer()
        monitorViewModel.uploadRawData()
        monitorViewModel.calculateResult()
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
        monitorViewModel.resetReadings()
        chart?.data?.clearValues()

//        binding.viewFinderLayout.visibility = View.GONE
//        binding.startBtn.visibility = View.VISIBLE
//        binding.circularProgressBar.setProgressWithAnimation(0.0f)
//        binding.heartRate.text = "_ _"
//        showBottomNavigation()

        navController.navigate(R.id.scanFragment)
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

    private val handleAcceleration = fun() {
        if (monitorViewModel.isProcessing) {
            Timber.i("Moving too much!")
            showLongToast("Moving too much!")
            endIncompleteScanning()
        }
    }

    fun calculateDynamicBPM(centeredSignal: List<Double>, timeStamp: List<Int>): Int {
        val fp = FindPeak(centeredSignal.toDoubleArray())
        val out = fp.detectPeaks()

        val peaks = out.peaks
        Timber.i("Size, Dynamic Peaks: ${peaks.size}, ${Arrays.toString(peaks)}")
        var filteredPeaks = out.filterByProminence(peaks, 0.4, null)
        Timber.i(
            "Size, Dynamic Prominent Peaks: ${filteredPeaks.size}, ${
                Arrays.toString(
                    filteredPeaks
                )
            }"
        )

        val ibiList = mutableListOf<Double>() //Time in milliseconds
        for (i in 0 until filteredPeaks.size - 1) {
            ibiList.add((timeStamp[filteredPeaks[i + 1]] - timeStamp[filteredPeaks[i]]) * 1.0)
        }
        val ibiAvg = ibiList.average()
        Timber.i("Size, Average, ibiList: ${ibiList.size}, $ibiAvg, ${Arrays.toString(ibiList.toDoubleArray())}")
        return ((60 * 1000.0) / ibiAvg).toInt()
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
}