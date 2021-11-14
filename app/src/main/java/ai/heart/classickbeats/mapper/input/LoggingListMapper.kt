package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.model.getLogType
import timber.log.Timber
import javax.inject.Inject


class LoggingListMapper @Inject constructor(
) : Mapper<List<LogEntityNetwork>, List<BaseLogEntity>> {
    override fun map(input: List<LogEntityNetwork>): List<BaseLogEntity> {
        val output: MutableList<BaseLogEntity> = mutableListOf()
        input.forEach { entity ->
            val fields = entity.fields
            val id = entity.id
            val timeStamp = fields.timeStamp
            output.add(
                when (entity.model.getLogType()) {
                    LogType.GlucoseLevel -> {
                        val glucoseValue = fields.glucoseValue ?: -1
                        val tag = fields.glucoseTag ?: -1
                        GlucoseLogEntity(
                            id = id,
                            glucoseLevel = glucoseValue,
                            tag = tag,
                            timeStamp = timeStamp
                        )
                    }
                    LogType.WaterIntake -> {
                        val waterQuantity = fields.waterQuantity ?: -1.0f
                        WaterLogEntity(id = id, quantity = waterQuantity, timeStamp = timeStamp)
                    }
                    LogType.Weight -> {
                        val weight = fields.weight ?: -1.0f
                        WeightLogEntity(id = id, weight = weight, timeStamp = timeStamp)
                    }
                    LogType.BloodPressure -> {
                        val diastolic = fields.diastolic ?: -1
                        val systolic = fields.systolic ?: -1
                        BpLogEntity(
                            id = id,
                            systolic = systolic,
                            diastolic = diastolic,
                            timeStamp = timeStamp
                        )
                    }
                    else -> {
                        Timber.e("unhandled log entity type")
                        BaseLogEntity(LogType.Weight)
                    }
                }
            )
        }
        return output.toList()
    }
}
