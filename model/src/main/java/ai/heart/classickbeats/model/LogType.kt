package ai.heart.classickbeats.model

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

fun String.getLogType(): LogType =
    when (this) {
        "record_data.glucose" -> LogType.GlucoseLevel
        "record_data.waterintake" -> LogType.WaterIntake
        "record_data.weightlog" -> LogType.Weight
        "record_data.bloodpressure" -> LogType.BloodPressure
        "record_data.ppg" -> LogType.PPG
        else -> throw Exception("Unhandled model type")
    }