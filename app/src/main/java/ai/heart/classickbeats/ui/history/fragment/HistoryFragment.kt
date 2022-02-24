package ai.heart.classickbeats.ui.history.fragment

import ai.heart.classickbeats.MainActivity
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentHistoryBinding
import ai.heart.classickbeats.model.HistoryType
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.Timeline
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.history.HistoryAdapter
import ai.heart.classickbeats.ui.history.viewmodel.HistoryViewModel
import ai.heart.classickbeats.utils.hideLoadingBar
import ai.heart.classickbeats.utils.setSafeOnClickListener
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

    private var job: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        historyAdapter = HistoryAdapter(requireContext(), timelineItemClickListener)

        (requireActivity() as MainActivity).selectHistoryFragmentInBottomNavigation()

        binding.apply {
            historyRv.adapter = historyAdapter

            historyViewModel.selectedHistoryType.observe(viewLifecycleOwner) {
                dailyCategory.isSelected = false
                weeklyCategory.isSelected = false
                monthlyCategory.isSelected = false
                when (it) {
                    HistoryType.Daily -> {
                        dailyCategory.isSelected = true
                    }
                    HistoryType.Weekly -> {
                        weeklyCategory.isSelected = true

                    }
                    HistoryType.Monthly -> {
                        monthlyCategory.isSelected = true
                    }
                }

                job?.cancel()
                job = lifecycleScope.launch {
                    historyViewModel.getHistoryData(it)
                        .collectLatest { pagingData ->
                            historyAdapter.submitData(pagingData)
                        }
                }
            }

            arrayOf(dailyCategory, weeklyCategory, monthlyCategory).forEach { category ->
                category.setOnClickListener {
                    val historyType = when (category.id) {
                        dailyCategory.id -> {
                            HistoryType.Daily
                        }
                        weeklyCategory.id -> {
                            HistoryType.Weekly
                        }
                        monthlyCategory.id -> {
                            HistoryType.Monthly
                        }
                        else -> {
                            HistoryType.Daily
                        }
                    }
                    historyViewModel.setSelectedHistoryType(historyType)
                }
            }

            arrayOf(switchIcon, timelineTv).forEach {
                it.setSafeOnClickListener {
                    navigateToTimelineFragment()
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
            val historyType = historyViewModel.selectedHistoryType.value ?: HistoryType.Daily
            historyViewModel.getHistoryData(historyType).collectLatest { pagingData ->
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
        timelineType: HistoryType,
        startDate: Date
    ) {
        val action = HistoryFragmentDirections.actionHistoryFragmentToHistoryGraphFragment(
            logType = model,
            dataType = timelineType,
            startDate = startDate
        )
        navController.navigate(action)
    }

    private fun navigateToTimelineFragment() {
        val action = HistoryFragmentDirections.actionHistoryFragmentToTimelineFragment()
        navController.navigate(action)
    }
}