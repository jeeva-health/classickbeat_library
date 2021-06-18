package ai.heart.classickbeats.model.entity

data class MedicineLogEntity(
    val name: String,
    val dosage: Float,
    val timeString: String,
    val note: String? = null
)
