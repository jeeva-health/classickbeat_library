package ai.heart.classickbeats.model.entity

import ai.heart.classickbeats.model.LogType
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

data class PPGEntity(

    @Transient
    val id: Long = -1,

    @PrimaryKey(autoGenerate = true)
    val localId: Int = -1,

    @Json(name = "r_means")
    val rMeans: List<Float>? = null,

    @Json(name = "g_means")
    val gMeans: List<Float>? = null,

    @Json(name = "b_means")
    val bMeans: List<Float>? = null,

    @Json(name = "camera_timestamp")
    val cameraTimeStamps: List<Long>? = null,

    @Json(name = "acceleration_1")
    val xAcceleration: List<Float>? = null,

    @Json(name = "acceleration_2")
    val yAcceleration: List<Float>? = null,

    @Json(name = "acceleration_3")
    val zAcceleration: List<Float>? = null,

    @Json(name = "acceleration_timestamp")
    val accelerationTimestamp: List<Long>? = null,

    @Json(name = "filtered_r_means")
    val filteredRMeans: List<Double>? = null,

    val hr: Float? = null,

    val sdnn: Float? = null,

    @Json(name = "mean_nn")
    val meanNN: Float? = null,

    val rmssd: Float? = null,

    val pnn50: Float? = null,

    val ln: Float? = null,

    val quality: Float? = null,

    val bAgeBin: Int? = null,

    val activeStars: Int? = null,

    val stressLevel: Int? = null,

    @Json(name = "surveySleep")
    val sleepRating: Int? = null,

    @Json(name = "surveyMood")
    val moodRating: Int? = null,

    @Json(name = "surveyHealthy")
    val healthRating: Int? = null,

    @Json(name = "surveyState")
    val scanState: String? = null,

    val timeStamp: String? = null,

    @Json(name = "this_ppg_count")
    val ppgCount: Int? = null,

    @Json(name = "this_ppg_distinct_days")
    val ppgDistinctDays: Int? = null,

    @Json(name = "this_ppg_baseline_set")
    val isBaselineSet: Boolean? = null,

    @Json(name = "is_calculation_complete")
    val isCalculationComplete: Boolean? = null,

    @Json(name = "lifestyleClassification")
    val lifeStyleCategory: Int? = null,

    @Json(name = "is_saved")
    val isSaved: Boolean? = true,

    @Json(name = "heartAgeClassification")
    val heartAgeClassification: Int? = null,

    ) : BaseLogEntity(LogType.PPG)
