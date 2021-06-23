package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.Logging
import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.shared.mapper.Mapper
import timber.log.Timber
import javax.inject.Inject

class LoggingDataMapper @Inject constructor() :
    Mapper<Logging, BaseLogEntity> {
    override fun map(input: Logging): BaseLogEntity {
        val fields = input.fields
        return when (input.model) {
            "record_data.glucose" -> {
                val glucoseValue = fields.glucoseValue ?: -1
                val tag = fields.statusTag ?: -1
                GlucoseLogEntity(glucoseValue, tag)
            }
            "record_data.waterintake" -> {
                val waterQuantity = fields.water?.toFloat() ?: -1.0f
                WaterLogEntity(waterQuantity)
            }
            "record_data.weightlog" -> {
                val weight = fields.weightValue?.toFloat() ?: -1.0f
                WeightLogEntity(weight)
            }
            "record_data.bloodpressure" -> {
                val diastolic = fields.diastolic ?: -1
                val systolic = fields.systolic ?: -1
                BpLogEntity(systolic, diastolic)
            }
            "record_data.ppg" -> {
                val hr = fields.hr?.toFloat() ?: -1.0f
                val stressLevel = fields.stressLevel ?: -1
                PPGEntity(hr = hr, stressLevel = stressLevel)
            }
            else -> {
                Timber.e("unhandled log entity type")
                BaseLogEntity(LogType.Weight)
            }
        }
    }
}