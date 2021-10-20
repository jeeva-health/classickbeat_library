package ai.heart.classickbeats.data.record.cache

import ai.heart.classickbeats.model.entity.HistoryEntity
import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyEntity: HistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(historyEntityList: List<HistoryEntity>): List<Long>

    @Update
    suspend fun update(historyEntity: HistoryEntity)

    @Delete
    suspend fun delete(historyEntity: HistoryEntity)

    @Query("DELETE FROM history_record")
    suspend fun deleteAll()

    @Query("SELECT * FROM history_record WHERE id = :id")
    suspend fun load(id: Int): HistoryEntity

    @Query("SELECT * FROM history_record WHERE history_type = :type")
    fun loadByType(type: String): PagingSource<Int, HistoryEntity>

    @Query("SELECT * FROM history_record WHERE id IN (:ids)")
    suspend fun loadByIdList(ids: List<Int>): List<HistoryEntity>
}