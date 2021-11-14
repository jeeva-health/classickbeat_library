package ai.heart.classickbeats.data.record

import ai.heart.classickbeats.data.db.AppDatabase
import ai.heart.classickbeats.data.record.cache.TimelineRemoteKey
import ai.heart.classickbeats.mapper.input.HistoryRecordNetworkDbMapper
import ai.heart.classickbeats.model.entity.TimelineEntityDatabase
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bumptech.glide.load.HttpException
import timber.log.Timber
import java.io.IOException

const val TIMELINE_STARTING_PAGE_INDEX = 1


@ExperimentalPagingApi
class TimelineRemoteMediator constructor(
    private val service: RecordApiService,
    private val database: AppDatabase,
    private val historyRecordNetworkDbMapper: HistoryRecordNetworkDbMapper
) :
    RemoteMediator<Int, TimelineEntityDatabase>() {

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TimelineEntityDatabase>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: TIMELINE_STARTING_PAGE_INDEX
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
                    database.timelineRemoteKeyDao().clearRemoteKeys()
                    database.timelineDao().deleteAll()
                }
                val prevKey = if (page == TIMELINE_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = loggingList.map {
                    TimelineRemoteKey(timelineId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.timelineRemoteKeyDao().insertAll(keys)
                database.timelineDao().insertAll(loggingListDb)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, TimelineEntityDatabase>): TimelineRemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { record ->
                database.timelineRemoteKeyDao().remoteKeysTimelineId(record.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, TimelineEntityDatabase>): TimelineRemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { record ->
                database.timelineRemoteKeyDao().remoteKeysTimelineId(record.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, TimelineEntityDatabase>): TimelineRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { recordId ->
                database.timelineRemoteKeyDao().remoteKeysTimelineId(recordId)
            }
        }
    }
}
