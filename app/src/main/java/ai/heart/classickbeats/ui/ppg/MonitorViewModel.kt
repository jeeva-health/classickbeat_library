package ai.heart.classickbeats.ui.ppg

import ai.heart.classickbeats.compute.Filter
import ai.heart.classickbeats.compute.MAPmodeling
import ai.heart.classickbeats.compute.ProcessingData
import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.model.BioAge
import ai.heart.classickbeats.model.Gender
import ai.heart.classickbeats.model.PPGData
import ai.heart.classickbeats.model.StressResult
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.data.login.LoginRepository
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.succeeded
import ai.heart.classickbeats.shared.util.computeAge
import ai.heart.classickbeats.shared.util.toDate
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.psambit9791.jdsp.misc.UtilMethods.argmax
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val SCAN_DURATION = 63

@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    var scanResult: PPGData.ScanResult? = null

    private var timer: CountDownTimer? = null

    val mean1List = mutableListOf<Double>()
    val mean2List = mutableListOf<Double>()
    val mean3List = mutableListOf<Double>()
    val centeredSignal = mutableListOf<Double>()
    val timeList = mutableListOf<Int>()

    var ppgId: Long = -1

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
    var envelopeAverage: List<Double>? = null
    var envelope: List<Double>? = null
    var interpolatedList: List<Double>? = null
    var withoutSpikes: List<Double>? = null
    val processData = ProcessingData()
    val fps = 30

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

    fun calculateResult() {
        viewModelScope.launch {


            // TODO: cache dob and if not present do api call
            val user = loginRepository.getUser().data ?: throw Exception("User data is null")
            val age = user.dob.toDate()?.computeAge() ?: throw Exception("Unable to compute age")
            val gender = if (user.gender == Gender.MALE) 0 else 1

            val offset = 16

            Timber.i("TrackTime: Calculate started")

            Timber.i("Calculating PULSE STATS, offset $offset")
            val time = timeList.subList(offset, timeList.size - offset).toTypedArray()
            Timber.i("LIST sizes ${time.size}, ${mean1List.size}, ${centeredSignal.size}")
            assert(time.size == centeredSignal.size)
            interpolatedList = processData.interpolate(
                time,
                centeredSignal.toTypedArray()
            )
//            interpolatedList = processData.interpolate(timeList.toTypedArray(), mean1List.toTypedArray())

            Timber.i("TrackTime: Interpolated completed")

            Timber.i("Interpolated signal done")
//            withoutSpikes = processData.spikeRemover(interpolatedList!!.toTypedArray())
//            Timber.i("Spikes done")

            val filt = Filter()
            envelope = filt.hilbert(interpolatedList!!.toTypedArray())

            Timber.i("TrackTime: envelope computed")

            val windowSize2 = 101
            envelopeAverage = processData.movAvg(envelope!!.toTypedArray(), windowSize2)

            Timber.i("TrackTime: movAvg computed")

            leveledSignal = processData.leveling(
                interpolatedList!!.toTypedArray(),
                envelopeAverage!!.toTypedArray(),
                windowSize2
            )

            Timber.i("TrackTime: leveling completed")

//            filtOut = filt.chebyBandpass(leveledSignal!!.toTypedArray())
            // filtOut = filt.filtfiltChinese(leveledSignal!!.toTypedArray())
            // filtOut = filtOut!!.drop(300)

            val peaksQ = filt.peakDetection(leveledSignal!!.toTypedArray())
            val peaks = peaksQ.first
            val quality = peaksQ.second
            Timber.i("Signal Quality: $quality")

            Timber.i("TrackTime: quality computed")

            val pulseStats = processData.heartRateAndHRV(peaks, SCAN_DURATION)
            val meanNN = pulseStats[0]
            val sdnn = pulseStats[1]
            val rmssd = pulseStats[2]
            val pnn50 = pulseStats[3]
            val ln = pulseStats[4]

            Timber.i("TrackTime: pulse state computed")

            val bpm = (60 * 1000.0) / meanNN

            val mapModeling = MAPmodeling()

            //@RITESH: In cAge and gender parameters in lines 152 and 155,
            // input the age and gender of the user, respectively
            // gender = 0 is male and 1 is female

            val binProbsMAP =
                mapModeling.bAgePrediction(age.toDouble(), gender, meanNN, sdnn, rmssd, pnn50)
                    .toDoubleArray()
            // bAgeBin goes from 0 to 5
            val bAgeBin = argmax(binProbsMAP, false)

            Timber.i("TrackTime: age prediction completed")

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
//            else if (sedRatioLog >= 0.5 && sedRatioLog < 1)
//                5
//            else
//                6

            val activeStars = 6 - sedStars
            val isActive = sedRatioLog < 0

            Timber.i("TrackTime: sed ratio computation completed")

//            val stressProb = mapModeling.stressPrediction(meanNN, sdnn, rmssd)

            Timber.i("BPM: $bpm, SDNN: $sdnn, RMSSD: $rmssd, PNN50: $pnn50, LN: $ln")
            Timber.i("binProbsMAP: ${Arrays.toString(binProbsMAP)}")
            Timber.i("Sedantry and Active Probs: ${Arrays.toString(activeSedantryProb.toDoubleArray())}")
//            Timber.i("Stress Probs: ${Arrays.toString(stressProb.toDoubleArray())}")

            val qualityStr = when {
                quality <= 1e-5 -> "PERFECT Quality Recording, Good job!"
                quality <= 1e-4 -> "Good Quality Recording, Good job!"
                quality <= 1e-3 -> "Decent Quality Recording!"
                quality <= 1e-2 -> "Poor Quality Recording. Please record again!"
                else -> "Extremely poor signal quality. Please record again!"
            }

            val qualityPercent = processData.qualityPercent(quality)
            Timber.i("QualityPERCENT: $qualityPercent")

            val sdnnDataCount: Int
            val sdnnListResponse = recordRepository.getSdnnList()
            val stressOutput = if (sdnnListResponse.succeeded) {
                val dataArray = sdnnListResponse.data!!.toDoubleArray()
                sdnnDataCount = dataArray.size + 1
                mapModeling.stressLevelPrediction(dataArray, sdnn)
            } else {
                sdnnDataCount = 1
                1
            }

            Timber.i("TrackTime: stress calculation completed")

            val stressResult = StressResult(stressResult = stressOutput, dataCount = sdnnDataCount)

            val bioAge = BioAge.values()[bAgeBin]
            val bioAgeResult = when {
                age < bioAge.startRange -> -1
                age > bioAge.endRange -> 1
                else -> 0
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
                activeSedantryProb = activeSedantryProb.toList()
                    .map { String.format("%.8f", it).toFloat() },
                sedRatioLog = String.format("%.8f", quality).toFloat(),
                sedStars = sedStars,
                stressLevel = stressOutput
            )
            recordRepository.updatePPG(ppgId, ppgEntity)

            Timber.i("TrackTime: api request completed")

            val currentTime = Date()

            scanResult =
                PPGData.ScanResult(
                    bpm = bpm.toFloat(),
                    aFib = "Not Detected",
                    quality = qualityPercent.toFloat(),
                    ageBin = bAgeBin,
                    bioAgeResult = bioAgeResult,
                    activeStar = activeStars,
                    isActive = isActive,
                    sdnn = sdnn.toFloat(),
                    pnn50 = pnn50.toFloat(),
                    rmssd = rmssd.toFloat(),
                    stress = stressResult,
                    filteredRMean = leveledSignal ?: emptyList(),
                    timeStamp = currentTime
                )

            mean1List.clear()
            mean2List.clear()
            mean3List.clear()
            timeList.clear()
            centeredSignal.clear()
            outputComputed.postValue(Event(true))

            Timber.i("TrackTime: output computed posted")
        }
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