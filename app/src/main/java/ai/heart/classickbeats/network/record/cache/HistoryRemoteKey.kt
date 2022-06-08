package ai.heart.classickbeats.network.record.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_remote_keys")
data class HistoryRemoteKey(
    @PrimaryKey
    val historyId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)