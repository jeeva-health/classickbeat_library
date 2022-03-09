package ai.heart.classickbeats

import ai.heart.classickbeats.databinding.ActivityMainBinding
import android.app.Activity
import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

fun Activity.mainActivityOperation(operation: (ActivityMainBinding) -> Unit) {
    when (this) {
        is MainActivity -> {
            binding?.apply {
                operation.invoke(this)
            }
        }
        else -> {

        }
    }
}

fun Activity.checkForUpdate() {

}

fun Activity.showSnackbar(message: String, isShort: Boolean = true) {
    val length = if (isShort) {
        BaseTransientBottomBar.LENGTH_SHORT
    } else {
        BaseTransientBottomBar.LENGTH_LONG
    }
    mainActivityOperation {
        Snackbar.make(it.bottomNavigation, message, length)
            .apply { anchorView = it.bottomNavigation }.show()
    }
}

fun Activity.showBottomNavigation() {
    mainActivityOperation {
        it.bottomNavigation.visibility = View.VISIBLE
    }
}

fun Activity.hideBottomNavigation() {
    mainActivityOperation {
        it.bottomNavigation.visibility = View.GONE
    }
}

fun Activity.selectHistoryFragmentInBottomNavigation() {
    mainActivityOperation {
        if (it.bottomNavigation.selectedItemId != R.id.historyFragment) {
            it.bottomNavigation.selectedItemId = R.id.historyFragment
        }
    }
}

fun Activity.navigateToLoggingFragment() {
    mainActivityOperation {
        if (it.bottomNavigation.selectedItemId != R.id.loggingHomeFragment) {
            it.bottomNavigation.selectedItemId = R.id.loggingHomeFragment
        }
    }
}

fun Activity.navigateToHeartRateFragment() {
    mainActivityOperation {
        if (it.bottomNavigation.selectedItemId != R.id.scanFragment) {
            it.bottomNavigation.selectedItemId = R.id.scanFragment
        }
    }
}

fun Activity.navigateToWellnessFragment() {
    mainActivityOperation {
        if (it.bottomNavigation.selectedItemId != R.id.wellnessHomeFragment) {
            it.bottomNavigation.selectedItemId = R.id.wellnessHomeFragment
        }
    }
}

fun Activity.navigateToProfileFragment() {
    mainActivityOperation {
        if (it.bottomNavigation.selectedItemId != R.id.profileHomeFragment) {
            it.bottomNavigation.selectedItemId = R.id.profileHomeFragment
        }
    }
}

fun Activity.showLoadingBar() {
    Timber.i("showLoadingBar() called")
    mainActivityOperation {
        it.progressBar.visibility = View.VISIBLE
//        window?.setFlags(
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//        )
    }
}

fun Activity.hideLoadingBar() {
    Timber.i("hideLoadingBar() called")
    mainActivityOperation {
        it.progressBar.visibility = View.GONE
        //window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}
