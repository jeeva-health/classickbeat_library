package ai.heart.classickbeats.model.response

import ai.heart.classickbeats.model.entity.LogEntityNetwork
import com.squareup.moshi.Json


data class LoggingListResponse(
    @Json(name = "success")
    val successStatus: Boolean,
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "data")
    val responseData: LoggingData,
    @Json(name = "error_list")
    val errorList: List<String>?
) {
    data class LoggingData(
        @Json(name = "logging_list")
        val loggingList: List<List<LogEntityNetwork>>
    )
}
