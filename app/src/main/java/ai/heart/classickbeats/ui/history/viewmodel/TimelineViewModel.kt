package ai.heart.classickbeats.ui.history.viewmodel

import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.model.*
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import ai.heart.classickbeats.shared.util.getDateAddedBy
import ai.heart.classickbeats.shared.util.getNumberOfDaysInMonth
import ai.heart.classickbeats.ui.history.Utils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    var apiError: String? = null

    private val _showLoading = MutableLiveData(Event(false))
    val showLoading: LiveData<Event<Boolean>> = _showLoading
    private fun setShowLoadingTrue() = _showLoading.postValue(Event(true))
    private fun setShowLoadingFalse() = _showLoading.postValue(Event(false))

    private val _graphData = MutableLiveData<GraphData>()
    val graphData: LiveData<GraphData> = _graphData

    private val _measurementData = MutableLiveData<List<HistoryItem>>()
    val measurementData: LiveData<List<HistoryItem>> = _measurementData

    fun getTimelineData(type: TimelineType): Flow<PagingData<TimelineItem>> =
        recordRepository.getTimelineData(type).mapLatest { pagingData ->
            pagingData.map { timeline ->
                Utils.convertTimelineToTimelineItem(timeline)
            }.insertSeparators { before: TimelineItem?, after: TimelineItem? ->
                Utils.insertDateSeparatorIfNeeded(before, after)
            }
        }.cachedIn(viewModelScope)

    fun getGraphData(model: LogType, type: TimelineType, startDate: Date) {
        viewModelScope.launch {
            setShowLoadingTrue()
            val endDate = getEndDate(startDate, type)
            val response = recordRepository.getGraphData(model, type, startDate, endDate)
            if (response.succeeded) {
                _graphData.value = response.data
            } else {
                apiError = response.error
            }
            setShowLoadingFalse()
        }
    }

    fun getMeasurementData(model: LogType, type: TimelineType, startDate: Date) {
        viewModelScope.launch {
            val endDate = getEndDate(startDate, type)
            val response = recordRepository.getHistoryListData(model, startDate, endDate)
            if (response.succeeded) {
                val historyItemList = mutableListOf<HistoryItem>()
                val baseLogList = response.data!!.map {
                    Utils.convertLogEntityToHistoryItem(it)
                }
                for (i in baseLogList.indices) {
                    val leftItem = baseLogList.getOrNull(i - 1)
                    val rightItem = baseLogList.getOrNull(i)
                    val dateItem = Utils.insertDateSeparatorIfNeeded(leftItem, rightItem)
                    if (dateItem != null) {
                        historyItemList.add(dateItem)
                    }
                    historyItemList.add(baseLogList[i])
                }
                _measurementData.value = historyItemList.toList()
            } else {
                apiError = response.error
            }
        }
    }

    private fun getEndDate(startDate: Date, type: TimelineType): Date {
        val diffDays = when (type) {
            TimelineType.Daily -> 1
            TimelineType.Weekly -> 7
            TimelineType.Monthly -> startDate.getNumberOfDaysInMonth()
        }
        return startDate.getDateAddedBy(diffDays)
    }
}