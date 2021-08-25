package ai.heart.classickbeats.ui.ppg

import ai.heart.classickbeats.compute.ProcessingData
import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.data.user.UserRepository
import ai.heart.classickbeats.domain.CameraReading
import ai.heart.classickbeats.model.Constants.SCAN_DURATION
import ai.heart.classickbeats.model.Gender
import ai.heart.classickbeats.model.PPGData
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.util.computeAge
import ai.heart.classickbeats.shared.util.toDate
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    var user: User? = null

    var scanResult: PPGData.ScanResult? = null

    private var timer: CountDownTimer? = null

    private val mean1List = mutableListOf<Double>()
    private val mean2List = mutableListOf<Double>()
    private val mean3List = mutableListOf<Double>()
    val centeredSignal = mutableListOf<Double>()
    val timeList = mutableListOf<Int>()

    private val movAvgSmall = mutableListOf<Double>()
    private val movAvgLarge = mutableListOf<Double>()

    private val ibiList = mutableListOf<Double>()
    private val quality = mutableListOf<Double>()

    val fps = 30

    // Keep window sizes odd
    private val smallWindow = fps / 10
    private val largeWindow = fps + 1
    val offset = (largeWindow + smallWindow - 1) / 2

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
    }

    var leveledSignal: List<Double>? = null

    // Make sure 1000/f_interp is an integer
    val fInterp = 100.0

    fun fetchUser() {
        viewModelScope.launch {
            userRepository.getUser().collectLatest {
                user = it
            }
        }
    }

    fun uploadRawData() {
        viewModelScope.launch {
            val timeStamp0 = timeList.firstOrNull() ?: 0
            val ppgEntity = PPGEntity(
                rMeans = mean1List.toList().map { String.format("%.4f", it).toFloat() },
                gMeans = mean2List.toList().map { String.format("%.4f", it).toFloat() },
                bMeans = mean3List.toList().map { String.format("%.4f", it).toFloat() },
                cameraTimeStamps = timeList.toList().map { it.toLong() - timeStamp0.toLong() },
            )
            val result = recordRepository.recordPPG(ppgEntity)
            Timber.i("TrackTime: upload raw upload completed")
            ppgId = result.data ?: -1
        }
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

        if (movAvgSmall.size >= largeWindow) {
            val x = -1.0 * (movAvgSmall[movAvgSmall.size - offset] - movAvgLarge.last())
            centeredSignal.add(x)

            _dynamicGraphCoordinates.postValue(Event(Pair(centeredSignal.size, x)))
        }
    }

    fun calculateResultSplit(timeList: List<Int>, centeredSignalList: List<Double>) {
        viewModelScope.launch {
            val windowSize = 101

            val leveledSignal = ProcessingData.computeLeveledSignal(
                timeList = timeList,
                centeredSignalList = centeredSignalList,
                offset = offset,
                windowSize = windowSize
            )
            val (_ibiList, _quality) = ProcessingData.calculateIbiListAndQuality(
                leveledSignal,
                fInterp
            )

            ibiList.addAll(_ibiList)
            quality.add(_quality)
        }
    }

    fun calculateSplitCombinedResult() {
        viewModelScope.launch {

            val age = user?.dob?.toDate()?.computeAge() ?: throw Exception("Unable to compute age")
            val gender = if (user?.gender == Gender.MALE) 0 else 1

            val (meanNN, sdnn, rmssd, pnn50, ln) = ProcessingData.calculatePulseStats(ibiList)
            Timber.i("Ritesh meanNN: $meanNN, sdnn: $sdnn, rmssd: $rmssd, pnn50: $pnn50, ln: $ln")

//            // TODO(Harsh: combine quality parameter)
//            val qualityPercent = ProcessingData.qualityPercent(quality[0])
//            Timber.i("QualityPercent: $qualityPercent")
//
//            val bpm = (60 * 1000.0) / meanNN
//
//            // This will be used by View
//            this@MonitorViewModel.leveledSignal = leveledSignal
//
//            val mapModeling = MAPmodeling()
//
//            //@RITESH: In cAge and gender parameters in lines 152 and 155,
//            // input the age and gender of the user, respectively
//            // gender = 0 is male and 1 is female
//
//            val binProbsMAP =
//                mapModeling.bAgePrediction(age.toDouble(), gender, meanNN, sdnn, rmssd, pnn50)
//                    .toDoubleArray()
//            // bAgeBin goes from 0 to 5
//            val bAgeBin = argmax(binProbsMAP, false)
//
//            Timber.i("TrackTime: age prediction completed")
//
//            // First and second indices is for sedantry and active probabilities, respectively.
//            val activeSedantryProb = mapModeling.activeSedantryPrediction(27.0, meanNN, rmssd)
//            val sedRatioLog = kotlin.math.log10(activeSedantryProb[0] / activeSedantryProb[1])
//            // sedStars = 0 implies the person is fully active and the sedRatioLog is small
//            // sedStars =  6 implies all stars related to sedantry lifestyle be highlighted
//            // (sedRatioLog, sedStars) = (-1,0); (-0.7,1); (-0.3,2); (0,3); (0.3,4); (0.7, 5); (1, 6)
//            val sedStars = if (sedRatioLog < -1.0)
//                0
//            else if (sedRatioLog >= -1.0 && sedRatioLog < -0.5)
//                1
//            else if (sedRatioLog >= -0.5 && sedRatioLog < -0.15)
//                2
//            else if (sedRatioLog >= -0.15 && sedRatioLog < 0.15)
//                3
//            else if (sedRatioLog >= 0.15 && sedRatioLog < 0.5)
//                4
//            else
//                5
//
//            val activeStars = 6 - sedStars
//            val isActive = sedRatioLog < 0
//
//            Timber.i("TrackTime: sed ratio computation completed")
//
////            val stressProb = mapModeling.stressPrediction(meanNN, sdnn, rmssd)
//
//            Timber.i("BPM: $bpm, SDNN: $sdnn, RMSSD: $rmssd, PNN50: $pnn50, LN: $ln")
//            Timber.i("binProbsMAP: ${Arrays.toString(binProbsMAP)}")
//            Timber.i("Sedantry and Active Probs: ${Arrays.toString(activeSedantryProb.toDoubleArray())}")
//
//            val sdnnDataCount: Int
//            val sdnnListResponse = recordRepository.getSdnnList()
//            val stressOutput = if (sdnnListResponse.succeeded) {
//                val dataArray = sdnnListResponse.data!!.toDoubleArray()
//                sdnnDataCount = dataArray.size + 1
//                mapModeling.stressLevelPrediction(dataArray, sdnn)
//            } else {
//                sdnnDataCount = 1
//                1
//            }
//
//            Timber.i("TrackTime: stress calculation completed")
//
//            val stressResult = StressResult(stressResult = stressOutput, dataCount = sdnnDataCount)
//
//            val bioAge = BioAge.values()[bAgeBin]
//            val bioAgeResult = when {
//                age < bioAge.startRange -> -1
//                age > bioAge.endRange -> 1
//                else -> 0
//            }
        }
    }

    fun calculateResult(timeList: List<Int>, centeredSignalList: List<Double>) {
        viewModelScope.launch {
            val windowSize = 101

            val age = user?.dob?.toDate()?.computeAge() ?: throw Exception("Unable to compute age")
            val gender = if (user?.gender == Gender.MALE) 0 else 1

            val leveledSignal = ProcessingData.computeLeveledSignal(
                timeList = timeList,
                centeredSignalList = centeredSignalList,
                offset = offset,
                windowSize = windowSize
            )
            val (ibiList, quality) = ProcessingData.calculateIbiListAndQuality(
                leveledSignal,
                fInterp
            )

            val (meanNN, sdnn, rmssd, pnn50, ln) = ProcessingData.calculatePulseStats(ibiList)
            Timber.i("Ritesh meanNN: $meanNN, sdnn: $sdnn, rmssd: $rmssd, pnn50: $pnn50, ln: $ln")

//            val qualityPercent = ProcessingData.qualityPercent(quality)
//            Timber.i("QualityPercent: $qualityPercent")
//
//            val bpm = (60 * 1000.0) / meanNN
//
//            // This will be used by View
//            this@MonitorViewModel.leveledSignal = leveledSignal
//
//            val mapModeling = MAPmodeling()
//
//            //@RITESH: In cAge and gender parameters in lines 152 and 155,
//            // input the age and gender of the user, respectively
//            // gender = 0 is male and 1 is female
//
//            val binProbsMAP =
//                mapModeling.bAgePrediction(age.toDouble(), gender, meanNN, sdnn, rmssd, pnn50)
//                    .toDoubleArray()
//            // bAgeBin goes from 0 to 5
//            val bAgeBin = argmax(binProbsMAP, false)
//
//            Timber.i("TrackTime: age prediction completed")
//
//            // First and second indices is for sedantry and active probabilities, respectively.
//            val activeSedantryProb = mapModeling.activeSedantryPrediction(27.0, meanNN, rmssd)
//            val sedRatioLog = kotlin.math.log10(activeSedantryProb[0] / activeSedantryProb[1])
//            // sedStars = 0 implies the person is fully active and the sedRatioLog is small
//            // sedStars =  6 implies all stars related to sedantry lifestyle be highlighted
//            // (sedRatioLog, sedStars) = (-1,0); (-0.7,1); (-0.3,2); (0,3); (0.3,4); (0.7, 5); (1, 6)
//            val sedStars = if (sedRatioLog < -1.0)
//                0
//            else if (sedRatioLog >= -1.0 && sedRatioLog < -0.5)
//                1
//            else if (sedRatioLog >= -0.5 && sedRatioLog < -0.15)
//                2
//            else if (sedRatioLog >= -0.15 && sedRatioLog < 0.15)
//                3
//            else if (sedRatioLog >= 0.15 && sedRatioLog < 0.5)
//                4
//            else
//                5
//
//            val activeStars = 6 - sedStars
//            val isActive = sedRatioLog < 0
//
//            Timber.i("TrackTime: sed ratio computation completed")
//
////            val stressProb = mapModeling.stressPrediction(meanNN, sdnn, rmssd)
//
//            Timber.i("BPM: $bpm, SDNN: $sdnn, RMSSD: $rmssd, PNN50: $pnn50, LN: $ln")
//            Timber.i("binProbsMAP: ${Arrays.toString(binProbsMAP)}")
//            Timber.i("Sedantry and Active Probs: ${Arrays.toString(activeSedantryProb.toDoubleArray())}")
//
//            val sdnnDataCount: Int
//            val sdnnListResponse = recordRepository.getSdnnList()
//            val stressOutput = if (sdnnListResponse.succeeded) {
//                val dataArray = sdnnListResponse.data!!.toDoubleArray()
//                sdnnDataCount = dataArray.size + 1
//                mapModeling.stressLevelPrediction(dataArray, sdnn)
//            } else {
//                sdnnDataCount = 1
//                1
//            }
//
//            Timber.i("TrackTime: stress calculation completed")
//
//            val stressResult = StressResult(stressResult = stressOutput, dataCount = sdnnDataCount)
//
//            val bioAge = BioAge.values()[bAgeBin]
//            val bioAgeResult = when {
//                age < bioAge.startRange -> -1
//                age > bioAge.endRange -> 1
//                else -> 0
//            }
//
//            val ppgEntity = PPGEntity(
//                filteredRMeans = leveledSignal?.map { String.format("%.4f", it).toDouble() },
//                hr = String.format("%.4f", bpm).toFloat(),
//                meanNN = String.format("%.4f", meanNN).toFloat(),
//                sdnn = String.format("%.4f", sdnn).toFloat(),
//                pnn50 = String.format("%.4f", pnn50).toFloat(),
//                rmssd = String.format("%.4f", rmssd).toFloat(),
//                ln = String.format("%.4f", ln).toFloat(),
//                quality = String.format("%.2f", qualityPercent).toFloat(),
//                binProbsMAP = binProbsMAP.toList().map { String.format("%.8f", it).toFloat() },
//                bAgeBin = bAgeBin,
//                activeSedentaryProb = activeSedantryProb.toList()
//                    .map { String.format("%.8f", it).toFloat() },
//                sedRatioLog = String.format("%.8f", quality).toFloat(),
//                sedStars = sedStars,
//                stressLevel = stressOutput
//            )
//            recordRepository.updatePPG(ppgId, ppgEntity)
//
//            Timber.i("TrackTime: api request completed")
//
//            val currentTime = Date()
//
//            scanResult =
//                PPGData.ScanResult(
//                    bpm = bpm.toFloat(),
//                    aFib = "Not Detected",
//                    quality = qualityPercent.toFloat(),
//                    ageBin = bAgeBin,
//                    bioAgeResult = bioAgeResult,
//                    activeStar = activeStars,
//                    isActive = isActive,
//                    sdnn = sdnn.toFloat(),
//                    pnn50 = pnn50.toFloat(),
//                    rmssd = rmssd.toFloat(),
//                    stress = stressResult,
//                    filteredRMean = leveledSignal ?: emptyList(),
//                    timeStamp = currentTime
//                )
//
//            clearGlobalData()
//            outputComputed.postValue(Event(true))
//
//            Timber.i("TrackTime: output computed posted")
        }
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