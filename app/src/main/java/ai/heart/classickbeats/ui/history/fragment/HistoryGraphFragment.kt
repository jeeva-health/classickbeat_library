package ai.heart.classickbeats.ui.history.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentHistoryGraphBinding
import ai.heart.classickbeats.graph.BarGraph
import ai.heart.classickbeats.graph.ScatterPlotGraph
import ai.heart.classickbeats.model.*
import ai.heart.classickbeats.model.entity.BaseLogEntity
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.util.toDateString
import ai.heart.classickbeats.shared.util.toMonthString
import ai.heart.classickbeats.shared.util.toWeekString
import ai.heart.classickbeats.ui.history.GraphHistoryAdapter
import ai.heart.classickbeats.ui.history.viewmodel.HistoryViewModel
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
import com.github.mikephil.charting.charts.ScatterChart
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HistoryGraphFragment : Fragment(R.layout.fragment_history_graph) {

    private val binding by viewBinding(FragmentHistoryGraphBinding::bind)

    private val historyViewModel: HistoryViewModel by activityViewModels()

    private val args: HistoryGraphFragmentArgs by navArgs()

    private lateinit var navController: NavController

    private lateinit var graphHistoryAdapter: GraphHistoryAdapter

    private lateinit var barChart: BarChart

    private lateinit var scatterChart: ScatterChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val (logType, timelineType, startDate) = args

        when (timelineType) {
            HistoryType.Daily -> {
                binding.barChart.visibility = View.GONE
                binding.scatterChart.visibility = View.VISIBLE
            }
            else -> {
                binding.barChart.visibility = View.VISIBLE
                binding.scatterChart.visibility = View.GONE
            }
        }

        historyViewModel.getGraphData(logType, timelineType, startDate)
        historyViewModel.getMeasurementData(logType, timelineType, startDate)

        navController = findNavController()

        barChart = binding.barChart.apply {
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
            setDrawBarShadow(false)
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false
            invalidate()
            requestLayout()
        }

        scatterChart = binding.scatterChart.apply {
            setDrawGridBackground(false)
            description.isEnabled = false
            axisLeft.setDrawLabels(true)
            axisLeft.setDrawAxisLine(true)
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawLabels(false)
            axisRight.setDrawAxisLine(false)
            axisRight.setDrawGridLines(false)
            xAxis.axisMinimum = 0.0f
            xAxis.axisMaximum = 1440.0f
            xAxis.setDrawLabels(true)
            xAxis.setDrawAxisLine(true)
            xAxis.setDrawGridLines(false)
            legend.isEnabled = false
            setNoDataText("")
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false
            invalidate()
            requestLayout()
        }

        graphHistoryAdapter = GraphHistoryAdapter(requireContext(), historyItemClickListener)

        historyViewModel.graphData.observe(viewLifecycleOwner, {
            it?.let { showUI(graphData = it) }
        })

        historyViewModel.measurementData.observe(viewLifecycleOwner, {
            it?.let { showUI(listData = it) }
        })

        binding.pageTitle.text = logType.getDisplayName()

        binding.dataRv.adapter = graphHistoryAdapter

        binding.backArrow.setSafeOnClickListener {
            navController.navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()

        historyViewModel.graphData.value?.let { showUI(graphData = it) }
    }

    private fun showUI(graphData: GraphData? = null, listData: List<TimelineItem>? = null) {
        binding.apply {
            graphData?.let {
                val (dateRangeTxt, timelineTypeStr) = when (it.timelineType) {
                    HistoryType.Daily -> Pair(it.startDate.toDateString(), "DAILY")
                    HistoryType.Weekly -> Pair(it.startDate.toWeekString(), "WEEKLY")
                    HistoryType.Monthly -> Pair(it.startDate.toMonthString(), "MONTHLY")
                }
                timeRange.text = dateRangeTxt
                graphLabel.text = "$timelineTypeStr (in ${it.model.getShortString()})"
                if (it.timelineType == HistoryType.Daily) {
                    ScatterPlotGraph.draw(requireContext(), scatterChart, it)
                } else {
                    BarGraph.draw(requireContext(), barChart, it)
                }
            }

            listData?.let {
                graphHistoryAdapter.submitList(it)
            }
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
            HistoryGraphFragmentDirections.actionHistoryGraphFragmentToScanResultFragment(
                showingHistory = true,
                scanId = id
            )
        navController.navigate(action)
    }
}