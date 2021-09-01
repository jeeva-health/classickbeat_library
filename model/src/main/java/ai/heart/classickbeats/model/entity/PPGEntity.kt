package ai.heart.classickbeats.model.entity

import ai.heart.classickbeats.model.LogType
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

data class PPGEntity(

    @Transient
    var id: Long = -1,

    @PrimaryKey(autoGenerate = true)
    var localId: Int = -1,

    @Json(name = "r_means")
    var rMeans: List<Float>? = null,

    @Json(name = "g_means")
    var gMeans: List<Float>? = null,

    @Json(name = "b_means")
    var bMeans: List<Float>? = null,

    @Json(name = "camera_timestamp")
    var cameraTimeStamps: List<Long>? = null,

    @Json(name = "filtered_r_means")
    var filteredRMeans: List<Double>? = null,

    var hr: Float? = null,

    var sdnn: Float? = null,

    @Json(name = "mean_nn")
    var meanNN: Float? = null,

    var rmssd: Float? = null,

    var pnn50: Float? = null,

    var ln: Float? = null,

    var quality: Float? = null,

    var binProbsMAP: List<Float>? = null,

    var bAgeBin: Int? = null,

    @Json(name = "activeSedantryProb")
    var activeSedentaryProb: List<Float>? = null,

    var sedRatioLog: Float? = null,

    var sedStars: Int? = null,

    var stressLevel: Int? = null,

    @Json(name = "surveySleep")
    var sleepRating: Int? = null,

    @Json(name = "surveyMood")
    var moodRating: Int? = null,

    @Json(name = "surveyHealthy")
    var healthRating: Int? = null,

    @Json(name = "surveyState")
    var scanState: String? = null,

    var timeStamp: String? = null,

    var isUploaded: Boolean = false

) : BaseLogEntity(LogType.PPG)