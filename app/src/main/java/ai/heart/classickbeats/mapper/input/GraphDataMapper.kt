package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.GraphData
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.HistoryType
import ai.heart.classickbeats.model.response.GraphDataResponse
import ai.heart.classickbeats.shared.util.toDate
import java.util.*
import javax.inject.Inject

class GraphDataMapper @Inject constructor() : Mapper<GraphDataMapper.InputData, GraphData> {

    override fun map(input: InputData): GraphData {
        val (model, type, startDate, endDate, response) = input
        val dataList = mutableListOf<Double>()
        val dataList2 = mutableListOf<Double>()
        val dateList = mutableListOf<Date>()
        response.forEach {
            dateList.add(it.date!!.toDate()!!)
            when (model) {
                LogType.BloodPressure -> {
                    dataList.add(it.systolicDailyAvg!!)
                    dataList2.add(it.diastolicDailyAvg!!)
                }
                LogType.PPG -> {
                    dataList.add(it.hrDailyAvg!!)
                }
                LogType.Medicine -> throw Exception("Unhandled logType")
                else -> {
                    dataList.add(it.dailyAvg!!)
                }
            }
        }
        return GraphData(
            model = model,
            timelineType = type,
            startDate = startDate,
            endDate = endDate,
            valueList = dataList,
            valueList2 = dataList2,
            dateList = dateList,
        )
    }

    data class InputData(
        val model: LogType,
        val type: HistoryType,
        val startDate: Date,
        val endDate: Date,
        val response: List<GraphDataResponse.ResponseData>
    )
}