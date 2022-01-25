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
    private fun setReminderUpdated() {
        _reminderSaved.postValue(Event(true))
    }

    private val _dialogDismissed = MutableLiveData(false)
    val dialogDismissed: LiveData<Boolean> = _dialogDismissed
    fun setDialogDismissed() {
        _dialogDismissed.postValue(true)
    }

    private val localReminders = mutableListOf<Reminder>()

    var selectedReminder: Reminder? = null

    fun addReminder(name: String, time: Time, frequency: List<Reminder.DayOfWeek>) {
        val localRandomNumber = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
        val reminder = Reminder(
            _id = localRandomNumber,
            type = Reminder.Type.PPG,
            time = time,
            frequency = frequency,
            isReminderSet = true,
            isReminderActive = true
        )
        TODO("Fix add reminder")
        addReminder(reminder)
        setReminderUpdated()
    }

    fun updateReminder(
        reminder: Reminder,
        name: String,
        time: Time?,
        frequency: List<Reminder.DayOfWeek>
    ) {
        val updatedReminder = Reminder(
            _id = reminder._id,
            type = Reminder.Type.PPG,
            time = time ?: reminder.time,
            frequency = frequency,
            isReminderSet = true,
            isReminderActive = true
        )
        TODO("Fix update reminder")
        updateReminder(updatedReminder)
        setReminderUpdated()
    }

    fun deleteReminder(reminder: Reminder) {
        localReminders.removeIf { it._id == reminder._id }
        setReminderUpdated()
    }

    private fun addReminder(reminder: Reminder) {
        localReminders.add(reminder)
    }

    private fun updateReminder(reminder: Reminder) {
        val index = localReminders.indexOfFirst { it._id == reminder._id }
        if (index == -1) {
            addReminder(reminder)
        } else {
            localReminders.removeAt(index)
            localReminders.add(index, reminder)
        }
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
