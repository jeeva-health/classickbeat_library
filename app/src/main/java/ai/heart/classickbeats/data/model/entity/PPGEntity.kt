package ai.heart.classickbeats.data.model.entity

import com.squareup.moshi.Json

data class PPGEntity(
    @Json(name = "user")
    val userId: Long,
    @Json(name = "r_means")
    val rMeans: List<Float>? = null,
    @Json(name = "g_means")
    val gMeans: List<Float>? = null,
    @Json(name = "b_means")
    val bMeans: List<Float>? = null,
    @Json(name = "camera_timestamp")
    val cameraTimeStamps: List<Long>? = null,
    @Json(name = "filtered_r_means")
    val filteredRMeans: List<Float>? = null,
    val hr: Float = 0.0f,
    val sdnn: Float = 0.0f,
    @Json(name = "mean_nn")
    val meanNN: Float = 0.0f,
    val rmssd: Float = 0.0f,
    val pnn50: Float = 0.0f,
    val ln: Float = 0.0f,
    val quality: Float = 0.0f
)