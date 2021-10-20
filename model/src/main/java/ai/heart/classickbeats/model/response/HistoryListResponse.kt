package ai.heart.classickbeats.model.response

import ai.heart.classickbeats.model.TimelineEntityNetwork
import com.squareup.moshi.Json

data class HistoryListResponse(
    @Json(name = "success")
    val successStatus: Boolean,
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "data")
    val responseData: ResponseData,
    @Json(name = "error_list")
    val errorList: List<String>?
) {
    data class ResponseData(
        @Json(name = "history_list")
        val historyList: List<TimelineEntityNetwork>
    )
}