package ai.heart.classickbeats.model

data class User(
    val fullName: String,
    val gender: Gender,
    val height: Double,
    val isHeightInches: Boolean = true,
    val weight: Double,
    val isWeightKgs: Boolean = true,
    val dob: String,
    val emailAddress: String? = null,
    val isRegistered: Boolean = false
)