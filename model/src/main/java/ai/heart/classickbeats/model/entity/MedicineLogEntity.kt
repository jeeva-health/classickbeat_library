package ai.heart.classickbeats.model.entity

import ai.heart.classickbeats.model.LogType

data class MedicineLogEntity(
    @Transient
    val id: Long = -1,
    val name: String,
    val dosage: Float,
    val timeStamp: String? = null,
    val note: String? = null
) : BaseLogEntity(LogType.Medicine)