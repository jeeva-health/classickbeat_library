package ai.heart.classickbeats.ui.profile.reminder

import ai.heart.classickbeats.data.reminder.ReminderRepository
import ai.heart.classickbeats.model.Reminder
import ai.heart.classickbeats.model.Time
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    var apiError: String? = null

    private val _showLoading = MutableLiveData(Event(false))
    val showLoading: LiveData<Event<Boolean>> = _showLoading
    private fun setShowLoadingTrue() = _showLoading.postValue(Event(true))
    private fun setShowLoadingFalse() = _showLoading.postValue(Event(false))

    private val _isDaily = MutableLiveData(Event(true))
    val isDaily: LiveData<Event<Boolean>> = _isDaily
    fun setIsDaily(boolean: Boolean) {
        _isDaily.postValue(Event(boolean))
    }

    private val _reminderSaved = MutableLiveData(Event(false))
    val reminderSaved: LiveData<Event<Boolean>> = _reminderSaved
    private fun setReminderUpdated() {
        _reminderSaved.postValue(Event(true))
    }

    private val _reminderList = MutableLiveData<List<Reminder>>(emptyList())
    val reminderList: LiveData<List<Reminder>> = _reminderList

    private val _selectedReminder = MutableLiveData<Reminder>()
    val selectedReminder: LiveData<Reminder> = _selectedReminder

    private val _reminderType = MutableLiveData<Event<Reminder.Type>>()
    val reminderType: LiveData<Event<Reminder.Type>> = _reminderType
    fun updateReminderType(reminderType: Reminder.Type) {
        _reminderType.postValue(Event(reminderType))
    }

    fun getReminder(reminderId: Long) {
        viewModelScope.launch {
            val response = reminderRepository.getReminder(reminderId)
            if (response.succeeded) {
                val reminder = response.data!!
                _selectedReminder.postValue(reminder)
            } else {
                apiError = response.error
            }
        }
    }

    fun addReminder(time: Time, frequency: List<Reminder.DayOfWeek>) {
        val reminder = Reminder(
            type = reminderType.value?.peekContent() ?: Reminder.Type.PPG,
            time = time,
            frequency = frequency,
            isReminderSet = true,
            isReminderActive = true
        )
        viewModelScope.launch {
            setShowLoadingTrue()
            val response = reminderRepository.addReminder(reminder)
            if (!response.succeeded) {
                apiError = response.error
            }
            setShowLoadingFalse()
            setReminderUpdated()
        }
    }

    fun updateReminder(
        time: Time?,
        frequency: List<Reminder.DayOfWeek>
    ) {
        val reminder = selectedReminder.value!!
        val updatedReminder = Reminder(
            _id = reminder._id,
            type = reminderType.value?.peekContent() ?: Reminder.Type.PPG,
            time = time ?: reminder.time,
            frequency = frequency,
            isReminderSet = true,
            isReminderActive = true
        )
        viewModelScope.launch {
            setShowLoadingTrue()
            val response = reminderRepository.updateReminder(updatedReminder)
            if (!response.succeeded) {
                apiError = response.error
            }
            setShowLoadingFalse()
            setReminderUpdated()
        }
    }

    fun deleteReminder() {
        viewModelScope.launch {
            val reminder = selectedReminder.value!!
            setShowLoadingTrue()
            val response = reminderRepository.deleteReminder(reminder)
            if (!response.succeeded) {
                apiError = response.error
            }
            setShowLoadingFalse()
            setReminderUpdated()
        }
    }

    fun getAllLocalReminders(): Flow<List<Reminder>> =
        reminderRepository.getReminderLocalList()

    fun getAllReminders() {
        viewModelScope.launch {
            reminderRepository.getReminderList()
        }
    }
}
