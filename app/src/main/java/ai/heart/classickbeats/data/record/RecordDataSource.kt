package ai.heart.classickbeats.data.record

import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.model.response.LoggingListResponse
import ai.heart.classickbeats.model.response.ScanDetailResponse
import ai.heart.classickbeats.model.response.SdnnListResponse
import ai.heart.classickbeats.shared.result.Result

interface RecordDataSource {

    suspend fun recordPPG(ppgEntity: PPGEntity): Result<Long>

    suspend fun updatePPG(ppgId: Long, ppgEntity: PPGEntity): Result<Boolean>

    suspend fun getSdnnList(): Result<SdnnListResponse.Data>

    suspend fun getScanDetails(id: Int): Result<ScanDetailResponse.ResponseData>

    suspend fun getLoggingData(): Result<LoggingListResponse.LoggingData>

    suspend fun recordBloodPressure(bpLogEntity: BpLogEntity): Result<Long>

    suspend fun recordGlucoseLevel(glucoseLogEntity: GlucoseLogEntity): Result<Long>

    suspend fun recordWaterIntake(waterLogEntity: WaterLogEntity): Result<Long>

    suspend fun recordWeight(weightLogEntity: WeightLogEntity): Result<Long>
}