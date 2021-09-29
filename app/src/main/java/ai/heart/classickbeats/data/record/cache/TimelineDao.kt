package ai.heart.classickbeats.data.record.cache

import ai.heart.classickbeats.model.entity.TimelineEntity
import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface TimelineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timelineEntity: TimelineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(timelineEntityList: List<TimelineEntity>)

    @Update
    suspend fun update(timelineEntity: TimelineEntity)

    @Delete
    suspend fun delete(timelineEntity: TimelineEntity)

    @Query("DELETE FROM timeline")
    suspend fun deleteAll()

    @Query("SELECT * FROM timeline WHERE id = :id")
    suspend fun load(id: Int): TimelineEntity

    @Query("SELECT * FROM timeline WHERE timeline_type = :type")
    fun loadByType(type: String): PagingSource<Int, TimelineEntity>
}