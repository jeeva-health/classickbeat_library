package ai.heart.classickbeats.ui.history.viewmodel

import ai.heart.classickbeats.network.record.RecordRepository
import ai.heart.classickbeats.model.TimelineItem
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.ui.history.Utils
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
class TimelineViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    var apiError: String? = null

    private val _showLoading = MutableLiveData(Event(false))
    val showLoading: LiveData<Event<Boolean>> = _showLoading
    private fun setShowLoadingTrue() = _showLoading.postValue(Event(true))
    private fun setShowLoadingFalse() = _showLoading.postValue(Event(false))

    fun getTimelineData(): Flow<PagingData<TimelineItem>> {
        return recordRepository.getHistoryData().map { pagingData ->
            pagingData.map { baseLogEntity ->
                Utils.convertLogEntityToTimelineItem(baseLogEntity)
            }.insertSeparators { before: TimelineItem?, after: TimelineItem? ->
                Utils.insertDateSeparatorIfNeeded(before, after)
            }
        }.cachedIn(viewModelScope)
    }
}