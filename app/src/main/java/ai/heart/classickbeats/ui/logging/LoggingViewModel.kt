package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.model.Date
import ai.heart.classickbeats.model.Time
import ai.heart.classickbeats.model.entity.*
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
class LoggingViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    var apiError: String? = null

    private var _loggingData: List<BaseLogEntity>? = null
    val loggingData: List<BaseLogEntity>?
        get() = _loggingData

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

    private val _navigateToLoggingHome = MutableLiveData<Event<Boolean>>()
    val navigateToLoggingHome: LiveData<Event<Boolean>> = _navigateToLoggingHome
    private fun navigateBack() {
        _navigateToLoggingHome.postValue(Event(true))
    }

    private val _showLoading = MutableLiveData(Event(false))
    val showLoading: LiveData<Event<Boolean>> = _showLoading
    fun setShowLoadingTrue() {
        _showLoading.postValue(Event(true))
    }

    private val _reloadScreen = MutableLiveData<Event<Unit>>()
    val reloadScreen: LiveData<Event<Unit>> = _reloadScreen
    private fun reloadLoggingHomeScreen() {
        _reloadScreen.postValue(Event(Unit))
    }

    fun setShowLoadingFalse() {
        _showLoading.postValue(Event(false))
    }

    fun getLoggingData() {
        viewModelScope.launch {
            setShowLoadingTrue()
            val response = recordRepository.getLoggingData()
            if (response.succeeded) {
                _loggingData = response.data
            } else {
                apiError = response.error
            }
            reloadLoggingHomeScreen()
            setShowLoadingFalse()
        }
    }

    fun uploadBloodPressureEntry(systolic: Int, diastolic: Int, notes: String? = null) {
        viewModelScope.launch {
            setShowLoadingTrue()
            val timeStamp = getLogTimeStampString()
            val bpLogEntity = BpLogEntity(
                systolic = systolic,
                diastolic = diastolic,
                timeStamp = timeStamp,
                note = notes
            )
            recordRepository.recordBloodPressure(bpLogEntity)
            navigateBack()
        }
    }

    fun uploadGlucoseLevelEntry(glucoseLevel: Int, tag: Int, notes: String? = null) {
        viewModelScope.launch {
            setShowLoadingTrue()
            val timeStamp = getLogTimeStampString()
            val glucoseLogEntity = GlucoseLogEntity(
                glucoseLevel = glucoseLevel,
                tag = tag,
                timeStamp = timeStamp,
                note = notes
            )
            recordRepository.recordGlucoseLevel(glucoseLogEntity)
            navigateBack()
        }
    }

    fun uploadWaterIntakeEntry(quantity: Float, notes: String? = null) {
        viewModelScope.launch {
            setShowLoadingTrue()
            val timeStamp = getLogTimeStampString()
            val waterLogEntity =
                WaterLogEntity(quantity = quantity, timeStamp = timeStamp, note = notes)
            recordRepository.recordWaterIntake(waterLogEntity)
            navigateBack()
        }
    }

    fun uploadWeightEntry(weight: Float, notes: String? = null) {
        viewModelScope.launch {
            setShowLoadingTrue()
            val timeStamp = getLogTimeStampString()
            val weightLogEntity =
                WeightLogEntity(weight = weight, timeStamp = timeStamp, note = notes)
            recordRepository.recordWeight(weightLogEntity)
            navigateBack()
        }
    }

    private fun getLogTimeStampString(): String {
        val date =
            selectedLogDate.value?.peekContent() ?: throw Exception("selected date is null")
        val time =
            selectedLogTime.value?.peekContent() ?: Time(0, 0)
        return "${date.year}-${date.month}-${date.day} ${time.hourOfDay}:${time.minute}"
    }
}