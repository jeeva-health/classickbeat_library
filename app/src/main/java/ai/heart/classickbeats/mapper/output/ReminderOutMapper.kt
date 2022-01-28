package ai.heart.classickbeats.mapper.output

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.Reminder
import ai.heart.classickbeats.model.entity.ReminderEntity
import ai.heart.classickbeats.model.toInt
import javax.inject.Inject

class ReminderOutMapper @Inject constructor() : Mapper<Reminder, ReminderEntity> {

    override fun map(input: Reminder): ReminderEntity {
        return ReminderEntity(
            id = null,
            dayOfWeek = input.frequency.map { it.toInt() },
            isActive = input.isReminderActive,
            isSet = input.isReminderSet,
            notes = null,
            time = input.time.toSerializeString(),
            type = input.type.toInt()
        )
    }
}
