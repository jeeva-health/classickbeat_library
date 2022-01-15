package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.Reminder
import ai.heart.classickbeats.model.entity.ReminderEntity
import javax.inject.Inject

class ReminderInMapper @Inject constructor() : Mapper<ReminderEntity, Reminder> {

    override fun map(input: ReminderEntity): Reminder {
        TODO("Not yet implemented")
    }
}