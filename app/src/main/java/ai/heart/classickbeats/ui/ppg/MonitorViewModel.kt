package ai.heart.classickbeats.ui.ppg

import ai.heart.classickbeats.compute.Filter
import ai.heart.classickbeats.compute.MAPmodeling
import ai.heart.classickbeats.compute.ProcessingData
import ai.heart.classickbeats.data.LoginRepository
import ai.heart.classickbeats.model.ScanResult
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.psambit9791.jdsp.misc.UtilMethods.argmax
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val SCAN_DURATION = 33

@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    var scanResult: ScanResult? = null

    private var timer: CountDownTimer? = null

    val mean1List = mutableListOf<Double>()

    val mean2List = mutableListOf<Double>()

    val mean3List = mutableListOf<Double>()

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
        timeList.clear()
        isProcessing = false
    }

    var outputList: List<Double>? = null
    var filtOut: List<Double>? = null
    var centeredSignal: List<Double>? = null
    var leveledSignal: List<Double>? = null
    var movingAverage: List<Double>? = null
    var envelopeAverage: List<Double>? = null
    var envelope: List<Double>? = null
    var interpolatedList: List<Double>? = null
    var withoutSpikes: List<Double>? = null

    fun uploadRawData() {
        viewModelScope.launch {
            val timeStamp0 = timeList[0]
            val ppgEntity = PPGEntity(
                rMeans = mean1List.toList().map { String.format("%.4f", it).toFloat() },
                gMeans = mean2List.toList().map { String.format("%.4f", it).toFloat() },
                bMeans = mean3List.toList().map { String.format("%.4f", it).toFloat() },
                cameraTimeStamps = timeList.toList().map { it.toLong() - timeStamp0.toLong() },
            )
            val result = loginRepository.recordPPG(ppgEntity)
            ppgId = result.data ?: -1
        }
    }

    fun calculateResult() {
        viewModelScope.launch(Dispatchers.Default) {
            val windowSize = 101
            val processData = ProcessingData()
            outputList = processData.interpolate(timeList.toTypedArray(), mean1List.toTypedArray())
            outputList = processData.movAvg(outputList!!.toTypedArray(), 11)
            movingAverage = processData.movAvg(outputList!!.toTypedArray(), windowSize)
            centeredSignal = processData.centering(
                outputList!!.toTypedArray(),
                movingAverage!!.toTypedArray(),
                windowSize
            )

            val filt = Filter()

            // Uncomment to use spike remover
//            withoutSpikes = processData.spikeRemover(centeredSignal!!.toTypedArray())
//            envelope = filt.hilbert(withoutSpikes!!.toTypedArray())

            envelope = filt.hilbert(centeredSignal!!.toTypedArray())
            val windowSize2 = 101
            envelopeAverage = processData.movAvg(envelope!!.toTypedArray(), windowSize2)
            leveledSignal = processData.leveling(
                withoutSpikes!!.toTypedArray(),
                envelopeAverage!!.toTypedArray(),
                windowSize2
            )

//            filtOut = filt.chebyBandpass(leveledSignal!!.toTypedArray())
            // filtOut = filt.filtfiltChinese(leveledSignal!!.toTypedArray())
            // filtOut = filtOut!!.drop(300)

            val peaksQ = filt.peakDetection(leveledSignal!!.toTypedArray())
            val peaks = peaksQ.first
            val quality = peaksQ.second
            Timber.i("Signal Quality: $quality")

//            // Checking peaks location without filter
//            val centeredSignal2 = centeredSignal!!.drop(300)
//            val envelope2 = filt.hilbert(centeredSignal2!!.toTypedArray())
//            val envelopeAverage2 = processData.movAvg(envelope2.toTypedArray(), window)
//            val finalSignal2 = processData.leveling(centeredSignal2!!.toTypedArray(), envelopeAverage2!!.toTypedArray(), window)
//            val peaksQ2 = filt.peakDetection(finalSignal2!!.toTypedArray())
//            Timber.i("Signal 2 Quality: ${peaksQ2.second}")

            val pulseStats = processData.heartRateAndHRV(peaks, SCAN_DURATION)
            val meanNN = pulseStats[0]
            val sdnn = pulseStats[1]
            val rmssd = pulseStats[2]
            val pnn50 = pulseStats[3]
            val ln = pulseStats[4]

            val bpm = (60 * 1000.0) / meanNN

            val mapModeling = MAPmodeling()

            //@RITESH: In cAge and gender parameters in lines 152 and 155,
            // input the age and gender of the user, respectively
            // gender = 0 is male and 1 is female

            val binProbsMAP =
                mapModeling.bAgePrediction(27.0, 0, meanNN, sdnn, rmssd, pnn50).toDoubleArray()
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
            else if (sedRatioLog >= -1.0 && sedRatioLog < 0.5)
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

            scanResult =
                ScanResult(bpm = bpm, hrv = sdnn, aFib = "Not Detected", quality = qualityStr)

            val ppgEntity = PPGEntity(
                filteredRMeans = leveledSignal?.map { String.format("%.4f", it).toFloat() },
                hr = String.format("%.4f", bpm).toFloat(),
                meanNN = String.format("%.4f", meanNN).toFloat(),
                sdnn = String.format("%.4f", sdnn).toFloat(),
                pnn50 = String.format("%.4f", pnn50).toFloat(),
                ln = String.format("%.4f", ln).toFloat(),
                quality = String.format("%.8f", quality).toFloat(),
                binProbsMAP = binProbsMAP.toList().map { String.format("%.8f", it).toFloat() },
                bAgeBin = bAgeBin,
                activeSedantryProb = activeSedantryProb.toList()
                    .map { String.format("%.8f", it).toFloat() },
                sedRatioLog = String.format("%.8f", quality).toFloat(),
                sedStars = sedStars,
            )
            loginRepository.updatePPG(ppgId, ppgEntity)

            mean1List.clear()
            mean2List.clear()
            mean3List.clear()
            timeList.clear()
            outputComputed.postValue(Event(true))
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
            loginRepository.updatePPG(ppgId, ppgEntity)
        }
    }
}