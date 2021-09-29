package ai.heart.classickbeats.data.record

import ai.heart.classickbeats.data.db.AppDatabase
import ai.heart.classickbeats.data.record.cache.TimelineRemoteKey
import ai.heart.classickbeats.model.TimelineType
import ai.heart.classickbeats.model.entity.TimelineEntity
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
    private val timelineType: TimelineType,
    private val service: RecordApiService,
    private val database: AppDatabase
) :
    RemoteMediator<Int, TimelineEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TimelineEntity>
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
            val apiResponse = service.getTimelineRecord(timelineType.value, page)

            // TODO: Ritesh: Add isSuccessful check
            val timelineFields = apiResponse.responseData.timelinePaginatedData.fields
            val endOfPaginationReached =
                apiResponse.responseData.timelinePaginatedData.next == null
            database.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    database.timelineRemoteKeyDao().clearRemoteKeys()
                    database.timelineDao().deleteAll()
                }
                val prevKey = if (page == TIMELINE_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = timelineFields.map {
                    TimelineRemoteKey(timelineId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                timelineFields.forEach {
                    it.type = when {
                        it.weeklyAvg != null || it.diastolicWeeklyAvg != null || it.hrWeeklyAvg != null -> TimelineType.Weekly
                        it.monthlyAvg != null || it.diastolicMonthlyAvg != null || it.hrMonthlyAvg != null -> TimelineType.Monthly
                        else -> TimelineType.Daily
                    }.value
                }
                database.timelineRemoteKeyDao().insertAll(keys)
                database.timelineDao().insertAll(timelineFields)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, TimelineEntity>): TimelineRemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { record ->
                database.timelineRemoteKeyDao().remoteKeysTimelineId(record.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, TimelineEntity>): TimelineRemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { record ->
                database.timelineRemoteKeyDao().remoteKeysTimelineId(record.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, TimelineEntity>): TimelineRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { recordId ->
                database.timelineRemoteKeyDao().remoteKeysTimelineId(recordId)
            }
        }
    }
}