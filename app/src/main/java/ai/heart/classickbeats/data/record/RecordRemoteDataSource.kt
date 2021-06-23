package ai.heart.classickbeats.data.record

import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.model.response.HistoryResponse
import ai.heart.classickbeats.model.response.LoggingListResponse
import ai.heart.classickbeats.model.response.SdnnListResponse
import ai.heart.classickbeats.shared.data.BaseRemoteDataSource
import ai.heart.classickbeats.shared.network.SessionManager
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecordRemoteDataSource internal constructor(
    private val recordApiService: RecordApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    sessionManager: SessionManager
) : BaseRemoteDataSource(sessionManager), RecordDataSource {

    override suspend fun recordPPG(ppgEntity: PPGEntity): Result<Long> =
        withContext(ioDispatcher) {
            val response = safeApiCall { recordApiService.recordPPG(ppgEntity) }
            if (response.succeeded) {
                val ppgId = response.data?.id ?: -1L
                return@withContext Result.Success(ppgId)
            }
            return@withContext Result.Error(response.error)
        }

    override suspend fun updatePPG(ppgId: Long, ppgEntity: PPGEntity): Result<Boolean> =
        withContext(ioDispatcher) {
            val response = safeApiCall { recordApiService.updatePPG(ppgId, ppgEntity) }
            if (response.succeeded) {
                return@withContext Result.Success(true)
            }
            return@withContext Result.Error(response.error)
        }

    override suspend fun getSdnnList(): Result<SdnnListResponse.Data> =
        withContext(ioDispatcher) {
            val response = safeApiCall { recordApiService.getSdnnList() }
            if (response.succeeded) {
                val data = response.data!!.responseData
                return@withContext Result.Success(data)
            }
            return@withContext Result.Error(response.error)
        }

    override suspend fun getLoggingData(): Result<LoggingListResponse.LoggingData> =
        withContext(ioDispatcher) {
            val response = safeApiCall { recordApiService.getLoggingData() }
            if (response.succeeded) {
                val data = response.data!!.responseData
                return@withContext Result.Success(data)
            }
            return@withContext Result.Error(response.error)
        }

    override suspend fun getHistoryData(): Result<HistoryResponse.LoggingData> =
        withContext(ioDispatcher) {
            val response = safeApiCall { recordApiService.getHistoryData() }
            if (response.succeeded) {
                val data = response.data!!.responseData
                return@withContext Result.Success(data)
            }
            return@withContext Result.Error(response.error)
        }

    override suspend fun recordBloodPressure(bpLogEntity: BpLogEntity): Result<Long> =
        withContext(ioDispatcher) {
            val response = safeApiCall { recordApiService.recordBloodPressure(bpLogEntity) }
            if (response.succeeded) {
                val id = response.data?.id ?: -1L
                return@withContext Result.Success(id)
            }
            return@withContext Result.Error(response.error)
        }


    override suspend fun recordGlucoseLevel(glucoseLogEntity: GlucoseLogEntity): Result<Long> =
        withContext(ioDispatcher) {
            val response = safeApiCall { recordApiService.recordGlucoseLevel(glucoseLogEntity) }
            if (response.succeeded) {
                val id = response.data?.id ?: -1L
                return@withContext Result.Success(id)
            }
            return@withContext Result.Error(response.error)
        }


    override suspend fun recordWaterIntake(waterLogEntity: WaterLogEntity): Result<Long> =
        withContext(ioDispatcher) {
            val response = safeApiCall { recordApiService.recordWaterIntake(waterLogEntity) }
            if (response.succeeded) {
                val id = response.data?.id ?: -1L
                return@withContext Result.Success(id)
            }
            return@withContext Result.Error(response.error)
        }


    override suspend fun recordWeight(weightLogEntity: WeightLogEntity): Result<Long> =
        withContext(ioDispatcher) {
            val response = safeApiCall { recordApiService.recordWeight(weightLogEntity) }
            if (response.succeeded) {
                val id = response.data?.id ?: -1L
                return@withContext Result.Success(id)
            }
            return@withContext Result.Error(response.error)
        }
}