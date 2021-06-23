package ai.heart.classickbeats.model.entity

import ai.heart.classickbeats.model.LogType
import com.squareup.moshi.Json

data class PPGEntity(
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
    val hr: Float? = null,
    val sdnn: Float? = null,
    @Json(name = "mean_nn")
    val meanNN: Float? = null,
    val rmssd: Float? = null,
    val pnn50: Float? = null,
    val ln: Float? = null,
    val quality: Float? = null,
    val binProbsMAP: List<Float>? = null,
    val bAgeBin: Int? = null,
    val activeSedantryProb: List<Float>? = null,
    val sedRatioLog: Float? = null,
    val sedStars: Int? = null,
    val stressLevel: Int? = null,
    @Json(name = "surveySleep")
    val sleepRating: Int? = null,
    @Json(name = "surveyMood")
    val moodRating: Int? = null,
    @Json(name = "surveyHealthy")
    val healthRating: Int? = null,
    @Json(name = "surveyState")
    val scanState: String? = null
) : BaseLogEntity(LogType.PPG)