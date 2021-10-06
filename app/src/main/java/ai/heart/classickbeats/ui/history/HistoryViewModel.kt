package ai.heart.classickbeats.ui.history

import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.model.HistoryItem
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.util.toDateStringWithoutTime
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    var apiError: String? = null

    private val _showLoading = MutableLiveData(Event(false))
    val showLoading: LiveData<Event<Boolean>> = _showLoading
    private fun setShowLoadingTrue() = _showLoading.postValue(Event(true))
    private fun setShowLoadingFalse() = _showLoading.postValue(Event(false))

    fun getHistoryData(): Flow<PagingData<HistoryItem>> {
        return recordRepository.getHistoryData().map { pagingData ->
            pagingData.map { baseLogEntity ->
                convertLogEntityToHistoryItem(baseLogEntity)
            }.insertSeparators { before: HistoryItem?, after: HistoryItem? ->
                insertDateSeparatorIfNeeded(before, after)
            }
        }.cachedIn(viewModelScope)
    }

    private fun convertLogEntityToHistoryItem(baseLogEntity: BaseLogEntity): HistoryItem {
        return HistoryItem.LogItem(baseLogEntity)
    }

    private fun insertDateSeparatorIfNeeded(
        leftEntity: HistoryItem?,
        rightEntity: HistoryItem?
    ): HistoryItem? {
        val leftLogEntity = (leftEntity as HistoryItem.LogItem?)?.logEntity
        val rightLogEntity = (rightEntity as HistoryItem.LogItem?)?.logEntity
        val leftDate: String? = when (leftLogEntity?.type) {
            LogType.BloodPressure -> (leftLogEntity as BpLogEntity).timeStamp
            LogType.GlucoseLevel -> (leftLogEntity as GlucoseLogEntity).timeStamp
            LogType.WaterIntake -> (leftLogEntity as WaterLogEntity).timeStamp
            LogType.Weight -> (leftLogEntity as WeightLogEntity).timeStamp
            LogType.Medicine -> (leftLogEntity as MedicineLogEntity).timeStamp
            LogType.PPG -> (leftLogEntity as PPGEntity).timeStamp
            else -> null
        }?.toDateStringWithoutTime()
        val rightDate: String? = when (rightLogEntity?.type) {
            LogType.BloodPressure -> (rightLogEntity as BpLogEntity).timeStamp
            LogType.GlucoseLevel -> (rightLogEntity as GlucoseLogEntity).timeStamp
            LogType.WaterIntake -> (rightLogEntity as WaterLogEntity).timeStamp
            LogType.Weight -> (rightLogEntity as WeightLogEntity).timeStamp
            LogType.Medicine -> (rightLogEntity as MedicineLogEntity).timeStamp
            LogType.PPG -> (rightLogEntity as PPGEntity).timeStamp
            else -> null
        }?.toDateStringWithoutTime()
        return if (leftDate != rightDate && rightDate != null) {
            HistoryItem.DateItem(rightDate)
        } else {
            null
        }
    }
}