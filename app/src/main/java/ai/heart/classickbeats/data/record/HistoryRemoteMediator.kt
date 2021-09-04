package ai.heart.classickbeats.data.record

import ai.heart.classickbeats.data.db.AppDatabase
import ai.heart.classickbeats.data.record.cache.HistoryRemoteKey
import ai.heart.classickbeats.mapper.input.HistoryRecordNetworkDbMapper
import ai.heart.classickbeats.model.HistoryRecordDatabase
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bumptech.glide.load.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

const val HISTORY_STARTING_PAGE_INDEX = 1

@ExperimentalPagingApi
class HistoryRemoteMediator @Inject constructor(
    private val service: RecordApiService,
    private val database: AppDatabase,
    private val historyRecordNetworkDbMapper: HistoryRecordNetworkDbMapper
) :
    RemoteMediator<Int, HistoryRecordDatabase>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, HistoryRecordDatabase>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: HISTORY_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        Timber.i("loadType: $loadType, page to be loaded: $page")

        try {
            val apiResponse = service.getHistoryData(page)

            // TODO: Ritesh: Add isSuccessful check
            val loggingList = apiResponse.responseData.historyPaginatedData.loggingList
            val loggingListDb = loggingList.map { historyRecordNetworkDbMapper.map(it) }
            val endOfPaginationReached =
                apiResponse.responseData.historyPaginatedData.nextPage == null
            database.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    database.historyRemoteKeyDao().clearRemoteKeys()
                    database.historyDao().deleteAll()
                }
                val prevKey = if (page == HISTORY_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = loggingList.map {
                    HistoryRemoteKey(historyId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.historyRemoteKeyDao().insertAll(keys)
                database.historyDao().insertAll(loggingListDb)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, HistoryRecordDatabase>): HistoryRemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { record ->
                database.historyRemoteKeyDao().remoteKeysHistoryId(record.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, HistoryRecordDatabase>): HistoryRemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { record ->
                database.historyRemoteKeyDao().remoteKeysHistoryId(record.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, HistoryRecordDatabase>): HistoryRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { recordId ->
                database.historyRemoteKeyDao().remoteKeysHistoryId(recordId)
            }
        }
    }
}