package ai.heart.classickbeats.utils

import ai.heart.classickbeats.MainActivity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun Fragment.showLongToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.showShortToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.hideKeyboard(view: View) {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.postOnMainLooper(call: () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        call.invoke()
    }
}

fun Fragment.showLoadingBar() {
    (requireActivity() as MainActivity).showLoadingBar()
}

fun Fragment.hideLoadingBar() {
    (requireActivity() as MainActivity).hideLoadingBar()
}
