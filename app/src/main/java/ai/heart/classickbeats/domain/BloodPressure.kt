package ai.heart.classickbeats.domain

import ai.heart.classickbeats.model.entity.PressureLogEntity
import java.time.ZonedDateTime

data class BloodPressure (
    val time: ZonedDateTime,
    val systolicLevel: Float,
    val diastolicLevel: Float,
)

fun BloodPressure.toDto(): PressureLogEntity = PressureLogEntity(
    systolicLevel = this.systolicLevel.toInt(),
    diastolicLevel = this.diastolicLevel.toInt(),
    timeStamp = this.time.toString()
)

fun PressureLogEntity.toDomain(): BloodPressure = BloodPressure(
    systolicLevel = this.systolicLevel.toFloat(),
    diastolicLevel = this.diastolicLevel.toFloat(),
    time = ZonedDateTime.parse(this.timeStamp)
)