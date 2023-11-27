package ai.heart.classickbeatslib.ui.scan.ppg.viewmodel

import ai.heart.classickbeatslib.model.entity.PPGEntity
import ai.heart.classickbeatslib.shared.result.Event
import ai.heart.classickbeatslib.shared.util.getDateAddedBy
import ai.heart.classickbeatslib.shared.util.toDbFormatString
import androidx.lifecycle.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*


@ExperimentalCoroutinesApi
class ScanViewModel: ViewModel() {

    val historyScanData = mutableListOf<PPGEntity>()

    val isFirstTimeScanCompleted = liveData {
//        val result = firstScanCompletedUseCase(Unit)
        val result = true
        Timber.i("isFirstTimeScanCompleted: $result")
        if (result == false) {
            emit(Event(false))
        } else {
            emit(Event(true))
        }
    }

    fun setFirstScanCompleted() {
//        viewModelScope.launch {
//            firstScanCompleteActionUseCase(true)
//            Timber.i("firstScanCompletedSet")
//        }
    }

//    fun getStartedClick() {
//        viewModelScope.launch {
//            firstScanCompleteActionUseCase(true)
//            _navigateToScanFragment.postValue(Event(Unit))
//        }
//    }

    fun getPpgHistoryDataByCount(limit: Int) {
        viewModelScope.launch {
//            val result = getPpgScanHistoryDataByCountUseCase(limit)
//            result.data?.let { processScanHistoryData(it) }

            val result = emptyList<PPGEntity>()
            processScanHistoryData(result)
        }
    }

    fun getPpgHistoryDataByDuration(daysDiff: Int) {
        viewModelScope.launch {
            val startDate = Date().getDateAddedBy(daysDiff)
            val startDateStr = startDate.toDbFormatString()
//            val result = getPpgScanHistoryDataByDurationUseCase(startDateStr)
//            result.data?.let { processScanHistoryData(it) }
            val result = emptyList<PPGEntity>()
            processScanHistoryData(result)
        }
    }

    private fun processScanHistoryData(scanData: List<PPGEntity>) {
        Timber.i("scanData count: ${scanData.size}")
        historyScanData.clear()
        historyScanData.addAll(scanData)
    }
}