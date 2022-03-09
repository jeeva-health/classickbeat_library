package ai.heart.classickbeats.utils

import ai.heart.classickbeats.*
import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
fun Fragment.setLightStatusBar() {
    val window = requireActivity().window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.setSystemBarsAppearance(
            APPEARANCE_LIGHT_STATUS_BARS,
            APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
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

fun Fragment.showLongToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.showShortToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showSnackbar(message: String, isShort: Boolean = true) {
    requireActivity().showSnackbar(message, isShort)
}

fun Fragment.hideKeyboard(view: View) {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.postOnMainLooper(call: () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        if (isResumed) {
            call.invoke()
        }
    }
}

fun Fragment.showBottomNavigation() {
    requireActivity().showBottomNavigation()
}

fun Fragment.hideBottomNavigation() {
    requireActivity().hideBottomNavigation()
}

fun Fragment.showLoadingBar() {
    requireActivity().showLoadingBar()
}

fun Fragment.hideLoadingBar() {
    requireActivity().hideLoadingBar()
}

fun Fragment.getContextColor(colorId: Int): Int {
    return requireContext().getColor(colorId)
}
