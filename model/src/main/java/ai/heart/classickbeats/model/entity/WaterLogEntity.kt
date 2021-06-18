package ai.heart.classickbeats.model.entity

data class WaterLogEntity(
    val quantity: Float,
    val timeString: String,
    val note: String? = null
)
