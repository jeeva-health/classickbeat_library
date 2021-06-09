package ai.heart.classickbeats.ui.splash

import ai.heart.classickbeats.shared.domain.prefs.AuthTokenUseCase
import ai.heart.classickbeats.shared.domain.prefs.OnBoardingCompletedUseCase
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(
    private val onBoardingCompletedUseCase: OnBoardingCompletedUseCase,
    private val authTokenUseCase: AuthTokenUseCase
) : ViewModel() {

    val launchDestination = liveData {
        delay(3000)
        val result = onBoardingCompletedUseCase(Unit)
        if (result.data == false) {
            emit(Event(LaunchDestination.ONBOARDING))
        } else {
            val authTokenResult = authTokenUseCase(Unit)
            val accessToken = authTokenResult.data?.accessToken
            if (accessToken?.isNotBlank() == true) {
                emit(Event(LaunchDestination.HOME_SCREEN))
            } else {
                emit(Event(LaunchDestination.SIGNUP))
            }
        }
    }
}

enum class LaunchDestination {
    ONBOARDING,
    SIGNUP,
    HOME_SCREEN
}
