package ai.heart.classickbeats.model

object Constants {

    const val PLAYBACK_CHANNEL_ID = "playback_1"
    const val PLAYBACK__NOTIFICATION_ID = 1023
}

enum class WeightUnits {
    KGS,
    LBS;

    fun valueToEnum(value: Int?) = when (value) {
        2 -> LBS
        else -> KGS
    }

    fun enumToValue(): Int = when (this) {
        LBS -> 2
        else -> 1
    }
}

enum class HeightUnits {
    CMS,
    INCHES;

    fun valueToEnum(value: Int?) = when (value) {
        2 -> INCHES
        else -> CMS
    }

    fun enumToValue(): Int = when (this) {
        INCHES -> 2
        else -> 1
    }
}