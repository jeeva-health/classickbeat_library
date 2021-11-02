package ai.heart.classickbeats.mapper.output

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.Reminder
import ai.heart.classickbeats.model.entity.ReminderEntity
import javax.inject.Inject

class ReminderOutMapper @Inject constructor(): Mapper<Reminder, ReminderEntity> {

    override fun map(input: Reminder): ReminderEntity {
        TODO("Not yet implemented")
    }
}