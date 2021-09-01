package ai.heart.classickbeats.data.record.cache

import ai.heart.classickbeats.model.HistoryRecord
import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyRecord: HistoryRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(historyRecordList: List<HistoryRecord>)

    @Update
    suspend fun update(historyRecord: HistoryRecord)

    @Delete
    suspend fun delete(historyRecord: HistoryRecord)

    @Query("DELETE FROM history_record")
    suspend fun deleteAll()

    @Query("SELECT * FROM history_record WHERE id = :id")
    suspend fun load(id: Int): HistoryRecord

    @Query("SELECT * FROM history_record")
    fun loadAll(): PagingSource<Int, HistoryRecord>
}