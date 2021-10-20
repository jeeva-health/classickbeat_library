package ai.heart.classickbeats.model.response

import ai.heart.classickbeats.model.entity.HistoryEntity
import com.squareup.moshi.Json

data class TimelineResponse(
    @Json(name = "success")
    val successStatus: Boolean,
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "data")
    val responseData: ResponseData,
    @Json(name = "error_list")
    val error_list: List<String>?
) {
    data class ResponseData(
        @Json(name = "history_list")
        val timelinePaginatedData: TimelinePaginatedData
    ) {
        data class TimelinePaginatedData(
            val count: Int,
            val next: String?,
            val previous: String?,
            @Json(name = "results")
            val fields: List<HistoryEntity>
        )
    }
}