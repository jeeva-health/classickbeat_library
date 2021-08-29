package ai.heart.classickbeats.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "history_record")
data class HistoryRecord(
    @Json(name = "pk")
    val id: Long,
    @Transient
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    val localId: Long = -1,
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