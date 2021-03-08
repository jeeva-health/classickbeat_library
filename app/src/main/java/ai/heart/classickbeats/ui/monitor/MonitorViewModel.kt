package ai.heart.classickbeats.ui.monitor

import ai.heart.classickbeats.compute.Filter
import ai.heart.classickbeats.compute.ProcessingData
import ai.heart.classickbeats.domain.TestType
import ai.heart.classickbeats.utils.Event
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
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
    }

    fun resetTimer() {
        timer?.cancel()
        mean1List.clear()
        mean2List.clear()
        isTimerRunning = false
        isProcessing = false
    }

    var outputList: List<Double>? = null
    var filtOut: List<Double>? = null
    var centeredSignal: List<Double>? = null
    var finalSignal: List<Double>? = null

    fun calculateResult() {
        viewModelScope.launch(Dispatchers.Default) {

            val window = 50
            val processData = ProcessingData()
            outputList = processData.interpolate(timeList.toTypedArray(), mean1List.toTypedArray())
            outputList = processData.movAvg(outputList!!.toTypedArray(), 10)

            val movingAverage = processData.movAvg(outputList!!.toTypedArray(), window)
            centeredSignal = processData.centering(
                outputList!!.toTypedArray(),
                movingAverage.toTypedArray(),
                window
            )

            val filt = Filter()
            filtOut = filt.chebyBandpass(centeredSignal!!.toTypedArray())
            filtOut = filtOut!!.drop(300)
            val envelope = filt.hilbert(filtOut!!.toTypedArray())
            val envelopeAverage = processData.movAvg(envelope.toTypedArray(), window)
            finalSignal = processData.leveling(
                filtOut!!.toTypedArray(),
                envelopeAverage!!.toTypedArray(),
                window
            )

            val peaksQ = filt.peakDetection(finalSignal!!.toTypedArray())
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

            val bpmHRV = processData.heartRateAndHRV(peaks, SCAN_DURATION)
            val bpm = bpmHRV.first
            val hrv = bpmHRV.second

//            val mean1Array: Array<Double> = mean1List.toTypedArray()
//            val mean2Array: Array<Double> = mean2List.toTypedArray()
//            val timeArray: Array<Int> = timeList.toTypedArray()
//            val python: Python = Python.getInstance()
//            val filePyObject = python.getModule("HeartStats")
//            val classPyObject = filePyObject.callAttr("HeartStats")
//            val response =
//                classPyObject.callAttr("HR_stats", mean1Array, mean2Array, timeArray).asList()
//            val bpm = response[0].toDouble()
//            val hrv = response[1].toDouble()
//            val afib = when (response[2].toDouble()) {
//                0.0 -> "Not Detected"
//                1.0 -> "Possible"
//                else -> "Detected"
//            }
//            val quality = response[3].toDouble()
            val qualityStr = when {
                quality <= 1e-5 -> "PERFECT Quality Recording, Good job!"
                quality <= 1e-4 -> "Good Quality Recording, Good job!"
                quality <= 1e-3 -> "Decent Quality Recording!"
                quality <= 1e-2 -> "Poor Quality Recording. Please record again!"
                else -> "Extremely poor signal quality. Please record again!"
            }
            hearRateResult =
                HeartRateResult(bpm = bpm, hrv = hrv, aFib = "Not Detected", quality = qualityStr)
            mean1List.clear()
            mean2List.clear()
            timeList.clear()
            outputComputed.postValue(Event(true))
        }
    }
}