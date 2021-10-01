package ai.heart.classickbeats.data.record.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timeline_remote_keys")
data class TimelineRemoteKey(
    @PrimaryKey
    val timelineId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)