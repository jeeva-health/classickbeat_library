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
                // TODO
            }
            LoadType.PREPEND -> {
                // TODO
            }
            LoadType.APPEND -> {
                // TODO
            }
        }

        try {
            val apiResponse = service.getHistoryData(page)

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
}