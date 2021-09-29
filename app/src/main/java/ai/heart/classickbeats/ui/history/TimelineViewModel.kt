package ai.heart.classickbeats.ui.history

import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.model.Timeline
import ai.heart.classickbeats.model.TimelineItem
import ai.heart.classickbeats.model.TimelineType
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.util.toMonthString
import ai.heart.classickbeats.shared.util.toOrdinalFormattedDateString
import ai.heart.classickbeats.shared.util.toWeekString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject


@ExperimentalPagingApi
@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    var apiError: String? = null

    private val _showLoading = MutableLiveData(Event(false))
    val showLoading: LiveData<Event<Boolean>> = _showLoading
    private fun setShowLoadingTrue() = _showLoading.postValue(Event(true))
    private fun setShowLoadingFalse() = _showLoading.postValue(Event(false))

    fun getTimelineData(type: TimelineType): Flow<PagingData<TimelineItem>> =
        recordRepository.getTimelineData(type).map { pagingData ->
            pagingData.map { timeline ->
                convertTimelineToTimelineItem(timeline)
            }.insertSeparators { before: TimelineItem?, after: TimelineItem? ->
                insertDateSeparatorIfNeeded(before, after)
            }
        }.cachedIn(viewModelScope)

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
            TimelineType.Daily -> date.toOrdinalFormattedDateString()
            TimelineType.Weekly -> date.toWeekString()
            TimelineType.Monthly -> date.toMonthString()
        }
}