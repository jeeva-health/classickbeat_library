package ai.heart.classickbeats.data.reminder

import ai.heart.classickbeats.di.ReminderDataModule.LocalDataSource
import ai.heart.classickbeats.di.ReminderDataModule.RemoteDataSource
import ai.heart.classickbeats.mapper.input.ReminderInMapper
import ai.heart.classickbeats.mapper.output.ReminderOutMapper
import ai.heart.classickbeats.model.Reminder
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
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
        val response = remoteDataSource.updateReminder(entity)
        return if (response.succeeded) {
            val data = response.data!!
            localDataSource.updateReminder(data)
            Result.Success(Unit)
        } else {
            Result.Error(response.error)
        }
    }

    suspend fun deleteReminder(reminder: Reminder): Result<Unit> {
        val entity = reminderOutMapper.map(reminder)
        val response = remoteDataSource.deleteReminder(entity)
        return if (response.succeeded) {
            val data = response.data!!
            localDataSource.deleteReminder(data)
            Result.Success(Unit)
        } else {
            Result.Error(response.error)
        }
    }

    suspend fun getReminderList(): Result<List<Reminder>> {
        val response = remoteDataSource.getAllReminder()
        return if (response.succeeded) {
            val entityList = response.data!!
            localDataSource.insertAllReminder(entityList)
            val reminderList = entityList.map { reminderInMapper.map(it) }
            Result.Success(reminderList)
        } else {
            Result.Error(response.error)
        }
    }
}
