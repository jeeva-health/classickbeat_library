package ai.heart.classickbeats.model.entity

import ai.heart.classickbeats.model.LogType
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "ppg_record")
data class PPGEntity(

    @Transient
    @ColumnInfo(name = "id")
    var id: Long = -1,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    var localId: Int = -1,

    @Json(name = "r_means")
    @ColumnInfo(name = "r_means")
    var rMeans: List<Float>? = null,

    @Json(name = "g_means")
    @ColumnInfo(name = "g_means")
    var gMeans: List<Float>? = null,

    @Json(name = "b_means")
    @ColumnInfo(name = "b_means")
    var bMeans: List<Float>? = null,

    @Json(name = "camera_timestamp")
    @ColumnInfo(name = "camera_timestamp")
    var cameraTimeStamps: List<Long>? = null,

    @Json(name = "filtered_r_means")
    @ColumnInfo(name = "filtered_r_means")
    var filteredRMeans: List<Double>? = null,

    var hr: Float? = null,

    var sdnn: Float? = null,

    @Json(name = "mean_nn")
    @ColumnInfo(name = "mean_nn")
    var meanNN: Float? = null,

    var rmssd: Float? = null,

    var pnn50: Float? = null,

    var ln: Float? = null,

    var quality: Float? = null,

    @ColumnInfo(name = "bin_probs_map")
    var binProbsMAP: List<Float>? = null,

    @ColumnInfo(name = "b_age_bin")
    var bAgeBin: Int? = null,

    @Json(name = "activeSedantryProb")
    @ColumnInfo(name = "active_sedantry_prob")
    var activeSedentaryProb: List<Float>? = null,

    @ColumnInfo(name = "sed_ratio_log")
    var sedRatioLog: Float? = null,

    @ColumnInfo(name = "sed_stars")
    var sedStars: Int? = null,

    @ColumnInfo(name = "stress_level")
    var stressLevel: Int? = null,

    @Json(name = "surveySleep")
    @ColumnInfo(name = "survey_sleep")
    var sleepRating: Int? = null,

    @Json(name = "surveyMood")
    @ColumnInfo(name = "survey_mood")
    var moodRating: Int? = null,

    @Json(name = "surveyHealthy")
    @ColumnInfo(name = "survey_healthy")
    var healthRating: Int? = null,

    @Json(name = "surveyState")
    @ColumnInfo(name = "survey_state")
    var scanState: String? = null,

    @ColumnInfo(name = "time_stamp")
    var timeStamp: String? = null,

    @ColumnInfo(name = "is_uploaded")
    var isUploaded: Boolean = false

) : BaseLogEntity(LogType.PPG)