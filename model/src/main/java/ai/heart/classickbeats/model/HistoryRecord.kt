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
        //TODO("add HRV metrics")
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

/*

Fetch 1 month history data during scan

* var sdnn: Float? = null,

    @Json(name = "mean_nn")
    var meanNN: Float? = null,

    var rmssd: Float? = null,

    var pnn50: Float? = null,
* */