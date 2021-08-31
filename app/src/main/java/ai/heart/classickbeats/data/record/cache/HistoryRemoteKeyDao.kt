package ai.heart.classickbeats.data.record.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<HistoryRemoteKey>)

    @Query("SELECT * FROM history_remote_keys WHERE historyId = :historyId")
    suspend fun remoteKeysHistoryId(historyId: Long): HistoryRemoteKey?

    @Query("DELETE FROM history_remote_keys")
    suspend fun clearRemoteKeys()
}
