package ai.heart.classickbeats.model.response

import ai.heart.classickbeats.model.HistoryRecord
import com.squareup.moshi.Json

data class HistoryResponse(
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
        val historyPaginatedData: HistoryPaginatedData
    ) {
        data class HistoryPaginatedData(
            @Json(name = "count")
            val count: Int,
            @Json(name = "next")
            val nextPage: String?,
            @Json(name = "previous")
            val previousPage: String?,
            @Json(name = "results")
            val loggingList: List<HistoryRecord>
        )
    }
}