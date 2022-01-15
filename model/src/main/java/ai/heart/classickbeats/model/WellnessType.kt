package ai.heart.classickbeats.model

enum class WellnessType {
    SLEEP,
    BLOOD_PRESSURE,
    ANGER,
    STRESS,
    IMMUNITY
}

fun Int.getWellnessType(): WellnessType =
    when (this) {
        0 -> WellnessType.SLEEP
        1 -> WellnessType.BLOOD_PRESSURE
        2 -> WellnessType.ANGER
        3 -> WellnessType.STRESS
        4 -> WellnessType.IMMUNITY
        else -> throw Exception("Unhandled wellness type")
    }

fun WellnessType.getInt(): Int =
    when (this) {
        WellnessType.SLEEP -> 0
        WellnessType.BLOOD_PRESSURE -> 1
        WellnessType.ANGER -> 2
        WellnessType.STRESS -> 3
        WellnessType.IMMUNITY -> 4
    }
