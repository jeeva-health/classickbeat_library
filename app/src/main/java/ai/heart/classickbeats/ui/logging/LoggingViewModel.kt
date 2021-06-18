package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.model.Date
import ai.heart.classickbeats.model.Time
import ai.heart.classickbeats.model.entity.BpLogEntity
import ai.heart.classickbeats.model.entity.GlucoseLogEntity
import ai.heart.classickbeats.model.entity.WaterLogEntity
import ai.heart.classickbeats.model.entity.WeightLogEntity
import ai.heart.classickbeats.shared.result.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoggingViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _selectedLogDate = MutableLiveData<Event<Date>>()
    val selectedLogDate: LiveData<Event<Date>> = _selectedLogDate
    fun setLogDate(date: Date) {
        _selectedLogDate.postValue(Event(date))
    }

    private val _selectedLogTime = MutableLiveData<Event<Time>>()
    val selectedLogTime: LiveData<Event<Time>> = _selectedLogTime
    fun setLogTime(time: Time) {
        _selectedLogTime.postValue(Event(time))
    }

    fun uploadBloodPressureEntry(systolic: Int, diastolic: Int, notes: String? = null) {
        viewModelScope.launch {
            val timeStamp = getLogTimeStampString()
            val bpLogEntity = BpLogEntity(systolic, diastolic, timeStamp, notes)
            recordRepository.recordBloodPressure(bpLogEntity)
        }
    }

    fun uploadGlucoseLevelEntry(glucoseLevel: Int, tag: String, notes: String? = null) {
        viewModelScope.launch {
            val timeStamp = getLogTimeStampString()
            val glucoseLogEntity = GlucoseLogEntity(glucoseLevel, tag, timeStamp, notes)
            recordRepository.recordGlucoseLevel(glucoseLogEntity)
        }
    }

    fun uploadWaterIntakeEntry(quantity: Float, notes: String) {
        viewModelScope.launch {
            val timeStamp = getLogTimeStampString()
            val waterLogEntity = WaterLogEntity(quantity, timeStamp, notes)
            recordRepository.recordWaterIntake(waterLogEntity)
        }
    }

    fun uploadWeightEntry(weight: Float, notes: String) {
        viewModelScope.launch {
            val timeStamp = getLogTimeStampString()
            val weightLogEntity = WeightLogEntity(weight, timeStamp, notes)
            recordRepository.recordWeight(weightLogEntity)
        }
    }

    private suspend fun getLogTimeStampString(): String {
        val date =
            selectedLogDate.value?.peekContent() ?: throw Exception("selected date is null")
        val time =
            selectedLogTime.value?.peekContent() ?: throw Exception("selected time is null")
        return "${date.year}-${date.month}-${date.day} ${time.hourOfDay}:${time.minute}"
    }
}