package ai.heart.classickbeats.data.record.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_remote_keys")
data class HistoryRemoteKey(
    @PrimaryKey
    val historyId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)