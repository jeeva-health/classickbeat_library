package ai.heart.classickbeats.model.entity

data class WeightLogEntity(
    val weight: Float,
    val timeStamp: String,
    val note: String? = null
)
