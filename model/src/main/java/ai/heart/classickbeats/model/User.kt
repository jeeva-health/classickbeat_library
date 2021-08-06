package ai.heart.classickbeats.model

data class User(
    val fullName: String,
    val gender: Gender,
    val height: Double,
    val heightUnit: HeightUnits = HeightUnits.INCHES,
    val weight: Double,
    val weightUnit: WeightUnits = WeightUnits.KGS,
    val dob: String,
    val emailAddress: String? = null,
    val isRegistered: Boolean = false
)