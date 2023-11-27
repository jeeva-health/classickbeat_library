package ai.heart.classickbeatslib.shared.domain.exception

open class CustomException(reason: String?) : Exception(reason ?: "Unknown exception")