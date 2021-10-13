package ai.heart.classickbeats.ui.history.viewmodel

import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.model.*
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import ai.heart.classickbeats.shared.util.*
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

    fun getTimelineData(type: TimelineType): Flow<PagingData<TimelineItem>> =
        recordRepository.getTimelineData(type).mapLatest { pagingData ->
            pagingData.map { timeline ->
                convertTimelineToTimelineItem(timeline)
            }.insertSeparators { before: TimelineItem?, after: TimelineItem? ->
                insertDateSeparatorIfNeeded(before, after)
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

    private fun convertTimelineToTimelineItem(timeline: Timeline): TimelineItem {
        return TimelineItem.LogItem(timeline)
    }

    private fun insertDateSeparatorIfNeeded(
        leftItem: TimelineItem?,
        rightItem: TimelineItem?
    ): TimelineItem? {
        val leftTimelineItem = (leftItem as TimelineItem.LogItem?)?.timeline
        val rightTimelineItem = (rightItem as TimelineItem.LogItem?)?.timeline
        val timelineType = leftTimelineItem?.type ?: rightTimelineItem?.type
        val leftDate = leftTimelineItem?.date
        val rightDate = rightTimelineItem?.date
        return if (leftDate != rightDate && rightDate != null) {
            val date = getDateStringByTimelineType(timelineType!!, rightDate)
            TimelineItem.DateItem(date)
        } else {
            null
        }
    }

    private fun getDateStringByTimelineType(timelineType: TimelineType, date: Date): String =
        when (timelineType) {
            TimelineType.Daily -> date.toOrdinalFormattedDateStringWithoutYear()
            TimelineType.Weekly -> date.toWeekString()
            TimelineType.Monthly -> date.toMonthString()
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