package ai.heart.classickbeatslib.model.entity

enum class LogType {
    BloodPressure,
    GlucoseLevel,
    WaterIntake,
    Weight,
    Medicine,
    PPG
}

fun LogType.getStringValue(): String =
    when (this) {
        LogType.BloodPressure -> "record_data.bloodpressure"
        LogType.GlucoseLevel -> "record_data.glucose"
        LogType.WaterIntake -> "record_data.waterintake"
        LogType.Weight -> "record_data.weightlog"
        LogType.PPG -> "record_data.ppg"
        else -> throw Exception("Unknown logType value")
    }

fun LogType.getShortString(): String =
    when (this) {
        LogType.BloodPressure -> "mm Hg"
        LogType.GlucoseLevel -> "mg/dl"
        LogType.WaterIntake -> "liters"
        LogType.Weight -> "kg"
        LogType.Medicine -> throw Exception("Unhandled logType")
        LogType.PPG -> "BPM"
    }

fun String.getLogType(): LogType =
    when (this) {
        "record_data.glucose" -> LogType.GlucoseLevel
        "record_data.waterintake" -> LogType.WaterIntake
        "record_data.weightlog" -> LogType.Weight
        "record_data.bloodpressure" -> LogType.BloodPressure
        "record_data.ppg" -> LogType.PPG
        else -> throw Exception("Unhandled model type")
    }

fun LogType.getDisplayName(): String =
    when (this) {
        LogType.BloodPressure -> "BLOOD PRESSURE"
        LogType.GlucoseLevel -> "BLOOD GLUCOSE LEVEL"
        LogType.WaterIntake -> "WATER INTAKE"
        LogType.Weight -> "WEIGHT"
        LogType.Medicine -> "MEDICINE"
        LogType.PPG -> "HEART RATE"
    }