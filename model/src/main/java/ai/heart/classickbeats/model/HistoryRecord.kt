package ai.heart.classickbeats.model

import androidx.room.Embedded
import androidx.room.Entity
import com.squareup.moshi.Json

@Entity(tableName = "history_record", primaryKeys = ["id", "model"])
data class HistoryRecord(
    @Json(name = "pk")
    val id: Long,
    @Embedded
    val fields: Fields,
    val model: String,
) {
    data class Fields(
        val hr: String?,
        val diastolic: Int?,
        val glucoseValue: Int?,
        val note: String?,
        val statusTag: Int?,
        val stressLevel: Int?,
        val systolic: Int?,
        val timeStamp: String?,
        val water: String?,
        val weightValue: String?
    )
}