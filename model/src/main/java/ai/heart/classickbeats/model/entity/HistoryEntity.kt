package ai.heart.classickbeats.model.entity

import ai.heart.classickbeats.model.HistoryType
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "history_record")
data class HistoryEntity(
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
    @Json(name = "diastolic_week_avg")
    val diastolicWeeklyAvg: Double?,
    @Json(name = "hr_week_avg")
    val hrWeeklyAvg: Double?,
    @Json(name = "sdnn_week_avg")
    val sdnnWeeklyAvg: Double?,
    @Json(name = "systolic_week_avg")
    val systolicWeeklyAvg: Double?,
    @Json(name = "week_avg")
    val weeklyAvg: Double?,
    @Json(name = "diastolic_month_avg")
    val diastolicMonthlyAvg: Double?,
    @Json(name = "hr_month_avg")
    val hrMonthlyAvg: Double?,
    @Json(name = "sdnn_month_avg")
    val sdnnMonthlyAvg: Double?,
    @Json(name = "systolic_month_avg")
    val systolicMonthlyAvg: Double?,
    @Json(name = "month_avg")
    val monthlyAvg: Double?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "history_type")
    var type: String = HistoryType.Daily.value
}