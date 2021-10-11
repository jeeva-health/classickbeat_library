package ai.heart.classickbeats.ui.ppg.viewmodel

import ai.heart.classickbeats.domain.GetRecentPpgScanHistoryDataByCountUseCase
import ai.heart.classickbeats.domain.GetRecentPpgScanHistoryDataByDurationUseCase
import ai.heart.classickbeats.domain.prefs.FirstScanCompleteActionUseCase
import ai.heart.classickbeats.domain.prefs.FistScanCompletedUseCase
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.util.getDateAddedBy
import ai.heart.classickbeats.shared.util.toDbFormatString
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@HiltViewModel
class ScanViewModel @Inject constructor(
    private val firstScanCompleteActionUseCase: FirstScanCompleteActionUseCase,
    private val firstScanCompletedUseCase: FistScanCompletedUseCase,
    private val getPpgScanHistoryDataByCountUseCase: GetRecentPpgScanHistoryDataByCountUseCase,
    private val getPpgScanHistoryDataByDurationUseCase: GetRecentPpgScanHistoryDataByDurationUseCase
) : ViewModel() {

    private val _navigateToScanFragment = MutableLiveData<Event<Unit>>()
    val navigateToScanFragment: LiveData<Event<Unit>> = _navigateToScanFragment

    val historyScanData = mutableListOf<PPGEntity>()

    val isFirstTimeScanCompleted = liveData {
        val result = firstScanCompletedUseCase(Unit)
        Timber.i("isFirstTimeScanCompleted: ${result.data}")
        if (result.data == false) {
            emit(Event(false))
        } else {
            emit(Event(true))
        }
    }

    fun setFirstScanCompleted() {
        viewModelScope.launch {
            firstScanCompleteActionUseCase(true)
            Timber.i("firstScanCompletedSet")
        }
    }

    fun getStartedClick() {
        viewModelScope.launch {
            firstScanCompleteActionUseCase(true)
            _navigateToScanFragment.postValue(Event(Unit))
        }
    }

    fun getPpgHistoryDataByCount(limit: Int) {
        viewModelScope.launch {
            val result = getPpgScanHistoryDataByCountUseCase(limit)
            result.data?.let { processScanHistoryData(it) }
        }
    }

    fun getPpgHistoryDataByDuration(daysDiff: Int) {
        viewModelScope.launch {
            val startDate = Date().getDateAddedBy(daysDiff)
            val startDateStr = startDate.toDbFormatString()
            val result = getPpgScanHistoryDataByDurationUseCase(startDateStr)
            result.data?.let { processScanHistoryData(it) }
        }
    }

    private fun processScanHistoryData(scanData: List<PPGEntity>) {
        Timber.i("scanData count: ${scanData.size}")
        historyScanData.clear()
        historyScanData.addAll(scanData)
    }
}