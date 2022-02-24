package ai.heart.classickbeats.ui.common

import ai.heart.classickbeats.shared.result.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConfirmationViewModel @Inject constructor() : ViewModel() {

    private val _negativeEvent = MutableLiveData(Event(false))
    val negativeEvent: LiveData<Event<Boolean>> = _negativeEvent

    private val _positiveEvent = MutableLiveData(Event(false))
    val positiveEvent: LiveData<Event<Boolean>> = _positiveEvent

    private val _dismissEvent = MutableLiveData(Event(false))
    val dismissEvent: LiveData<Event<Boolean>> = _dismissEvent

    fun onNegative() {
        _negativeEvent.postValue(Event(true))
    }

    fun onPositive() {
        _positiveEvent.postValue(Event(true))
    }

    fun dismiss() {
        _dismissEvent.postValue(Event(true))
    }
}
