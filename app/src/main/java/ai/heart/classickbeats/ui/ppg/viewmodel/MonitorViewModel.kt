package ai.heart.classickbeats.ui.ppg.viewmodel

import ai.heart.classickbeats.model.Constants.SCAN_DURATION
import ai.heart.classickbeats.shared.result.Event
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MonitorViewModel @Inject constructor(
) : ViewModel() {

    private var timer: CountDownTimer? = null

    val centeredSignal = mutableListOf<Double>()
    val timeList = mutableListOf<Int>()

    val fps = 30

    // Keep window sizes odd
    val smallWindow = fps / 10
    val largeWindow = fps + 1
    val offset = (largeWindow + smallWindow - 1) / 2

    var isTimerRunning: Boolean = false
        private set

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

    // Make sure 1000/f_interp is an integer
    val fInterp = 100.0

    fun endScanHandling() {
        viewModelScope.launch(Dispatchers.Default) {
            endTimer()
        }
    }
}
