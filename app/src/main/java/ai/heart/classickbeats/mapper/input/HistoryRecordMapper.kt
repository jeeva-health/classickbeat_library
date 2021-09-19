package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.HistoryRecordDatabase
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.entity.*
import timber.log.Timber
import javax.inject.Inject

class HistoryRecordMapper @Inject constructor() :
    Mapper<HistoryRecordDatabase, BaseLogEntity> {
    override fun map(input: HistoryRecordDatabase): BaseLogEntity {
        val id = input.id
        val timeStamp = input.timeStamp
        return when (input.model) {
            "record_data.glucose" -> {
                val glucoseValue = input.glucoseValue ?: -1
                val tag = input.glucoseTag ?: -1
                GlucoseLogEntity(
                    id = id,
                    glucoseLevel = glucoseValue,
                    tag = tag,
                    timeStamp = timeStamp
                )
            }
            "record_data.waterintake" -> {
                val waterQuantity = input.water?.toFloat() ?: -1.0f
                WaterLogEntity(id = id, quantity = waterQuantity, timeStamp = timeStamp)
            }
            "record_data.weightlog" -> {
                val weight = input.weightValue?.toFloat() ?: -1.0f
                WeightLogEntity(id = id, weight = weight, timeStamp = timeStamp)
            }
            "record_data.bloodpressure" -> {
                val diastolic = input.diastolic ?: -1
                val systolic = input.systolic ?: -1
                BpLogEntity(
                    id = id,
                    systolic = systolic,
                    diastolic = diastolic,
                    timeStamp = timeStamp
                )
            }
            "record_data.ppg" -> {
                val hr = input.hr?.toFloat() ?: -1.0f
                val stressLevel = input.stressLevel
                PPGEntity(id = id, hr = hr, stressLevel = stressLevel, timeStamp = timeStamp)
            }
            else -> {
                Timber.e("unhandled log entity type")
                BaseLogEntity(LogType.Weight)
            }
        }
    }
}