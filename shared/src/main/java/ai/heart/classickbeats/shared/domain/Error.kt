package ai.heart.classickbeats.shared.domain


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Error(
    @Json(name = "error_list")
    val errorList: List<String>?
)