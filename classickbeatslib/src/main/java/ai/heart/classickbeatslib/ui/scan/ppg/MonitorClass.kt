package ai.heart.classickbeatslib.ui.scan.ppg

import ai.heart.classickbeatslib.MonitorCallback
import ai.heart.classickbeatslib.compute.ProcessingData
import ai.heart.classickbeatslib.domain.CameraReading
import ai.heart.classickbeatslib.shared.Constant.SCAN_DURATION
import ai.heart.classickbeatslib.shared.result.Event
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.CameraAccessException
import android.media.Image
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

val MainScope = CoroutineScope(Dispatchers.Main)
val IoScope = CoroutineScope(Dispatchers.IO)

class MonitorClass : ComponentActivity() {
    var scanDuration = SCAN_DURATION
    val fps = 30
    @Volatile
    var isProcessing: Boolean = false
        private set



    private val timeList = mutableListOf<Int>()
    private val xAccelerationList = mutableListOf<Float>()
    private val yAccelerationList = mutableListOf<Float>()
    private val zAccelerationList = mutableListOf<Float>()

    private val accelerationTimeList = mutableListOf<Long>()

    private val mean1List = mutableListOf<Double>()
    private val mean2List = mutableListOf<Double>()
    private val mean3List = mutableListOf<Double>()
    private val centeredSignal = mutableListOf<Double>()


    private val movAvgSmall = mutableListOf<Double>()

    private val movAvgLarge = mutableListOf<Double>()


    // Keep window sizes odd


    private var timer: CountDownTimer? = null


    // Keep window sizes odd
    private val smallWindow = fps / 10

    private val largeWindow = fps + 1
    private var pixelAnalyzer: PixelAnalyzer? = null
    private lateinit var sensorManager: SensorManager
    private var mAccelerometer: Sensor? = null


    private lateinit var accelerometerListener: AccelerometerListener



    private var imageCounter = 0

    private var badImageCounter = 0

    private var callback: MonitorCallback? = null

    private val _timerProgress = MutableLiveData(Event(scanDuration))
    val timerProgress:LiveData<Event<Int>> = _timerProgress

//    private val _dynamicGraphCoordinates = MutableLiveData<Event<Pair<Int, Double>>>()
//    val dynamicGraphCoordinates: LiveData<Event<Pair<Int, Double>>> = _dynamicGraphCoordinates
    var isTimerRunning: Boolean = false
        private set

    fun initialize(
        activity: Activity,
        callback: MonitorCallback,
        scanDuration: Int = 30
    ) {
        if (mAccelerometer == null) {

        }
        this.callback = callback
        this.scanDuration = scanDuration

        pixelAnalyzer = PixelAnalyzer(activity)
        sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometerListener = AccelerometerListener(
            accelerationHandler = handleAcceleration,
            recordValue = recordAccelerationValue
        )

//        dynamicGraphCoordinates.observe(lifeCycleOwner, EventObserver {
//            up
//            val (x, y) = it
//            RunningGraph.addEntry(chart!!, x, y)
//        })

    }


    fun startTimer(timeLeftInMillis: Long = scanDuration * DateUtils.SECOND_IN_MILLIS) {
        timer?.cancel()
        timer = object : CountDownTimer(timeLeftInMillis, TimeUnit.SECONDS.toMillis(1)) {
            override fun onFinish() {
                isTimerRunning = false
                _timerProgress.postValue(Event(0))
            }

            override fun onTick(millisUntilFinished: Long) {
                _timerProgress.postValue(Event((millisUntilFinished / DateUtils.SECOND_IN_MILLIS).toInt()))
            }
        }
        timer?.start()
        isTimerRunning = true
    }

    private fun endTimer() {
        timer?.cancel()
        timer = null
        isTimerRunning = false
    }

    private fun resetReadings() {
//        centeredSignal.clear()
        timeList.clear()

        xAccelerationList.clear()
        yAccelerationList.clear()
        zAccelerationList.clear()
        accelerationTimeList.clear()

//        movAvgLarge.clear()
//        movAvgSmall.clear()
    }

    // Make sure 1000/f_interp is an integer
    val fInterp = 100.0




