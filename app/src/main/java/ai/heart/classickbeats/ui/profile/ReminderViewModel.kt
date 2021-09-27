package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.model.Reminder
import ai.heart.classickbeats.model.Time
import ai.heart.classickbeats.shared.result.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor() : ViewModel() {


    private val _reminderSaved = MutableLiveData(Event(false))
    val reminderSaved: LiveData<Event<Boolean>> = _reminderSaved
    private fun setReminderSavedTrue() {
        _reminderSaved.postValue(Event(true))
    }

    private val localReminders = mutableListOf<Reminder>()

    fun addReminder(name: String, time: Time, frequency: List<Reminder.DayOfWeek>) {
        val localRandomNumber = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
        val reminder = Reminder(
            _id = localRandomNumber,
            name = name,
            time = time,
            frequency = frequency,
            isReminderSet = true,
            isReminderActive = true
        )
        addReminder(reminder)
        setReminderSavedTrue()
    }

    private fun addReminder(reminder: Reminder) {
        localReminders.add(reminder)
    }

    fun getAllReminders(): List<Reminder> = localReminders

    private val _showLoading = MutableLiveData(Event(false))
    val showLoading: LiveData<Event<Boolean>> = _showLoading
    fun setShowLoadingTrue() {
        _showLoading.postValue(Event(true))
    }

    fun setShowLoadingFalse() {
        _showLoading.postValue(Event(false))
    }
}