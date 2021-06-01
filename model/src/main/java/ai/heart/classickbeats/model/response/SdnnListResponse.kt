package ai.heart.classickbeats.model.response

import com.squareup.moshi.Json

data class SdnnListResponse(
    @Json(name = "data") val responseData: Data,
    val errorList: List<String>
) {
    data class Data(
        val sdnn_list: List<String>
    )
}