package ai.heart.classickbeats.data.record

import ai.heart.classickbeats.data.db.AppDatabase
import ai.heart.classickbeats.data.record.cache.HistoryRemoteKey
import ai.heart.classickbeats.model.HistoryRecord
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bumptech.glide.load.HttpException
import java.io.IOException

const val HISTORY_STARTING_PAGE_INDEX = 1

@ExperimentalPagingApi
class HistoryRemoteMediator(
    private val service: RecordApiService,
    private val database: AppDatabase
) :
    RemoteMediator<Int, HistoryRecord>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, HistoryRecord>
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

        try {
            val apiResponse = service.getHistoryData(page)

            // TODO: Ritesh: Add isSuccessful check
            val loggingList = apiResponse.responseData.historyPaginatedData.loggingList
            val endOfPaginationReached = loggingList.isEmpty()
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
                database.historyDao().insertAll(loggingList)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, HistoryRecord>): HistoryRemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { record ->
                database.historyRemoteKeyDao().remoteKeysHistoryId(record.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, HistoryRecord>): HistoryRemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { record ->
                database.historyRemoteKeyDao().remoteKeysHistoryId(record.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, HistoryRecord>): HistoryRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { recordId ->
                database.historyRemoteKeyDao().remoteKeysHistoryId(recordId)
            }
        }
    }
}