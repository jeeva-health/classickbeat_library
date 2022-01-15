package ai.heart.classickbeats.data.reminder

import ai.heart.classickbeats.mapper.input.ReminderInMapper
import ai.heart.classickbeats.mapper.output.ReminderOutMapper
import ai.heart.classickbeats.model.Reminder
import javax.inject.Inject

class ReminderRepository @Inject constructor(
    private val remoteDataSource: ReminderRemoteDataSource,
    private val localDataSource: ReminderLocalDataSource,
    private val reminderInMapper: ReminderInMapper,
    private val reminderOutMapper: ReminderOutMapper
) {

    suspend fun addReminder(reminder: Reminder) {
        val entity = reminderOutMapper.map(reminder)
        val remoteEntity = remoteDataSource.addReminder(entity)
    }
}