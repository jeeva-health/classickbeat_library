package ai.heart.classickbeats.domain.model

data class User(
    val id: Long,
    val phoneNumber: String,
    val name: String?,
    val email: String?
)