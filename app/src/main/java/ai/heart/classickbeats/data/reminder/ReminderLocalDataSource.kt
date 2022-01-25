package ai.heart.classickbeats.data.reminder

import ai.heart.classickbeats.data.reminder.cache.ReminderDao
import ai.heart.classickbeats.model.entity.ReminderEntity
import ai.heart.classickbeats.shared.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderLocalDataSource internal constructor(
    private val reminderDao: ReminderDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ReminderDataSource {

    override suspend fun addReminder(reminderEntity: ReminderEntity): Result<ReminderEntity> =
        withContext(ioDispatcher) {
            try {
                val localId = reminderDao.insert(reminderEntity)
                Result.Success(reminderDao.selectLocal(localId))
            } catch (e: Exception) {
                Result.Error(e.message)
            }
        }

    override suspend fun updateReminder(reminderEntity: ReminderEntity): Result<ReminderEntity> =
        withContext(ioDispatcher) {
            try {
                val count = reminderDao.update(reminderEntity)
                if (count == 1L) {
                    Result.Success(reminderEntity)
                } else {
                    Result.Error("Number of rows updated: $count, should be 1")
                }
            } catch (e: Exception) {
                Result.Error(e.message)
            }
        }

    override suspend fun deleteReminder(reminderEntity: ReminderEntity): Result<ReminderEntity> =
        withContext(ioDispatcher) {
            try {
                reminderDao.delete(reminderEntity)
                Result.Success(reminderEntity)
            } catch (e: Exception) {
                Result.Error(e.message)
            }
        }

    override suspend fun getAllReminder(): Result<List<ReminderEntity>> =
        withContext(ioDispatcher) {
            try {
                val reminderList = reminderDao.getAll()
                Result.Success(reminderList)
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
