package ai.heart.classickbeats.model.response

import ai.heart.classickbeats.model.Logging
import com.squareup.moshi.Json

data class HistoryResponse(
    @Json(name = "data")
    val responseData: LoggingData,
    val errorList: List<String>?
) {
    data class LoggingData(
        @Json(name = "history_list")
        val loggingList: List<List<Logging>>
    )
}