package ai.heart.classickbeats.utils

import android.os.SystemClock
import android.view.View

class SafeClickListener(
    private var defaultInterval: Int = 2000,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {

    private var lastTimeClicked: Long = 0

    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}

fun View.setSafeOnClickListener(delay: Int = 2000, onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener(delay) {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}