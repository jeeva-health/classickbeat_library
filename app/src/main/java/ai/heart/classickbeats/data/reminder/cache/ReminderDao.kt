package ai.heart.classickbeats.data.reminder.cache

import ai.heart.classickbeats.model.entity.ReminderEntity
import androidx.room.*


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
    suspend fun selectNetwork(id: Int): List<ReminderEntity>

    @Query("SELECT * FROM reminders")
    suspend fun getAll(): List<ReminderEntity>

    @Delete
    suspend fun delete(reminderEntity: ReminderEntity)

    @Query("DELETE FROM reminders")
    suspend fun deleteAll()
}