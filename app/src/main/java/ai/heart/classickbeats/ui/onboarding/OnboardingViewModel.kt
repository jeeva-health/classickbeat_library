package ai.heart.classickbeats.ui.onboarding

import ai.heart.classickbeats.shared.domain.prefs.OnboardingCompleteActionUseCase
import ai.heart.classickbeats.shared.result.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingCompleteActionUseCase: OnboardingCompleteActionUseCase,
) : ViewModel() {

    private val _navigateToSignUpFragment = MutableLiveData<Event<Unit>>()
    val navigateToSignUpFragment: LiveData<Event<Unit>> = _navigateToSignUpFragment

    fun getStartedClick() {
        viewModelScope.launch {
            onboardingCompleteActionUseCase.invoke(true)
            _navigateToSignUpFragment.postValue(Event(Unit))
        }
    }
}


