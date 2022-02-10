package ai.heart.classickbeats.data.reminder.cache

import ai.heart.classickbeats.model.entity.ReminderEntity
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminderEntity: ReminderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reminderEntityList: List<ReminderEntity>)

    @Update
    suspend fun update(reminderEntity: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE local_id = :localId")
    suspend fun selectLocal(localId: Long): ReminderEntity

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun selectNetwork(id: Long): List<ReminderEntity>

    @Query("SELECT * FROM reminders")
    fun getAll(): Flow<List<ReminderEntity>>

    @Delete
    suspend fun delete(reminderEntity: ReminderEntity)

    @Query("DELETE FROM reminders")
    suspend fun deleteAll()
}