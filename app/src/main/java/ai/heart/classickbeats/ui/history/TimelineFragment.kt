package ai.heart.classickbeats.ui.history

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentTimelineBinding
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.Timeline
import ai.heart.classickbeats.model.TimelineType
import ai.heart.classickbeats.shared.result.EventObserver
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

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TimelineFragment : Fragment(R.layout.fragment_timeline) {

    private val binding by viewBinding(FragmentTimelineBinding::bind)

    private val timelineViewModel: TimelineViewModel by activityViewModels()

    private lateinit var navController: NavController

    private lateinit var timelineAdapter: TimelineAdapter

    private var selectedTimelineType: TimelineType = TimelineType.Daily

    private var job: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        timelineAdapter = TimelineAdapter(requireContext(), timelineItemClickListener)

        binding.apply {
            historyRv.adapter = timelineAdapter
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
                        timelineViewModel.getTimelineData(selectedTimelineType)
                            .collectLatest { pagingData ->
                                timelineAdapter.submitData(pagingData)
                            }
                    }
                }
            }
        }

        timelineViewModel.showLoading.observe(viewLifecycleOwner, EventObserver {
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
            timelineViewModel.getTimelineData(selectedTimelineType).collectLatest { pagingData ->
                timelineAdapter.submitData(pagingData)
            }
        }
    }

    private val timelineItemClickListener = fun(data: Timeline) {
        when (data.model) {
            LogType.BloodPressure -> TODO()
            LogType.GlucoseLevel -> TODO()
            LogType.WaterIntake -> TODO()
            LogType.Weight -> TODO()
            LogType.Medicine -> TODO()
            LogType.PPG -> TODO()
        }
    }
}