    private fun addAccelerationReading(x: Float, y: Float, z: Float, timestamp: Long) {
        xAccelerationList.add(x)
        yAccelerationList.add(y)
        zAccelerationList.add(z)
        accelerationTimeList.add(timestamp)
    }

/*    private fun calculateCenteredSignal() {
        val smallAvg = ProcessingData.runningMovAvg(
            smallWindow,
            mean1List
        )
        smallAvg?.let { movAvgSmall.add(it) }

        val largeAvg = ProcessingData.runningMovAvg(
            largeWindow,
            movAvgSmall
        )
        largeAvg?.let { movAvgLarge.add(it) }

        val largeWindowOffset = (largeWindow - 1) / 2
        if (movAvgSmall.size >= largeWindow) {
            val x = movAvgSmall[movAvgSmall.size - largeWindowOffset] - movAvgLarge.last()
            centeredSignal.add(x)
//            callback?.scanCoordinateUpdate(Pair(centeredSignal.size, x))
            _dynamicGraphCoordinates.postValue(Event(Pair(centeredSignal.size, x)))
        }
    }*/

    fun startAnalyzingBeat(img: Image) {
        if (isProcessing) {
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
                        callback?.onScanStopUnexpectedly("Please place the finger on the camera and flash completely")
                    }
                    addFrameDataToList(cameraReading)
                    calculateCenteredSignal()
                }

                //Calculating dynamic BPM
                if (imageCounter % (5 * fps) == 0 && imageCounter > (6 * fps)) {
//                    lifecycleScope.launchWhenResumed {
                    CoroutineScope(Dispatchers.IO).launch {
                        val dynamicBPM = calculateDynamicBPM(centeredSignal.toList(), timeList.toList())
                        CoroutineScope(Dispatchers.Main).launch {
//                            updateDynamicHeartRate(dynamicBPM)
                            callback?.updateHeartRate(dynamicBPM)
                        }
                    }
//                    }
                }
            }
        }
    }

//    fun uploadScanSurvey(sleepRating: Int, moodRating: Int, scanState: String) {
//        viewModelScope.launch {
//            if (ppgId != -1L) {
//                val ppgEntity = PPGEntity(
//                    sleepRating = sleepRating,
//                    moodRating = moodRating,
//                    scanState = scanState
//                )
//                recordRepository.updatePPG(ppgId, ppgEntity)
//            }
//        }
//    }

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
            fInterp
        )

        val (meanNN, _, _, _, _) = ProcessingData.calculatePulseStats(ibiList)
        val bpm = (60 * 1000.0) / meanNN
        return@withContext bpm.toInt()

    }




    @SuppressLint("MissingPermission")
    fun startScanning() {
        if (isProcessing) {
            Timber.e("scanning already running")
        } else {
            resetReadings()
            isProcessing = true
            startTimer()
            callback?.onStartScanning()
        }
    }

    fun endScanning() {
        Timber.i("endScanning called")
        isProcessing = false
        imageCounter = 0
        //todo scan complete here
        callback?.onEndScanning()
    }

    private fun endIncompleteScanning(s: String) {
        isProcessing = false
        endTimer()
        imageCounter = 0
        badImageCounter = 0

        callback?.onScanStopUnexpectedly(s)

        Timber.i("endIncompleteScanning called")
        // Resets the dynamic heart rate
        callback?.updateHeartRate(-1)

    }

    private val handleAcceleration = fun() {
        if (isProcessing) {
            Timber.i("Moving too much!")
            endIncompleteScanning("Moving too much!")
        }
    }

    private val recordAccelerationValue = fun(x: Float, y: Float, z: Float, timeStamp: Long) {
        if (isProcessing) {
            addAccelerationReading(x, y, z, timeStamp)
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
//        scanViewModel.getPpgHistoryDataByCount(10)
//        scanViewModel.getPpgHistoryDataByDuration(-100)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(accelerometerListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            endTimer()
        } catch (e: CameraAccessException) {
            Timber.e(e)
        }
    }

    private fun addFrameDataToList(cameraReading: CameraReading) {
        val red = cameraReading.red
        val green = cameraReading.green
        val blue = cameraReading.blue
        val timeStamp = cameraReading.timeStamp

        mean1List.add(red)
        mean2List.add(green)
        mean3List.add(blue)
        timeList.add(timeStamp)
    }
    private fun calculateCenteredSignal() {
        val smallAvg = ProcessingData.runningMovAvg(
            smallWindow,
            mean1List
        )
        smallAvg?.let { movAvgSmall.add(it) }

        val largeAvg = ProcessingData.runningMovAvg(
            largeWindow,
            movAvgSmall
        )
        largeAvg?.let { movAvgLarge.add(it) }

        val largeWindowOffset = (largeWindow - 1) / 2
        if (movAvgSmall.size >= largeWindow) {
            val x = movAvgSmall[movAvgSmall.size - largeWindowOffset] - movAvgLarge.last()
            centeredSignal.add(x)
            callback?.scanCoordinateUpdate(Pair(centeredSignal.size, x))
//            dynamicGraphCoordinates.postValue(Event(Pair(centeredSignal.size, x)))
        }
    }
}