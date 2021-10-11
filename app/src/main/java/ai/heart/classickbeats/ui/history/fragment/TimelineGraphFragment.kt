package ai.heart.classickbeats.ui.history.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentTimelineGraphBinding
import ai.heart.classickbeats.graph.BarGraph
import ai.heart.classickbeats.model.GraphData
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.TimelineType
import ai.heart.classickbeats.model.entity.BaseLogEntity
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.model.getShortString
import ai.heart.classickbeats.shared.util.toDateString
import ai.heart.classickbeats.shared.util.toMonthString
import ai.heart.classickbeats.shared.util.toWeekString
import ai.heart.classickbeats.ui.history.GraphHistoryAdapter
import ai.heart.classickbeats.ui.history.viewmodel.TimelineViewModel
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import com.github.mikephil.charting.charts.BarChart
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

    private lateinit var barChart: BarChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val (logType, timelineType, startDate) = args

        timelineViewModel.getGraphData(logType, timelineType, startDate)

        navController = findNavController()

        barChart = binding.chart.apply {
            setDrawGridBackground(false)
            description.isEnabled = false
            axisLeft.setDrawLabels(true)
            axisLeft.setDrawAxisLine(true)
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawLabels(false)
            axisRight.setDrawAxisLine(false)
            axisRight.setDrawGridLines(false)
            xAxis.setDrawLabels(true)
            xAxis.setDrawAxisLine(true)
            xAxis.setDrawGridLines(false)
            legend.isEnabled = false
            setNoDataText("")
            invalidate()
            requestLayout()
        }

        graphHistoryAdapter = GraphHistoryAdapter(requireContext(), historyItemClickListener)

        timelineViewModel.graphData.observe(viewLifecycleOwner, {
            it?.let { showUI(it) }
        })

        binding.backArrow.setSafeOnClickListener {
            navController.navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()

        timelineViewModel.graphData.value?.let { showUI(it) }
    }

    private fun showUI(graphData: GraphData) {
        binding.apply {
            val (dateRangeTxt, timelineTypeStr) = when (graphData.timelineType) {
                TimelineType.Daily -> Pair(graphData.startDate.toDateString(), "DAILY")
                TimelineType.Weekly -> Pair(graphData.startDate.toWeekString(), "WEEKLY")
                TimelineType.Monthly -> Pair(graphData.startDate.toMonthString(), "MONTHLY")
            }
            timeRange.text = dateRangeTxt
            graphLabel.text = "$timelineTypeStr (in ${graphData.model.getShortString()})"
            BarGraph.draw(requireContext(), chart, graphData.valueList)
        }
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