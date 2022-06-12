package ai.heart.classickbeats.ui.ppg.fragment.my_health

data class BoardModel(
    val icon: Int,
    val action: String,
    val value: String,
    val unit: String,
    val viewGraph: Boolean
) {
    companion object {
        const val BLOOD_PRESSURE = "Blood pressure"
        const val BLOOD_GLUCOSE = "Blood Glucose"
        const val BODY_WEIGHT = "Body Weight"
        const val HEART_RATE = "Heart Rate"
        const val WATER_INTAKE = "Water Intake"
    }
}

