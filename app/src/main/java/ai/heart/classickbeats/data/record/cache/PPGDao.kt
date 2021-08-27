package ai.heart.classickbeats.data.record.cache

import ai.heart.classickbeats.model.entity.PPGEntity
import androidx.room.*

@Dao
interface PPGDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ppgEntity: PPGEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ppgEntities: List<PPGEntity>)

    @Update
    suspend fun update(ppgEntity: PPGEntity)

    @Delete
    suspend fun delete(ppgEntity: PPGEntity)

    @Query("DELETE FROM ppg_record")
    suspend fun deleteAll()

    @Query("SELECT * FROM ppg_record WHERE id = :id")
    suspend fun load(id: Int): PPGEntity
}