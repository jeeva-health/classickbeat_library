package ai.heart.classickbeats.ui.ppg.viewmodel

import ai.heart.classickbeats.compute.ProcessingData
import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.data.user.UserRepository
import ai.heart.classickbeats.domain.CameraReading
import ai.heart.classickbeats.model.Constants.SCAN_DURATION
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    var user: User? = null

    private var timer: CountDownTimer? = null

    private val mean1List = mutableListOf<Double>()
    private val mean2List = mutableListOf<Double>()
    private val mean3List = mutableListOf<Double>()
    val centeredSignal = mutableListOf<Double>()
    val timeList = mutableListOf<Int>()

    private val xAccelerationList = mutableListOf<Float>()
    private val yAccelerationList = mutableListOf<Float>()
    private val zAccelerationList = mutableListOf<Float>()
    private val accelerationTimeList = mutableListOf<Long>()

    private val movAvgSmall = mutableListOf<Double>()
    private val movAvgLarge = mutableListOf<Double>()

    val fps = 30

    // Keep window sizes odd
    val smallWindow = fps / 10
    val largeWindow = fps + 1
    val offset = (largeWindow + smallWindow - 1) / 2

    @Volatile
    var ppgId: Long = -1L

    private val _dynamicGraphCoordinates = MutableLiveData<Event<Pair<Int, Double>>>()
    val dynamicGraphCoordinates: LiveData<Event<Pair<Int, Double>>> = _dynamicGraphCoordinates

    var isTimerRunning: Boolean = false
        private set

    @Volatile
    var isProcessing: Boolean = false

    val timerProgress = MutableLiveData(Event(SCAN_DURATION))

    fun startTimer(timeLeftInMillis: Long = SCAN_DURATION * DateUtils.SECOND_IN_MILLIS) {
        timer?.cancel()
        timer = object : CountDownTimer(timeLeftInMillis, TimeUnit.SECONDS.toMillis(1)) {
            override fun onFinish() {
                isTimerRunning = false
                timerProgress.postValue(Event(0))
            }

            override fun onTick(millisUntilFinished: Long) {
                timerProgress.postValue(Event((millisUntilFinished / DateUtils.SECOND_IN_MILLIS).toInt()))
            }
        }
        timer?.start()
        isTimerRunning = true
    }

    fun endTimer() {
        timer?.cancel()
        timer = null
        isTimerRunning = false
    }

    fun resetReadings() {
        mean1List.clear()
        mean2List.clear()
        mean3List.clear()

        centeredSignal.clear()
        timeList.clear()

        xAccelerationList.clear()
        yAccelerationList.clear()
        zAccelerationList.clear()
        accelerationTimeList.clear()

        movAvgLarge.clear()
        movAvgSmall.clear()
    }

    // Make sure 1000/f_interp is an integer
    val fInterp = 100.0

    fun fetchUser() {
        viewModelScope.launch {
            userRepository.getUserAsFlow().collectLatest {
                user = it
            }
        }
    }

    fun endScanHandling() {
        viewModelScope.launch(Dispatchers.Default) {
            endTimer()
            uploadRawData()
        }
    }

    private suspend fun uploadRawData() {
        val timeStamp0 = timeList.firstOrNull() ?: 0
        val accelerationTime0 = accelerationTimeList.firstOrNull() ?: 0
        val ppgEntity = PPGEntity(
            rMeans = mean1List.toList().map { String.format("%.4f", it).toFloat() },
            gMeans = mean2List.toList().map { String.format("%.4f", it).toFloat() },
            bMeans = mean3List.toList().map { String.format("%.4f", it).toFloat() },
            cameraTimeStamps = timeList.toList().map { it.toLong() - timeStamp0.toLong() },
            xAcceleration = xAccelerationList.toList().map { String.format("%.2f", it).toFloat() },
            yAcceleration = yAccelerationList.toList().map { String.format("%.2f", it).toFloat() },
            zAcceleration = zAccelerationList.toList().map { String.format("%.2f", it).toFloat() },
            accelerationTimestamp = accelerationTimeList.toList().map { it - accelerationTime0 }
        )
        val result = recordRepository.recordPPG(ppgEntity)
        Timber.i("TrackTime: upload raw upload completed id: ${result.data}")
        ppgId = result.data ?: -1
    }

    fun addFrameDataToList(cameraReading: CameraReading) {
        val red = cameraReading.red
        val green = cameraReading.green
        val blue = cameraReading.blue
        val timeStamp = cameraReading.timeStamp

        mean1List.add(red)
        mean2List.add(green)
        mean3List.add(blue)
        timeList.add(timeStamp)
    }

    fun addAccelerationReading(x: Float, y: Float, z: Float, timestamp: Long) {
        xAccelerationList.add(x)
        yAccelerationList.add(y)
        zAccelerationList.add(z)
        accelerationTimeList.add(timestamp)
    }

    fun calculateCenteredSignal() {
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

            _dynamicGraphCoordinates.postValue(Event(Pair(centeredSignal.size, x)))
        }
    }

    fun uploadScanSurvey(sleepRating: Int, moodRating: Int, scanState: String) {
        viewModelScope.launch {
            if (ppgId != -1L) {
                val ppgEntity = PPGEntity(
                    sleepRating = sleepRating,
                    moodRating = moodRating,
                    scanState = scanState
                )
                recordRepository.updatePPG(ppgId, ppgEntity)
            }
        }
    }
}
