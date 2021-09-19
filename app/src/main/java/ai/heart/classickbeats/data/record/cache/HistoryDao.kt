package ai.heart.classickbeats.data.record.cache

import ai.heart.classickbeats.model.HistoryRecordDatabase
import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyRecord: HistoryRecordDatabase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(historyRecordList: List<HistoryRecordDatabase>)

    @Update
    suspend fun update(historyRecord: HistoryRecordDatabase)

    @Delete
    suspend fun delete(historyRecord: HistoryRecordDatabase)

    @Query("DELETE FROM history_record")
    suspend fun deleteAll()

    @Query("SELECT * FROM history_record WHERE id = :id")
    suspend fun load(id: Int): HistoryRecordDatabase

    @Query("SELECT * FROM history_record")
    fun loadAll(): PagingSource<Int, HistoryRecordDatabase>

    @Query("SELECT * FROM history_record WHERE model = :model ORDER BY id DESC LIMIT :limit")
    suspend fun loadHistoryDataByModelAndCount(
        model: String,
        limit: Int
    ): List<HistoryRecordDatabase>

    @Query("SELECT * FROM history_record WHERE model = :model AND timeStamp >= :startTimeStamp ORDER BY id DESC")
    suspend fun loadHistoryDataByModelAndDuration(
        model: String,
        startTimeStamp: String
    ): List<HistoryRecordDatabase>
}