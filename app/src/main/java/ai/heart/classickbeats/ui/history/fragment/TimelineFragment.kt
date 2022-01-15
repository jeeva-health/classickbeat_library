package ai.heart.classickbeats.ui.history.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentTimelineBinding
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.entity.BaseLogEntity
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.history.TimelineAdapter
import ai.heart.classickbeats.ui.history.viewmodel.TimelineViewModel
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
import kotlinx.coroutines.flow.collectLatest


@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TimelineFragment : Fragment(R.layout.fragment_timeline) {

    private val binding by viewBinding(FragmentTimelineBinding::bind)

    private val historyViewModel: TimelineViewModel by activityViewModels()

    private lateinit var navController: NavController

    private lateinit var timelineAdapter: TimelineAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        timelineAdapter = TimelineAdapter(requireContext(), historyItemClickListener)

        binding.apply {
            timelineRv.adapter = timelineAdapter

            backArrow.setSafeOnClickListener {
                navigateToHistoryFragment()
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

        lifecycleScope.launchWhenResumed {
            historyViewModel.getTimelineData().collectLatest { pagingData ->
                timelineAdapter.submitData(pagingData)
            }
        }
    }

    private val historyItemClickListener = fun(data: BaseLogEntity) {
        if (data.type == LogType.PPG) {
            val ppgEntity = data as PPGEntity
            val id = ppgEntity.id
            navigateToScanDetailFragment(id)
        }
    }

    private fun navigateToHistoryFragment() {
        val action = TimelineFragmentDirections.actionTimelineFragmentToHistoryFragment()
        navController.navigate(action)
    }

    private fun navigateToScanDetailFragment(scanId: Long) {
        val action = TimelineFragmentDirections.actionTimelineFragmentToScanResultFragment(
            showingHistory = true,
            scanId = scanId
        )
        navController.navigate(action)
    }
}