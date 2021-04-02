package ai.heart.classickbeats.ui.monitor

import ai.heart.classickbeats.compute.Filter
import ai.heart.classickbeats.compute.MAPmodeling
import ai.heart.classickbeats.compute.ProcessingData
import ai.heart.classickbeats.domain.TestType
import ai.heart.classickbeats.utils.Event
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val SCAN_DURATION = 33

@HiltViewModel
class MonitorViewModel @Inject constructor() : ViewModel() {

    var hearRateResult: HeartRateResult? = null

    private var timer: CountDownTimer? = null

    val mean1List = mutableListOf<Double>()

    val mean2List = mutableListOf<Double>()

    val timeList = mutableListOf<Int>()

    var testType: TestType = TestType.HEART_RATE

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
    }

    fun resetTimer() {
        timer?.cancel()
        timer = null
        mean1List.clear()
        mean2List.clear()
        timeList.clear()
        isTimerRunning = false
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
            withoutSpikes = processData.spikeRemover(centeredSignal!!.toTypedArray())
            envelope = filt.hilbert(withoutSpikes!!.toTypedArray())
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
            val meanNN= pulseStats[0]
            val sdnn = pulseStats[1]
            val rmssd = pulseStats[2]
            val pnn50 = pulseStats[3]
            val ln = pulseStats[4]

            val bpm = (60 * 1000.0) / meanNN

            val mapModeling = MAPmodeling()
            val binProbsMAP = mapModeling.bAgePrediction(27.0, 0, meanNN, sdnn, rmssd, pnn50)

            Timber.i("BPM: $bpm, SDNN: $sdnn, RMSSD: $rmssd, PNN50: $pnn50, LN: $ln")
            Timber.i("binProbsMAP: ${Arrays.toString(binProbsMAP.toDoubleArray())}")

            val qualityStr = when {
                quality <= 1e-5 -> "PERFECT Quality Recording, Good job!"
                quality <= 1e-4 -> "Good Quality Recording, Good job!"
                quality <= 1e-3 -> "Decent Quality Recording!"
                quality <= 1e-2 -> "Poor Quality Recording. Please record again!"
                else -> "Extremely poor signal quality. Please record again!"
            }
            hearRateResult =
                HeartRateResult(bpm = bpm, hrv = sdnn, aFib = "Not Detected", quality = qualityStr)
            mean1List.clear()
            mean2List.clear()
            timeList.clear()
            outputComputed.postValue(Event(true))
        }
    }
}