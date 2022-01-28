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

    private val _isDaily = MutableLiveData<Boolean>(true)
    val isDaily: LiveData<Boolean> = _isDaily
    fun setIsDaily(boolean: Boolean) {
        _isDaily.postValue(boolean)
    }

    private val _reminderSaved = MutableLiveData(Event(false))
    val reminderSaved: LiveData<Event<Boolean>> = _reminderSaved
    private fun setReminderUpdated() {
        _reminderSaved.postValue(Event(true))
    }

    private val _reminderList = MutableLiveData<List<Reminder>>(emptyList())
    val reminderList: LiveData<List<Reminder>> = _reminderList

    var selectedReminder: Reminder? = null

    private val _reminderType = MutableLiveData(Reminder.Type.PPG)
    val reminderType: LiveData<Reminder.Type> = _reminderType
    fun updateReminderType(reminderType: Reminder.Type) {
        _reminderType.postValue(reminderType)
    }

    fun addReminder(time: Time, frequency: List<Reminder.DayOfWeek>) {
        val reminder = Reminder(
            type = reminderType.value!!,
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
        }
        setReminderUpdated()
    }

    fun updateReminder(
        reminder: Reminder,
        time: Time?,
        frequency: List<Reminder.DayOfWeek>
    ) {
        val updatedReminder = Reminder(
            _id = reminder._id,
            type = reminderType.value!!,
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
        }
        setReminderUpdated()
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            setShowLoadingTrue()
            val response = reminderRepository.deleteReminder(reminder)
            if (!response.succeeded) {
                apiError = response.error
            }
            setShowLoadingFalse()
        }
    }

    fun getAllReminders() {
        viewModelScope.launch {
            setShowLoadingTrue()
            val response = reminderRepository.getReminderList()
            if (response.succeeded) {
                val list = response.data!!
                _reminderList.postValue(list)
            } else {
                apiError = response.error
            }
            setShowLoadingFalse()
        }
    }
}
