package ai.heart.classickbeats.data.meditation

import ai.heart.classickbeats.shared.mapper.input.MeditationDataMapper
import ai.heart.classickbeats.model.MeditationMedia
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.error
import dagger.hilt.android.scopes.ActivityRetainedScoped
import timber.log.Timber
import javax.inject.Inject

@ActivityRetainedScoped
class MeditationRepository @Inject constructor(
    private val remoteDataSource: MeditationRemoteDataSource,
    private val meditationDataMapper: MeditationDataMapper
) {

    private var meditationList: List<MeditationMedia>? = null

    suspend fun getMeditationList(): Result<List<MeditationMedia>> {
        if (meditationList != null) {
            return Result.Success(meditationList!!)
        }
        val response = remoteDataSource.getMeditationList()
        when (response) {
            is Result.Success -> {
                val entityList = response.data
                val mediaList = entityList.map { meditationDataMapper.map(it) }
                meditationList = mediaList
                return Result.Success(mediaList)
            }
            is Result.Error -> Timber.e(response.exception)
            Result.Loading -> throw IllegalStateException("getMeditationList() response invalid state")
        }
        return Result.Error(response.error)
    }

    suspend fun getMeditationFile(mediationId: Long): Result<MeditationMedia> {
        val response = remoteDataSource.getMeditationFile(mediationId)
        when (response) {
            is Result.Success -> {
                val entity = response.data
                val meditationMedia = meditationDataMapper.map(entity)
                return Result.Success(meditationMedia)
            }
            is Result.Error -> Timber.e(response.exception)
            Result.Loading -> throw IllegalStateException("getMeditationFile() response invalid state")
        }
        return Result.Error(response.error)
    }
}