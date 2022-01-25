package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.*
import ai.heart.classickbeats.model.entity.ReminderEntity
import javax.inject.Inject

class ReminderInMapper @Inject constructor() : Mapper<ReminderEntity, Reminder> {

    override fun map(input: ReminderEntity): Reminder {
        val id = input.id ?: -1
        val time = input.time?.serializeStringToTime() ?: Time(-1, -1)
        val type = input.type?.toReminderType() ?: Reminder.Type.PPG
        val frequency = input.dayOfWeek?.map { it.toReminderDayOfWeek() } ?: emptyList()
        val isReminderSet = input.isSet ?: false
        val isReminderActive = input.isActive ?: false
        return Reminder(
            _id = id,
            time = time,
            type = type,
            frequency = frequency,
            isReminderSet = isReminderSet,
            isReminderActive = isReminderActive
        )
    }
}