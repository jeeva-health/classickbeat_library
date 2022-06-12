package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.data.user.UserRepository
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.shared.result.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userData = MutableLiveData<Event<User>>()
    val userData: LiveData<Event<User>> = _userData
    private fun setUserDate(user: User) {
        _userData.postValue(Event(user))
    }

    private val _showLoading = MutableLiveData(Event(false))
    val showLoading: LiveData<Event<Boolean>> = _showLoading
    fun setShowLoadingTrue() {
        _showLoading.postValue(Event(true))
    }

    fun setShowLoadingFalse() {
        _showLoading.postValue(Event(false))
    }

    private val _feedbackSubmitted = MutableLiveData(false)
    val feedbackSubmitted: LiveData<Boolean> = _feedbackSubmitted
    fun resetFeedbackSubmitted() {
        _feedbackSubmitted.postValue(false)
    }

    fun getUser() {
        viewModelScope.launch {
            userRepository.getUserAsFlow().collectLatest { user: User? ->
                user?.let { setUserDate(it) }
            }
        }
    }

    fun submitFeedback(feedback: String) {
        viewModelScope.launch {
            setShowLoadingTrue()
            userRepository.submitFeedback(feedback)
            setShowLoadingFalse()
            _feedbackSubmitted.postValue(true)
        }
    }

    fun getReferralGemReward(): Pair<Int, Int> {
        return Pair(10, 5)
    }

    fun getProPricing(): Triple<Int, Int, Int> {
        return Triple(2500, 208, 350)
    }
}
