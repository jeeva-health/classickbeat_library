package ai.heart.classickbeats.data.reminder

import ai.heart.classickbeats.di.ReminderDataModule.LocalDataSource
import ai.heart.classickbeats.di.ReminderDataModule.RemoteDataSource
import ai.heart.classickbeats.shared.mapper.input.ReminderInMapper
import ai.heart.classickbeats.shared.mapper.output.ReminderOutMapper
import ai.heart.classickbeats.model.Reminder
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReminderRepository @Inject constructor(
    @RemoteDataSource private val remoteDataSource: ReminderRemoteDataSource,
    @LocalDataSource private val localDataSource: ReminderLocalDataSource,
    private val reminderInMapper: ReminderInMapper,
    private val reminderOutMapper: ReminderOutMapper
) {
    suspend fun addReminder(reminder: Reminder): Result<Unit> {
        val entity = reminderOutMapper.map(reminder)
        val response = remoteDataSource.addReminder(entity)
        return if (response.succeeded) {
            val data = response.data!!
            localDataSource.addReminder(data)
            Result.Success(Unit)
        } else {
            Result.Error(response.error)
        }
    }

    suspend fun updateReminder(reminder: Reminder): Result<Unit> {
        val entity = reminderOutMapper.map(reminder)
        val response = remoteDataSource.updateReminder(reminder._id, entity)
        return if (response.succeeded) {
            val data = response.data!!
            localDataSource.updateReminder(reminder._id, data)
            Result.Success(Unit)
        } else {
            Result.Error(response.error)
        }
    }

    suspend fun deleteReminder(reminder: Reminder): Result<Unit> {
        val entity = reminderOutMapper.map(reminder)
        val response = remoteDataSource.deleteReminder(reminder._id, entity)
        return if (response.succeeded) {
            val data = response.data!!
            localDataSource.deleteReminder(reminder._id, data)
            Result.Success(Unit)
        } else {
            Result.Error(response.error)
        }
    }

    suspend fun getReminder(reminderId: Long): Result<Reminder> {
        val response = localDataSource.getReminder(reminderId)
        return if (response.succeeded) {
            val entity = response.data!!
            val reminder = reminderInMapper.map(entity)
            Result.Success(reminder)
        } else {
            Result.Error(response.error)
        }
    }

    fun getReminderLocalList() =
        localDataSource.getAllReminder().map { it.map { reminderInMapper.map(it) } }


    suspend fun getReminderList() {
        val response = remoteDataSource.getAllReminder()
        if (response.succeeded) {
            val entityList = response.data!!
            localDataSource.insertAllReminder(entityList)
        }
    }
}
