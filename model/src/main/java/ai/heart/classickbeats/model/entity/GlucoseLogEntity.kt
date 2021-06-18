package ai.heart.classickbeats.model.entity

data class GlucoseLogEntity(
    val glucoseLevel: Int,
    val timeStamp: String,
    val tag: String,
    val note: String? = null
)
