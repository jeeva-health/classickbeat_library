package ai.heart.classickbeats.data.reminder

import ai.heart.classickbeats.model.entity.ReminderEntity
import ai.heart.classickbeats.shared.result.Result

interface ReminderDataSource {

    suspend fun addReminder(reminderEntity: ReminderEntity): Result<ReminderEntity>

    suspend fun updateReminder(reminderEntity: ReminderEntity): Result<Unit>

    suspend fun deleteReminder(reminderEntity: ReminderEntity): Result<Unit>

    suspend fun getAllReminder(): Result<List<ReminderEntity>>
}