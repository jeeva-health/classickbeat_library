package ai.heart.classickbeats.model.entity

import ai.heart.classickbeats.model.TimelineType
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "timeline")
data class TimelineEntity(
    @Json(name = "date")
    val date: String?,
    @Json(name = "week")
    val week: String?,
    @Json(name = "month")
    val month: String?,
    @Json(name = "model")
    val model: String,
    @Json(name = "diastolic_daily_avg")
    val diastolicDailyAvg: Double?,
    @Json(name = "hr_daily_avg")
    val hrDailyAvg: Double?,
    @Json(name = "sdnn_daily_avg")
    val sdnnDailyAvg: Double?,
    @Json(name = "systolic_daily_avg")
    val systolicDailyAvg: Double?,
    @Json(name = "daily_avg")
    val dailyAvg: Double?,
    @Json(name = "diastolic_weekly_avg")
    val diastolicWeeklyAvg: Double?,
    @Json(name = "hr_weekly_avg")
    val hrWeeklyAvg: Double?,
    @Json(name = "sdnn_weekly_avg")
    val sdnnWeeklyAvg: Double?,
    @Json(name = "systolic_weekly_avg")
    val systolicWeeklyAvg: Double?,
    @Json(name = "weekly_avg")
    val weeklyAvg: Double?,
    @Json(name = "diastolic_monthly_avg")
    val diastolicMonthlyAvg: Double?,
    @Json(name = "hr_monthly_avg")
    val hrMonthlyAvg: Double?,
    @Json(name = "sdnn_monthly_avg")
    val sdnnMonthlyAvg: Double?,
    @Json(name = "systolic_monthly_avg")
    val systolicMonthlyAvg: Double?,
    @Json(name = "monthly_avg")
    val monthlyAvg: Double?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "timeline_type")
    var type: String = TimelineType.Daily.value
}