package ai.heart.classickbeats.data.record.cache

import ai.heart.classickbeats.model.entity.PPGEntity
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PpgDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ppgEntity: PPGEntity)

    @Update
    suspend fun update(ppgEntity: PPGEntity)

    @Delete
    suspend fun delete(ppgEntity: PPGEntity)

    @Query("SELECT * FROM ppg_record WHERE id = :id")
    suspend fun load(id: Int): PPGEntity

    @Query("SELECT * FROM ppg_record ORDER BY local_id ASC LIMIT :limit")
    fun loadByLimit(limit: Int): Flow<List<PPGEntity>>
}