package ai.heart.classickbeats.model.entity

data class BpLogEntity(
    val systolic: Int,
    val diastolic: Int,
    val timeStamp: String,
    val note: String? = null
)
