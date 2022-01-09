package ai.heart.classickbeats.model.response

import ai.heart.classickbeats.model.entity.MeditationEntity
import com.squareup.moshi.Json

data class MeditationListResponse(
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
        @Json(name = "meditation_list")
        val meditationList: List<MeditationEntity>
    )
}