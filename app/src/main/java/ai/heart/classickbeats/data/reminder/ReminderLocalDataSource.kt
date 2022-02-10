package ai.heart.classickbeats.data.reminder

import ai.heart.classickbeats.data.reminder.cache.ReminderDao
import ai.heart.classickbeats.model.entity.ReminderEntity
import ai.heart.classickbeats.shared.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ReminderLocalDataSource internal constructor(
    private val reminderDao: ReminderDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ReminderDataSource {

    override suspend fun addReminder(reminderEntity: ReminderEntity): Result<ReminderEntity> =
        withContext(ioDispatcher) {
            try {
                reminderDao.insert(reminderEntity)
                Result.Success(reminderEntity)
            } catch (e: Exception) {
                Result.Error(e.message)
            }
        }

    override suspend fun updateReminder(
        reminderId: Long,
        reminderEntity: ReminderEntity
    ): Result<ReminderEntity> =
        withContext(ioDispatcher) {
            try {
                reminderDao.update(reminderEntity)
                Result.Success(reminderEntity)
            } catch (e: Exception) {
                Result.Error(e.message)
            }
        }

    override suspend fun deleteReminder(
        reminderId: Long,
        reminderEntity: ReminderEntity
    ): Result<ReminderEntity> =
        withContext(ioDispatcher) {
            try {
                reminderDao.delete(reminderEntity)
                Result.Success(reminderEntity)
            } catch (e: Exception) {
                Result.Error(e.message)
            }
        }

    fun getAllReminder(): Flow<List<ReminderEntity>> =
        try {
            val reminderList = reminderDao.getAll()
            reminderList
        } catch (e: Exception) {
            throw Exception("Error fetching reminder list from database")

        }

    suspend fun getReminder(reminderId: Long): Result<ReminderEntity> =
        withContext(ioDispatcher) {
            try {
                val entity = reminderDao.selectNetwork(reminderId).first()
                Result.Success(entity)
            } catch (e: Exception) {
                Result.Error(e.message)
            }
        }

    suspend fun insertAllReminder(reminderList: List<ReminderEntity>): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                reminderDao.insertAll(reminderList)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e.message)
            }
        }
}
