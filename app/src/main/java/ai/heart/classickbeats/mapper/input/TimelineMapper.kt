package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.Timeline
import ai.heart.classickbeats.model.TimelineType
import ai.heart.classickbeats.model.entity.TimelineEntity
import ai.heart.classickbeats.shared.util.toDate
import javax.inject.Inject
import kotlin.math.roundToInt

class TimelineMapper @Inject constructor() : Mapper<TimelineEntity, Timeline> {

    override fun map(input: TimelineEntity): Timeline {
        val type = when (input.type) {
            "daily" -> TimelineType.Daily
            "weekly" -> TimelineType.Weekly
            "monthly" -> TimelineType.Monthly
            else -> throw Exception("Unhandled timeline type variable")
        }
        val (date, diastolicAvg, hrAvg, sdnnAvg, systolicAvg, avgValue) = when (type) {
            TimelineType.Daily ->
                GroupedData(
                    date = input.date!!,
                    diastolicAvg = input.diastolicDailyAvg?.toInt(),
                    hrAvg = input.hrDailyAvg,
                    sdnnAvg = input.sdnnDailyAvg,
                    systolicAvg = input.systolicDailyAvg?.toInt(),
                    avgValue = input.dailyAvg
                )
            TimelineType.Weekly ->
                GroupedData(
                    date = input.week!!,
                    diastolicAvg = input.diastolicWeeklyAvg?.toInt(),
                    hrAvg = input.hrWeeklyAvg,
                    sdnnAvg = input.sdnnWeeklyAvg,
                    systolicAvg = input.systolicWeeklyAvg?.toInt(),
                    avgValue = input.weeklyAvg
                )
            TimelineType.Monthly ->
                GroupedData(
                    date = input.month!!,
                    diastolicAvg = input.diastolicMonthlyAvg?.toInt(),
                    hrAvg = input.hrMonthlyAvg,
                    sdnnAvg = input.sdnnMonthlyAvg,
                    systolicAvg = input.systolicMonthlyAvg?.toInt(),
                    avgValue = input.monthlyAvg
                )
        }
        val model: LogType = when (input.model) {
            "record_data.glucose" -> LogType.GlucoseLevel
            "record_data.waterintake" -> LogType.WaterIntake
            "record_data.weightlog" -> LogType.Weight
            "record_data.bloodpressure" -> LogType.BloodPressure
            "record_data.ppg" -> LogType.PPG
            else -> throw Exception("Unhandled model type")
        }
        return Timeline(
            type = type,
            date = date.toDate()!!,
            model = model,
            diastolicAvg = diastolicAvg,
            systolicAvg = systolicAvg,
            hrAvg = hrAvg?.roundToInt(),
            sdnnAvg = sdnnAvg,
            avgValue = String.format("%.1f", avgValue ?: 0.0).toDouble()
        )
    }

    data class GroupedData(
        val date: String,
        val diastolicAvg: Int?,
        val hrAvg: Double?,
        val sdnnAvg: Double?,
        val systolicAvg: Int?,
        val avgValue: Double?,
    )
}