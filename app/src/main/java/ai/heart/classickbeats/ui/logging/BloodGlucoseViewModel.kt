package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.data.logging.LoggingRepository
import ai.heart.classickbeats.domain.BloodGlucose
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.ui.logging.model.BloodGlucoseViewData
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
class BloodGlucoseViewModel @Inject constructor(
    private val loggingRepository: LoggingRepository
) : ViewModel() {

    var apiError: String? = null

    private val _showLoading = MutableLiveData(Event(false))
    val showLoading: LiveData<Event<Boolean>> = _showLoading

    private val _navigateBack = MutableLiveData(Event(false))
    val navigateBack: LiveData<Event<Boolean>> = _navigateBack

    val defaultData = BloodGlucoseViewData(
        timeString = "2:30 PM",
        dateString = "Today",
        reading = 80,
        tag = BloodGlucose.TAG.FASTING,
        note = null
    )

    fun navigateBack() {
        _navigateBack.postValue(Event(true))
    }

    fun uploadGlucoseLevelEntry(data: BloodGlucoseViewData) {
        viewModelScope.launch {
            _showLoading.postValue(Event(true))
            val bloodGlucose = BloodGlucose(
                time = TODO(),
                reading = data.reading.toFloat(),
                tag = data.tag,
                note = data.note
            )
            loggingRepository.recordGlucoseLevel(bloodGlucose)
            _showLoading.postValue(Event(false))
            apiError = null
        }
    }
}
