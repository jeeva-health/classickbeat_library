package ai.heart.classickbeats.data.record.cache

import ai.heart.classickbeats.model.entity.TimelineEntityDatabase
import androidx.paging.PagingSource
import androidx.room.*


@Dao
interface TimelineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timeline: TimelineEntityDatabase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(timelineRecordList: List<TimelineEntityDatabase>)

    @Update
    suspend fun update(timeline: TimelineEntityDatabase)

    @Delete
    suspend fun delete(timeline: TimelineEntityDatabase)

    @Query("DELETE FROM timeline")
    suspend fun deleteAll()

    @Query("SELECT * FROM timeline WHERE id = :id")
    suspend fun load(id: Int): TimelineEntityDatabase

    @Query("SELECT * FROM timeline")
    fun loadAll(): PagingSource<Int, TimelineEntityDatabase>

    @Query("SELECT * FROM timeline WHERE model = :model ORDER BY id DESC LIMIT :limit")
    suspend fun loadTimelineDataByModelAndCount(
        model: String,
        limit: Int
    ): List<TimelineEntityDatabase>

    @Query("SELECT * FROM timeline WHERE model = :model AND timeStamp >= :startTimeStamp ORDER BY id DESC")
    suspend fun loadTimelineDataByModelAndDuration(
        model: String,
        startTimeStamp: String
    ): List<TimelineEntityDatabase>
}
