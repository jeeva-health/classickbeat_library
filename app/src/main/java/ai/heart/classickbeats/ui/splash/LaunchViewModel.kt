package ai.heart.classickbeats.ui.splash

import ai.heart.classickbeats.shared.result.Event
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(
) : ViewModel() {

    val launchDestination = liveData {
        delay(3000)
        emit(Event(LaunchDestination.HOME_SCREEN))
    }
}

enum class LaunchDestination {
    HOME_SCREEN
}
