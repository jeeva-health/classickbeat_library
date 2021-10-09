package ai.heart.classickbeats.ui.history.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentTimelineGraphBinding
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.entity.BaseLogEntity
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.ui.history.GraphHistoryAdapter
import ai.heart.classickbeats.ui.history.viewmodel.TimelineViewModel
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TimelineGraphFragment : Fragment(R.layout.fragment_timeline_graph) {

    private val binding by viewBinding(FragmentTimelineGraphBinding::bind)

    private val timelineViewModel: TimelineViewModel by activityViewModels()

    private val args: TimelineGraphFragmentArgs by navArgs()

    private lateinit var navController: NavController

    private lateinit var graphHistoryAdapter: GraphHistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val (logType, timelineType, start_date) = args

        navController = findNavController()

        graphHistoryAdapter = GraphHistoryAdapter(requireContext(), historyItemClickListener)
    }

    private val historyItemClickListener = fun(data: BaseLogEntity) {
        when (data.type) {
            LogType.PPG -> {
                val ppgEntity = data as PPGEntity
                val id = ppgEntity.id
                navigateToScanResultFragment(id)
            }
            else -> {
            }
        }
    }

    private fun navigateToScanResultFragment(id: Long) {
        val action =
            TimelineGraphFragmentDirections.actionTimelineGraphFragmentToScanResultFragment(
                showingHistory = true,
                scanId = id
            )
        navController.navigate(action)
    }
}