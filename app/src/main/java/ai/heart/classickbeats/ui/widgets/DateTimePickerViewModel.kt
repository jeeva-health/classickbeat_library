package ai.heart.classickbeats.ui.widgets

import ai.heart.classickbeats.model.Date
import ai.heart.classickbeats.model.Time
import ai.heart.classickbeats.shared.result.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DateTimePickerViewModel @Inject constructor() : ViewModel() {

    private val _selectedLogTime = MutableLiveData<Event<Time>>()
    val selectedLogTime: LiveData<Event<Time>> = _selectedLogTime
    fun setLogTime(time: Time) {
        _selectedLogTime.postValue(Event(time))
    }

    private val _selectedLogDate = MutableLiveData<Event<Date>>()
    val selectedLogDate: LiveData<Event<Date>> = _selectedLogDate
    fun setLogDate(date: Date) {
        _selectedLogDate.postValue(Event(date))
    }
}