package ai.heart.classickbeats.shared.domain.exception

open class CustomException(reason: String?) : Exception(reason ?: "Unknown exception")