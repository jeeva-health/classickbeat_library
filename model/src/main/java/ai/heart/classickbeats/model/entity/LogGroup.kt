package ai.heart.classickbeats.model.entity

data class LogGroup(
    val bpLogs: List<BpLogEntity>,
    val glucoseLogs: List<GlucoseLogEntity>,
    val waterIntakeLogs: List<WaterLogEntity>,
    val weightLogs: List<WeightLogEntity>
)