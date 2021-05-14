package ai.heart.classickbeats.ui.ppg

import ai.heart.classickbeats.MainActivity
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanBinding
import ai.heart.classickbeats.domain.CameraReading
import ai.heart.classickbeats.domain.TestType
import ai.heart.classickbeats.graph.RunningGraph
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.utils.postOnMainLooper
import ai.heart.classickbeats.utils.showLongToast
import ai.heart.classickbeats.utils.viewBinding
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
import androidx.navigation.fragment.navArgs
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

    private val navArgs: ScanFragmentArgs by navArgs()

    private lateinit var navController: NavController

    private lateinit var chart: LineChart

    private lateinit var sensorManager: SensorManager

    private var mAccelerometer: Sensor? = null

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

            }
        }

    private fun ifAllMustPermissionsAreGranted() = permissionToRequest.isEmpty()

    private fun requestForPermissions() {
        if (ifAllMustPermissionsAreGranted()) {

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

    private var width: Int = 0
    private var height: Int = 0

    private lateinit var accelerometerListener: AccelerometerListener

    private val fps = 30
    private var localTimeLast = 0

    private var badImageCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pixelAnalyzer = PixelAnalyzer(requireContext(), monitorViewModel)

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (mAccelerometer == null) {
            TODO("No Accelerometer found")
        }

        accelerometerListener = AccelerometerListener(handleAcceleration)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).hideSystemUI()

        monitorViewModel.testType = navArgs.testType

        updateDynamicHeartRate(-1)

        checkPermission(Manifest.permission.CAMERA)
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestForPermissions()

//        chart = binding.lineChart.apply {
//            setDrawGridBackground(false)
//            description.isEnabled = false
//            axisRight.isEnabled = false
//            axisLeft.isEnabled = false
//            xAxis.isEnabled = false
//            legend.isEnabled = false
//            setNoDataText("")
//            invalidate()
//        }

        navController = findNavController()

        textureView = binding.viewFinder

        textureView.surfaceTextureListener = surfaceTextureListener

        startBackgroundThread()

        monitorViewModel.timerProgress.observe(viewLifecycleOwner, EventObserver {
            Timber.i("Timer: $it")
            if (it == 0) {
                if (monitorViewModel.isProcessing) {
                    endScanning()
                }
            } else {
                val progress = ((SCAN_DURATION - it) * 100 / SCAN_DURATION).toFloat()
//                circularProgressBar.setProgress(progress)
//                circularProgressBar.invalidate()
            }
        })
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
                                restartReading()
                            }
                        }
                        Timber.i("badImageCounter: $badImageCounter")
                        monitorViewModel.mean1List.add(red)
                        monitorViewModel.mean2List.add(green)
                        monitorViewModel.timeList.add(timeStamp)

                        // Calculating dynamic heart rate
                        val totalTimeElapsed = timeStamp - monitorViewModel.timeList[0]
                        val localTimeElapsed = timeStamp - localTimeLast
                        Timber.i("Total time: $totalTimeElapsed, Local Time: $localTimeElapsed")
                        if (totalTimeElapsed >= 6000 && localTimeElapsed >= 2000) {
                            val dynamicBPM = calculateDynamicBPM(
                                monitorViewModel.mean1List.takeLast(150),
                                monitorViewModel.timeList.takeLast(150)
                            )
                            updateDynamicHeartRate(dynamicBPM)
                            localTimeLast = monitorViewModel.timeList.last()
                        }
                        RunningGraph.addEntry(chart, monitorViewModel.mean1List.size, red)
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
            if (navArgs.testType == TestType.HEART_RATE) {
                builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH)
            }
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
        if (monitorViewModel.isProcessing) {
            Timber.e("scanning already running")
        } else {
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
        monitorViewModel.calculateResult()

        navigateToCalculationFragment()

        imageCounter = 0
    }

    private fun navigateToCalculationFragment() {
        val action =
            ScanFragmentDirections.actionScanFragmentToCalculatingFragment()
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

    private fun restartReading() {
        monitorViewModel.resetTimer()
        badImageCounter = 0
        chart.data?.clearValues()
    }

    private val handleAcceleration = fun() {
        if (monitorViewModel.isProcessing) {
            Timber.i("Moving too much!")
            showLongToast("Moving too much!")
            restartReading()
        }
    }

    fun calculateDynamicBPM(meanList: List<Double>, timeStamp: List<Int>): Int {
        val fp = FindPeak(meanList.toDoubleArray())
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

    fun updateDynamicHeartRate(bpm: Int) {
        postOnMainLooper {
            var heartRateStr = "-- bpm"
            if (bpm >= 0) {
                heartRateStr = "$bpm bpm"
            }
            binding.heartRate.text = heartRateStr
        }
    }
}