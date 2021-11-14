package ai.heart.classickbeats.data.logging

import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.model.response.*
import ai.heart.classickbeats.shared.data.BaseRemoteDataSource
import ai.heart.classickbeats.shared.network.SessionManager
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class LoggingRemoteDataSource internal constructor(
    private val loggingApiService: LoggingApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    sessionManager: SessionManager
) : BaseRemoteDataSource(sessionManager), LoggingDataSource {

    override suspend fun getLoggingData(): Result<LoggingListResponse.LoggingData> {
        val response = safeApiCall { loggingApiService.getLoggingData() }
        if (response.succeeded) {
            val data = response.data!!.responseData
            return Result.Success(data)
        }
        return Result.Error(response.error)
    }

    override suspend fun recordBloodPressure(bpLogEntity: BpLogEntity): Result<Long> =
        withContext(ioDispatcher) {
            val response = safeApiCall { loggingApiService.recordBloodPressure(bpLogEntity) }
            if (response.succeeded) {
                val id = response.data?.responseData?.id ?: -1L
                return@withContext Result.Success(id)
            }
            return@withContext Result.Error(response.error)
        }


    override suspend fun recordGlucoseLevel(glucoseLogEntity: GlucoseLogEntity): Result<Long> =
        withContext(ioDispatcher) {
            val response = safeApiCall { loggingApiService.recordGlucoseLevel(glucoseLogEntity) }
            if (response.succeeded) {
                val id = response.data?.responseData?.id ?: -1L
                return@withContext Result.Success(id)
            }
            return@withContext Result.Error(response.error)
        }


    override suspend fun recordWaterIntake(waterLogEntity: WaterLogEntity): Result<Long> =
        withContext(ioDispatcher) {
            val response = safeApiCall { loggingApiService.recordWaterIntake(waterLogEntity) }
            if (response.succeeded) {
                val id = response.data?.responseData?.id ?: -1L
                return@withContext Result.Success(id)
            }
            return@withContext Result.Error(response.error)
        }


    override suspend fun recordWeight(weightLogEntity: WeightLogEntity): Result<Long> =
        withContext(ioDispatcher) {
            val response = safeApiCall { loggingApiService.recordWeight(weightLogEntity) }
            if (response.succeeded) {
                val id = response.data?.responseData?.id ?: -1L
                return@withContext Result.Success(id)
            }
            return@withContext Result.Error(response.error)
        }
}
