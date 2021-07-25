package ai.heart.classickbeats.data.record

import ai.heart.classickbeats.mapper.input.HistoryListMapper
import ai.heart.classickbeats.model.entity.BaseLogEntity
import androidx.paging.PagingSource
import androidx.paging.PagingState

class HistoryListPagingSource(
    private val recordApiService: RecordApiService,
    private val historyListMapper: HistoryListMapper
) :
    PagingSource<Int, BaseLogEntity>() {

    override fun getRefreshKey(state: PagingState<Int, BaseLogEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BaseLogEntity> {
        val position = params.key ?: 1

        return try {
            val response = recordApiService.getHistoryData(position)
            val paginatedResponse = response.responseData.historyPaginatedData
            val historyList = historyListMapper.map(paginatedResponse.loggingList)
            val previousPageKey = if (position > 1) position - 1 else null
            val nextPageKey = if (paginatedResponse.nextPage == null) null else position + 1
            LoadResult.Page(
                data = historyList,
                prevKey = previousPageKey,
                nextKey = nextPageKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}