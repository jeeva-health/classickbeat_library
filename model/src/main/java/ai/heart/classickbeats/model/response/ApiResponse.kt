package ai.heart.classickbeats.model.response

import com.squareup.moshi.Json

data class ApiResponse(
    @Json(name = "success")
    val successStatus: Boolean,
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "data")
    val responseData: ResponseData,
    @Json(name = "error_list")
    val errorList: List<String>?
) {
    data class ResponseData(@Json(name = "id") val id: Long = -1)
}
