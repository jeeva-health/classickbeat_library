package ai.heart.classickbeats.model.response

import com.squareup.moshi.Json

data class GraphDataResponse(
    @Json(name = "success")
    val successStatus: Boolean,
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "data")
    val responseData: List<ResponseData>?,
    @Json(name = "error_list")
    val errorList: List<String>?
) {
    data class ResponseData(
        @Json(name = "hr_daily_avg")
        val hrDailyAvg: Double?,
        @Json(name = "sdnn_daily_avg")
        val sdnnDailyAvg: Double?,
        @Json(name = "systolic_daily_avg")
        val systolicDailyAvg: Double?,
        @Json(name = "diastolic_daily_avg")
        val diastolicDailyAvg: Double?,
        @Json(name = "daily_avg")
        val dailyAvg: Double?,

        @Json(name = "hr_week_avg")
        val hrWeeklyAvg: Double?,
        @Json(name = "sdnn_week_avg")
        val sdnnWeeklyAvg: Double?,
        @Json(name = "systolic_week_avg")
        val systolicWeeklyAvg: Double?,
        @Json(name = "diastolic_week_avg")
        val diastolicWeeklyAvg: Double?,
        @Json(name = "week_avg")
        val weeklyAvg: Double?,

        @Json(name = "hr_month_avg")
        val hrMonthlyAvg: Double?,
        @Json(name = "sdnn_month_avg")
        val sdnnMonthlyAvg: Double?,
        @Json(name = "systolic_month_avg")
        val systolicMonthlyAvg: Double?,
        @Json(name = "diastolic_month_avg")
        val diastolicMonthlyAvg: Double?,
        @Json(name = "month_avg")
        val monthlyAvg: Double?,

        val date: String?,
        val week: String?,
        val month: String?,
    )
}