package ai.heart.classickbeats.network.logging

import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.model.response.LoggingListResponse
import ai.heart.classickbeats.shared.result.Result


interface LoggingDataSourceContract {

    suspend fun getLoggingData(): Result<LoggingListResponse.LoggingData>

    suspend fun recordBloodPressure(bpLogEntity: PressureLogEntity): Result<Long>

    suspend fun recordGlucoseLevel(glucoseLogEntity: GlucoseLogEntity): Result<Long>

    suspend fun recordWaterIntake(waterLogEntity: WaterLogEntity): Result<Long>

    suspend fun recordWeight(weightLogEntity: WeightLogEntity): Result<Long>
}
