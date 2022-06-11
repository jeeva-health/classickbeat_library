package ai.heart.classickbeats.ui.history.viewmodel

import ai.heart.classickbeats.network.record.RecordRepository
import ai.heart.classickbeats.model.*
import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import ai.heart.classickbeats.shared.util.getDateAddedBy
import ai.heart.classickbeats.shared.util.getNumberOfDaysInMonth
import ai.heart.classickbeats.shared.util.toDateWithMilli
import ai.heart.classickbeats.shared.util.toDateWithSeconds
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
class HistoryViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    var apiError: String? = null

    private val _showLoading = MutableLiveData(Event(false))
    val showLoading: LiveData<Event<Boolean>> = _showLoading
    private fun setShowLoadingTrue() = _showLoading.postValue(Event(true))
    private fun setShowLoadingFalse() = _showLoading.postValue(Event(false))

    private val _graphData = MutableLiveData<GraphData>()
    val graphData: LiveData<GraphData> = _graphData

    private val _selectedHistoryType = MutableLiveData(HistoryType.Daily)
    val selectedHistoryType: LiveData<HistoryType> = _selectedHistoryType
    fun setSelectedHistoryType(historyType: HistoryType) {
        _selectedHistoryType.postValue(historyType)
    }

    private val _measurementData = MutableLiveData<List<TimelineItem>>()
    val measurementData: LiveData<List<TimelineItem>> = _measurementData

    fun getHistoryData(type: HistoryType): Flow<PagingData<HistoryItem>> =
        recordRepository.getHistoryData(type).mapLatest { pagingData ->
            pagingData.map { timeline ->
                Utils.convertTimelineToHistoryItem(timeline)
            }.insertSeparators { before: HistoryItem?, after: HistoryItem? ->
                Utils.insertDateSeparatorIfNeeded(before, after)
            }
        }.cachedIn(viewModelScope)

    fun getGraphData(model: LogType, type: HistoryType, startDate: Date) {
        viewModelScope.launch {
            setShowLoadingTrue()
            if (type == HistoryType.Daily) {
                val endDate = startDate.getDateAddedBy(1)
                val response = recordRepository.getHistoryListData(model, startDate, endDate)
                if (response.succeeded) {
                    _graphData.value = convertBaseLogEntityToGraphData(
                        model = model,
                        timelineType = type,
                        input = response.data!!,
                        startDate = startDate,
                        endDate = startDate
                    )
                } else {
                    apiError = response.error
                }
            } else {
                val endDate = getEndDateGraph(startDate, type)
                val response = recordRepository.getGraphData(model, type, startDate, endDate)
                if (response.succeeded) {
                    _graphData.value = response.data
                } else {
                    apiError = response.error
                }
            }
            setShowLoadingFalse()
        }
    }

    fun getMeasurementData(model: LogType, type: HistoryType, startDate: Date) {
        viewModelScope.launch {
            val endDate = getEndDate(startDate, type)
            val response = recordRepository.getHistoryListData(model, startDate, endDate)
            if (response.succeeded) {
                val historyItemList = mutableListOf<TimelineItem>()
                val baseLogList = response.data!!.map {
                    Utils.convertLogEntityToTimelineItem(it)
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

    private fun getEndDateGraph(startDate: Date, type: HistoryType): Date {
        val diffDays = when (type) {
            HistoryType.Daily -> 1
            HistoryType.Weekly -> 6
            HistoryType.Monthly -> startDate.getNumberOfDaysInMonth() - 1
        }
        return startDate.getDateAddedBy(diffDays)
    }

    private fun getEndDate(startDate: Date, type: HistoryType): Date {
        val diffDays = when (type) {
            HistoryType.Daily -> 1
            HistoryType.Weekly -> 7
            HistoryType.Monthly -> startDate.getNumberOfDaysInMonth()
        }
        return startDate.getDateAddedBy(diffDays)
    }

    private fun convertBaseLogEntityToGraphData(
        model: LogType,
        timelineType: HistoryType,
        input: List<BaseLogEntity>,
        startDate: Date,
        endDate: Date
    ): GraphData {
        val valueList1 = mutableListOf<Double>()
        val valueList2 = mutableListOf<Double>()
        val dateList = mutableListOf<Date>()
        input.forEach { baseLogEntity ->
            val (value1, value2, timeStamp) = when (baseLogEntity.type) {
                LogType.BloodPressure -> Triple(
                    (baseLogEntity as PressureLogEntity).systolic,
                    baseLogEntity.diastolic,
                    baseLogEntity.timeStamp?.toDateWithSeconds()
                )
                LogType.GlucoseLevel -> Triple(
                    (baseLogEntity as GlucoseLogEntity).glucoseLevel,
                    null,
                    baseLogEntity.timeStamp?.toDateWithSeconds()
                )
                LogType.WaterIntake -> Triple(
                    (baseLogEntity as WaterLogEntity).quantity,
                    null,
                    baseLogEntity.timeStamp?.toDateWithSeconds()
                )
                LogType.Weight -> Triple(
                    (baseLogEntity as WeightLogEntity).weight,
                    null,
                    baseLogEntity.timeStamp?.toDateWithSeconds()
                )
                LogType.PPG -> Triple(
                    (baseLogEntity as PPGEntity).hr,
                    baseLogEntity.sdnn,
                    baseLogEntity.timeStamp?.toDateWithMilli()
                )
                else -> throw Exception("LogType not handled")
            }
            value1?.let { valueList1.add(it.toDouble()) }
            value2?.let { valueList2.add(it.toDouble()) }
            timeStamp?.let { dateList.add(it) }
        }
        return GraphData(
            model = model,
            timelineType = timelineType,
            isDecimal = true,
            valueList = valueList1,
            valueList2 = valueList2,
            dateList = dateList,
            startDate = startDate,
            endDate = endDate
        )
    }
}