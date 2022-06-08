package ai.heart.classickbeats.network.logging.cache

import ai.heart.classickbeats.model.entity.LogEntityDatabase
import androidx.room.*


@Dao
interface LoggingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(logEntity: LogEntityDatabase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(logEntityList: List<LogEntityDatabase>)

    @Delete
    suspend fun delete(logEntity: LogEntityDatabase)

    @Query("SELECT * FROM logging WHERE id = :id")
    suspend fun load(id: Long): LogEntityDatabase

    @Query("SELECT * FROM logging WHERE isUploaded = :isUploaded LIMIT :limit")
    suspend fun loadToUpload(isUploaded: Boolean, limit: Int)

    @Update
    suspend fun update(logEntity: LogEntityDatabase)

    @Update
    suspend fun update(logEntityList: List<LogEntityDatabase>)
}
