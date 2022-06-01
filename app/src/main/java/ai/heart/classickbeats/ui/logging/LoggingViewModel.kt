package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.data.logging.LoggingRepository
import ai.heart.classickbeats.model.Date
import ai.heart.classickbeats.model.Time
import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import ai.heart.classickbeats.shared.util.toDbFormatString
import ai.heart.classickbeats.utils.LoggingUtils.getLogTimeStampString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@HiltViewModel
class LoggingViewModel @Inject constructor(
    private val loggingRepository: LoggingRepository
) : ViewModel() {

    var apiError: String? = null

    var loggingData: List<BaseLogEntity>? = null

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
            val response = loggingRepository.getLoggingData()
            if (response.succeeded) {
                loggingData = response.data
            } else {
                apiError = response.error
            }
            reloadLoggingHomeScreen()
        }
    }

    fun uploadBloodPressureEntry(
        systolic: Int,
        diastolic: Int,
        notes: String? = null,
        time: Time?,
        date: Date?
    ) {
        viewModelScope.launch {
            setShowLoadingTrue()
            val timeStamp = getLogTimeStampString(time, date)
            val bpLogEntity = BpLogEntity(
                systolic = systolic,
                diastolic = diastolic,
                timeStamp = timeStamp,
                note = notes
            )
            loggingRepository.recordBloodPressure(bpLogEntity)
            navigateBack()
        }
    }

    fun uploadWaterIntakeEntry(
        quantity: Float,
        notes: String? = null,
        time: Time?,
        date: Date?
    ) {
        viewModelScope.launch {
            setShowLoadingTrue()
            val timeStamp = getLogTimeStampString(time, date)
            val waterLogEntity =
                WaterLogEntity(quantity = quantity, timeStamp = timeStamp, note = notes)
            loggingRepository.recordWaterIntake(waterLogEntity)
            navigateBack()
        }
    }

    fun uploadWeightEntry(
        weight: Float,
        notes: String? = null,
        time: Time?,
        date: Date?
    ) {
        viewModelScope.launch {
            setShowLoadingTrue()
            val timeStamp = getLogTimeStampString(time, date)
            val weightLogEntity =
                WeightLogEntity(weight = weight, timeStamp = timeStamp, note = notes)
            loggingRepository.recordWeight(weightLogEntity)
            navigateBack()
        }
    }
}
