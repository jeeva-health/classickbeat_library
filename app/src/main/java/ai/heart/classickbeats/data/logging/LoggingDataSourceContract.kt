package ai.heart.classickbeats.data.logging

import ai.heart.classickbeats.model.entity.PressureLogEntity
import ai.heart.classickbeats.model.entity.GlucoseLogEntity
import ai.heart.classickbeats.model.entity.WaterLogEntity
import ai.heart.classickbeats.model.entity.WeightLogEntity
import ai.heart.classickbeats.model.response.LoggingListResponse
import ai.heart.classickbeats.shared.result.Result


interface LoggingDataSourceContract {

    suspend fun getLoggingData(): Result<LoggingListResponse.LoggingData>

    suspend fun recordBloodPressure(pressureLogEntity: PressureLogEntity): Result<Long>

    suspend fun recordGlucoseLevel(glucoseLogEntity: GlucoseLogEntity): Result<Long>

    suspend fun recordWaterIntake(waterLogEntity: WaterLogEntity): Result<Long>

    suspend fun recordWeight(weightLogEntity: WeightLogEntity): Result<Long>
}
