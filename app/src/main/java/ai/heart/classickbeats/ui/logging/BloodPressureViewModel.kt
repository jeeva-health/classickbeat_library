package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.data.logging.LoggingRepository
import ai.heart.classickbeats.domain.BloodPressure
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.ui.logging.model.BloodPressureViewData
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@HiltViewModel
class BloodPressureViewModel @Inject constructor(
    private val loggingRepository: LoggingRepository
) : ViewModel() {

    var apiError: String? = null

    private val _showLoading = MutableLiveData(Event(false))
    val showLoading: LiveData<Event<Boolean>> = _showLoading

    private val _navigateBack = MutableLiveData(Event(false))
    val navigateBack: LiveData<Event<Boolean>> = _navigateBack

    val defaultData = BloodPressureViewData(
        timeString = "2:30 PM",
        dateString = "Today",
        systolicLevel = 85,
        diastolicLevel = 85
    )

    val _sysValue = mutableStateOf(defaultData.systolicLevel)
    val _diaValue = mutableStateOf(defaultData.diastolicLevel)

    fun uploadPressureLevelEntry(data: BloodPressureViewData) {
        viewModelScope.launch {
            _showLoading.postValue(Event(true))
            val bloodPressure = BloodPressure(
                time = TODO(),
                systolicLevel = data.systolicLevel.toFloat(),
                diastolicLevel = data.diastolicLevel.toFloat()

            )
            loggingRepository.recordBloodPressure(bloodPressure)
            _showLoading.postValue(Event(false))
            apiError = null
        }
    }
}
