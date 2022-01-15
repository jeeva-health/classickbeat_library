package ai.heart.classickbeats.model.entity

import androidx.room.Entity
import com.squareup.moshi.Json


@Entity(tableName = "timeline", primaryKeys = ["id", "model"])
data class TimelineEntityDatabase(
    @Json(name = "pk")
    val id: Long,
    val model: String,
    val hr: String?,
    val sdnn: Float?,
    val meanNN: Float?,
    val rmssd: Float?,
    val pnn50: Float?,
    val diastolic: Int?,
    val glucoseValue: Int?,
    val glucoseTag: Int?,
    val stressLevel: Int?,
    val systolic: Int?,
    val timeStamp: String?,
    val water: String?,
    val weightValue: String?
)
