package ai.heart.classickbeats.monitor

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit

class MonitorViewModel @ViewModelInject constructor() : ViewModel() {

    private var timer: CountDownTimer? = null

    var isTimerRunning: Boolean = false
        private set

    val timerProgress = MutableLiveData<Int>(30)

    fun startTimer(timeLeftInMillis: Long = 30 * DateUtils.SECOND_IN_MILLIS) {
        timer = object : CountDownTimer(timeLeftInMillis, TimeUnit.SECONDS.toMillis(1)) {
            override fun onFinish() {
                isTimerRunning = false
                timerProgress.postValue(0)
            }

            override fun onTick(millisUntilFinished: Long) {
                timerProgress.postValue((millisUntilFinished / DateUtils.SECOND_IN_MILLIS).toInt())
            }
        }
        timer?.start()
        isTimerRunning = true
    }

    fun endTimer() {
        timer?.cancel()
    }
}