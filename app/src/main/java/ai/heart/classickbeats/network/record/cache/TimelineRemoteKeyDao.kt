package ai.heart.classickbeats.network.record.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TimelineRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<TimelineRemoteKey>)

    @Query("SELECT * FROM timeline_remote_keys WHERE timelineId = :timelineId")
    suspend fun remoteKeysTimelineId(timelineId: Long): TimelineRemoteKey?

    @Query("DELETE FROM timeline_remote_keys")
    suspend fun clearRemoteKeys()
}
