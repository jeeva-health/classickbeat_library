package ai.heart.classickbeats.ui.history

import ai.heart.classickbeats.model.*
import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.shared.util.toDateStringWithoutTime
import ai.heart.classickbeats.shared.util.toMonthString
import ai.heart.classickbeats.shared.util.toOrdinalFormattedDateStringWithoutYear
import ai.heart.classickbeats.shared.util.toWeekString
import java.util.Date

object Utils {

    fun convertLogEntityToHistoryItem(baseLogEntity: BaseLogEntity): TimelineItem {
        return TimelineItem.LogItem(baseLogEntity)
    }

    fun convertTimelineToTimelineItem(timeline: Timeline): HistoryItem {
        return HistoryItem.LogItem(timeline)
    }

    fun insertDateSeparatorIfNeeded(
        leftEntity: TimelineItem?,
        rightEntity: TimelineItem?
    ): TimelineItem? {
        val leftLogEntity = (leftEntity as TimelineItem.LogItem?)?.logEntity
        val rightLogEntity = (rightEntity as TimelineItem.LogItem?)?.logEntity
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
            TimelineItem.DateItem(rightDate)
        } else {
            null
        }
    }

    fun insertDateSeparatorIfNeeded(
        leftItem: HistoryItem?,
        rightItem: HistoryItem?
    ): HistoryItem? {
        val leftTimelineItem = (leftItem as HistoryItem.LogItem?)?.timeline
        val rightTimelineItem = (rightItem as HistoryItem.LogItem?)?.timeline
        val timelineType = leftTimelineItem?.type ?: rightTimelineItem?.type
        val leftDate = leftTimelineItem?.date
        val rightDate = rightTimelineItem?.date
        return if (leftDate != rightDate && rightDate != null) {
            val date = getDateStringByTimelineType(timelineType!!, rightDate)
            HistoryItem.DateItem(date)
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
}