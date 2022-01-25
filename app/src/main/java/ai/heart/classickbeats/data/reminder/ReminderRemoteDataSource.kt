package ai.heart.classickbeats.data.reminder

import ai.heart.classickbeats.model.entity.ReminderEntity
import ai.heart.classickbeats.shared.data.BaseRemoteDataSource
import ai.heart.classickbeats.shared.network.SessionManager
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderRemoteDataSource internal constructor(
    private val reminderApiService: ReminderApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    sessionManager: SessionManager
) : BaseRemoteDataSource(sessionManager), ReminderDataSource {

    override suspend fun addReminder(reminderEntity: ReminderEntity): Result<ReminderEntity> =
        withContext(ioDispatcher) {
            val response = safeApiCall { reminderApiService.add(reminderEntity) }
            if (response.succeeded) {
                val entity = response.data!!.responseData!!
                Result.Success(entity)
            } else {
                Result.Error(response.error)
            }
        }

    override suspend fun updateReminder(reminderEntity: ReminderEntity): Result<ReminderEntity> =
        withContext(ioDispatcher) {
            val id = reminderEntity.id!!
            val response = safeApiCall { reminderApiService.update(id, reminderEntity) }
            if (response.succeeded) {
                val entity = response.data!!.responseData!!
                Result.Success(entity)
            } else {
                Result.Error(response.error)
            }
        }

    override suspend fun deleteReminder(reminderEntity: ReminderEntity): Result<ReminderEntity> =
        withContext(ioDispatcher) {
            val id = reminderEntity.id!!
            val response = safeApiCall { reminderApiService.delete(id) }
            if (response.succeeded) {
                val entity = response.data!!.responseData!!
                Result.Success(entity)
            } else {
                Result.Error(response.error)
            }
        }

    override suspend fun getAllReminder(): Result<List<ReminderEntity>> =
        withContext(ioDispatcher) {
            val response = safeApiCall { reminderApiService.getAll() }
            if (response.succeeded) {
                val entries = response.data!!.responseData!!
                Result.Success(entries)
            } else {
                Result.Error(response.error)
            }
        }
}
