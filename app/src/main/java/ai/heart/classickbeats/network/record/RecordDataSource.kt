package ai.heart.classickbeats.network.record

import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.model.response.GraphDataResponse
import ai.heart.classickbeats.model.response.HistoryListResponse
import ai.heart.classickbeats.model.response.LoggingListResponse
import ai.heart.classickbeats.model.response.ScanDetailResponse
import ai.heart.classickbeats.shared.result.Result

interface RecordDataSource {

    suspend fun recordPPG(ppgEntity: PPGEntity): Result<Long>

    suspend fun updatePPG(ppgId: Long, ppgEntity: PPGEntity): Result<Boolean>

    suspend fun getScanDetails(id: Long): Result<ScanDetailResponse.ResponseData>

    suspend fun getLoggingData(): Result<LoggingListResponse.LoggingData>

    suspend fun recordBloodPressure(pressureLogEntity: PressureLogEntity): Result<Long>

    suspend fun recordGlucoseLevel(glucoseLogEntity: GlucoseLogEntity): Result<Long>

    suspend fun recordWaterIntake(waterLogEntity: WaterLogEntity): Result<Long>

    suspend fun recordWeight(weightLogEntity: WeightLogEntity): Result<Long>

    suspend fun getGraphData(
        model: String,
        type: String,
        startDate: String,
        endDate: String
    ): Result<List<GraphDataResponse.ResponseData>>

    suspend fun getHistoryListData(
        model: String,
        startDate: String,
        endDate: String
    ): Result<HistoryListResponse.ResponseData>
}