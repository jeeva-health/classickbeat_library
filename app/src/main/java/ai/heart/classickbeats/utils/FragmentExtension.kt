package ai.heart.classickbeats.utils

import android.os.Build
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.fragment.app.Fragment

@Suppress("DEPRECATION")
fun Fragment.setLightStatusBar() {
    val window = requireActivity().window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.setSystemBarsAppearance(
            APPEARANCE_LIGHT_STATUS_BARS,
            APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        window.decorView.systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}

@Suppress("DEPRECATION")
fun Fragment.setDarkStatusBar() {
    val window = requireActivity().window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window?.insetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
    } else {
        window?.decorView?.systemUiVisibility = 0
    }
}
