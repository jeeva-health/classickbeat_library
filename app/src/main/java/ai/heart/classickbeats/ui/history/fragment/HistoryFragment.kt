package ai.heart.classickbeats.ui.history.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentHistoryBinding
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.Timeline
import ai.heart.classickbeats.model.TimelineType
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.history.HistoryAdapter
import ai.heart.classickbeats.ui.history.viewmodel.HistoryViewModel
import ai.heart.classickbeats.utils.hideLoadingBar
import ai.heart.classickbeats.utils.showLoadingBar
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history) {

    private val binding by viewBinding(FragmentHistoryBinding::bind)

    private val historyViewModel: HistoryViewModel by activityViewModels()

    private lateinit var navController: NavController

    private lateinit var historyAdapter: HistoryAdapter

    private var selectedTimelineType: TimelineType = TimelineType.Daily

    private var job: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        historyAdapter = HistoryAdapter(requireContext(), timelineItemClickListener)

        binding.apply {
            historyRv.adapter = historyAdapter
            dailyCategory.isSelected = true
            arrayOf(dailyCategory, weeklyCategory, monthlyCategory).forEach { category ->
                category.setOnClickListener {
                    dailyCategory.isSelected = false
                    weeklyCategory.isSelected = false
                    monthlyCategory.isSelected = false

                    category.isSelected = true

                    selectedTimelineType = when (category.id) {
                        dailyCategory.id -> {
                            TimelineType.Daily
                        }
                        weeklyCategory.id -> {
                            TimelineType.Weekly
                        }
                        monthlyCategory.id -> {
                            TimelineType.Monthly
                        }
                        else -> {
                            TimelineType.Daily
                        }
                    }
                    job?.cancel()
                    job = lifecycleScope.launch {
                        historyViewModel.getTimelineData(selectedTimelineType)
                            .collectLatest { pagingData ->
                                historyAdapter.submitData(pagingData)
                            }
                    }
                }
            }
        }

        historyViewModel.showLoading.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                showLoadingBar()
            } else {
                hideLoadingBar()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        job?.cancel()
        job = lifecycleScope.launchWhenResumed {
            historyViewModel.getTimelineData(selectedTimelineType).collectLatest { pagingData ->
                historyAdapter.submitData(pagingData)
            }
        }
    }

    private val timelineItemClickListener = fun(data: Timeline) {
        val model = data.model
        val timelineType = data.type
        val startDate = data.date
        navigateToGraphFragment(model, timelineType, startDate)
    }

    private fun navigateToGraphFragment(
        model: LogType,
        timelineType: TimelineType,
        startDate: Date
    ) {
        val action = HistoryFragmentDirections.actionHistoryFragmentToHistoryGraphFragment(
            logType = model,
            dataType = timelineType,
            startDate = startDate
        )
        navController.navigate(action)
    }
}