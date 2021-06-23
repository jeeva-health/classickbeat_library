package ai.heart.classickbeats.model.response

import ai.heart.classickbeats.model.Logging
import com.squareup.moshi.Json

data class LoggingListResponse(
    @Json(name = "data")
    val responseData: LoggingData,
    val errorList: List<String>?
) {
    data class LoggingData(
        @Json(name = "logging_list")
        val loggingList: List<List<Logging>>
    )
}