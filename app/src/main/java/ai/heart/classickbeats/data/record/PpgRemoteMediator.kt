package ai.heart.classickbeats.data.record

import ai.heart.classickbeats.data.db.AppDatabase
import ai.heart.classickbeats.model.entity.PPGEntity
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator

@ExperimentalPagingApi
class PpgRemoteMediator(private val service: RecordApiService, private val database: AppDatabase) :
    RemoteMediator<Int, PPGEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PPGEntity>
    ): MediatorResult {
        TODO("Not yet implemented")
    }
}