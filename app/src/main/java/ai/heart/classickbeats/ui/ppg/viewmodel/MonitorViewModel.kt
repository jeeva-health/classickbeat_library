package ai.heart.classickbeats.ui.ppg.viewmodel

import ai.heart.classickbeats.compute.MAPmodeling
import ai.heart.classickbeats.compute.ProcessingData
import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.data.user.UserRepository
import ai.heart.classickbeats.domain.CameraReading
import ai.heart.classickbeats.model.*
import ai.heart.classickbeats.model.Constants.SCAN_DURATION
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.succeeded
import ai.heart.classickbeats.shared.util.computeAge
import ai.heart.classickbeats.shared.util.toDate
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.github.psambit9791.jdsp.misc.UtilMethods.argmax
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
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

    private val movAvgSmall = mutableListOf<Double>()
    private val movAvgLarge = mutableListOf<Double>()

    private val ibiList = mutableListOf<Double>()
    private val qualityList = mutableListOf<Double>()
    private val leveledSignalList = mutableListOf<List<Double>>()

    val fps = 30

    // Keep window sizes odd
    val smallWindow = fps / 10
    val largeWindow = fps + 1
    val offset = (largeWindow + smallWindow - 1) / 2

    var timeListSplitSize: Int = 0
    var centeredSignalSplitSize: Int = 0

    @Volatile
    var ppgId: Long = -1

    private val _dynamicGraphCoordinates = MutableLiveData<Event<Pair<Int, Double>>>()
    val dynamicGraphCoordinates: LiveData<Event<Pair<Int, Double>>> = _dynamicGraphCoordinates

    val outputComputed = MutableLiveData(Event(false))

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
        movAvgLarge.clear()
        movAvgSmall.clear()
    }

    var leveledSignal: List<Double>? = null

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
            val timeOffset = smallWindow + largeWindow - 2
            val timeListSize = timeList.size
            val centeredSignalSize = centeredSignal.size
            calculateResultSplit(
                timeList.subList(timeListSplitSize - timeOffset, timeListSize).toList(),
                centeredSignal.subList(centeredSignalSplitSize, centeredSignalSize).toList()
            )
            calculateSplitCombinedResult()
        }
    }

    private suspend fun uploadRawData() {
        val timeStamp0 = timeList.firstOrNull() ?: 0
        val ppgEntity = PPGEntity(
            rMeans = mean1List.toList().map { String.format("%.4f", it).toFloat() },
            gMeans = mean2List.toList().map { String.format("%.4f", it).toFloat() },
            bMeans = mean3List.toList().map { String.format("%.4f", it).toFloat() },
            cameraTimeStamps = timeList.toList().map { it.toLong() - timeStamp0.toLong() },
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
            val x = -1.0 * (movAvgSmall[movAvgSmall.size - largeWindowOffset] - movAvgLarge.last())
            centeredSignal.add(x)

            _dynamicGraphCoordinates.postValue(Event(Pair(centeredSignal.size, x)))
        }
    }

    suspend fun calculateResultSplit(timeList: List<Int>, centeredSignalList: List<Double>) =
        withContext(Dispatchers.Default) {
            val windowSize = 101

            timeListSplitSize = timeList.size
            centeredSignalSplitSize = centeredSignalList.size

            val smallWindowOffset = smallWindow - 1
            val largeWindowOffset = (largeWindow - 1) / 2
            val leveledSignal = ProcessingData.computeLeveledSignal(
                timeList = timeList,
                centeredSignalList = centeredSignalList,
                smallWindowOffset = smallWindowOffset,
                largeWindowOffset = largeWindowOffset,
                windowSize = windowSize
            )
            val (_ibiList, _quality) = ProcessingData.calculateIbiListAndQuality(
                leveledSignal,
                fInterp
            )

            leveledSignalList.add(leveledSignal)
            ibiList.addAll(_ibiList)
            qualityList.add(_quality)
        }

    private suspend fun calculateSplitCombinedResult() {
        val age = user?.dob?.toDate()?.computeAge() ?: throw Exception("Unable to compute age")
        val gender = if (user?.gender == Gender.MALE) 0 else 1

        val (meanNN, sdnn, rmssd, pnn50, ln) = ProcessingData.calculatePulseStats(ibiList)

        // TODO(Ritesh: store both leveled signal)

        // TODO(Harsh: combine quality parameter)
        val qualityPercent = ProcessingData.qualityPercent(qualityList[0])
        Timber.i("QualityPercent: $qualityPercent")

        val bpm = (60 * 1000.0) / meanNN

        // This will be used by View
        this@MonitorViewModel.leveledSignal = leveledSignalList[0]

        val mapModeling = MAPmodeling()

        //@RITESH: In cAge and gender parameters in lines 152 and 155,
        // input the age and gender of the user, respectively
        // gender = 0 is male and 1 is female

        val binProbsMAP =
            mapModeling.bAgePrediction(age.toDouble(), gender, meanNN, sdnn, rmssd, pnn50)
                .toDoubleArray()
        // bAgeBin goes from 0 to 5
        val bAgeBin = argmax(binProbsMAP, false)

        // First and second indices is for sedantry and active probabilities, respectively.
        val activeSedantryProb = mapModeling.activeSedantryPrediction(27.0, meanNN, rmssd)
        val sedRatioLog = kotlin.math.log10(activeSedantryProb[0] / activeSedantryProb[1])
        // sedStars = 0 implies the person is fully active and the sedRatioLog is small
        // sedStars =  6 implies all stars related to sedantry lifestyle be highlighted
        // (sedRatioLog, sedStars) = (-1,0); (-0.7,1); (-0.3,2); (0,3); (0.3,4); (0.7, 5); (1, 6)
        val sedStars = if (sedRatioLog < -1.0)
            0
        else if (sedRatioLog >= -1.0 && sedRatioLog < -0.5)
            1
        else if (sedRatioLog >= -0.5 && sedRatioLog < -0.15)
            2
        else if (sedRatioLog >= -0.15 && sedRatioLog < 0.15)
            3
        else if (sedRatioLog >= 0.15 && sedRatioLog < 0.5)
            4
        else
            5

        val sdnnListResponse = recordRepository.getSdnnList()
        val stressOutput = if (sdnnListResponse.succeeded) {
            val dataArray = sdnnListResponse.data!!.toDoubleArray()
            mapModeling.stressLevelPrediction(dataArray, sdnn)
        } else {
            1
        }

        val ppgEntity = PPGEntity(
            filteredRMeans = leveledSignal?.map { String.format("%.4f", it).toDouble() },
            hr = String.format("%.4f", bpm).toFloat(),
            meanNN = String.format("%.4f", meanNN).toFloat(),
            sdnn = String.format("%.4f", sdnn).toFloat(),
            pnn50 = String.format("%.4f", pnn50).toFloat(),
            rmssd = String.format("%.4f", rmssd).toFloat(),
            ln = String.format("%.4f", ln).toFloat(),
            quality = String.format("%.2f", qualityPercent).toFloat(),
            binProbsMAP = binProbsMAP.toList().map { String.format("%.8f", it).toFloat() },
            bAgeBin = bAgeBin,
            activeSedentaryProb = activeSedantryProb.toList()
                .map { String.format("%.8f", it).toFloat() },
            sedRatioLog = String.format("%.8f", qualityList[0]).toFloat(),
            sedStars = sedStars,
            stressLevel = stressOutput
        )

        recordRepository.updatePPG(ppgId, ppgEntity)

        clearGlobalData()
        outputComputed.postValue(Event(true))
    }

    private fun clearGlobalData() {
        mean1List.clear()
        mean2List.clear()
        mean3List.clear()
        timeList.clear()
        centeredSignal.clear()
    }

    fun uploadScanSurvey(sleepRating: Int, moodRating: Int, healthRating: Int, scanState: String) {
        viewModelScope.launch {
            val ppgEntity = PPGEntity(
                sleepRating = sleepRating,
                moodRating = moodRating,
                healthRating = healthRating,
                scanState = scanState
            )
            recordRepository.updatePPG(ppgId, ppgEntity)
        }
    }
}