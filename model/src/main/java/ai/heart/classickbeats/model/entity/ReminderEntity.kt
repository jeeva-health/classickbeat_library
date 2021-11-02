package ai.heart.classickbeats.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "reminders")
data class ReminderEntity(
    val id: Long?,
    @Json(name = "day_of_week")
    val dayOfWeek: List<Int>?,
    @Json(name = "is_active")
    val isActive: Boolean?,
    @Json(name = "is_set")
    val isSet: Boolean?,
    val notes: String?,
    val time: String?,
    val type: Int?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    var localId: Int = 0
}