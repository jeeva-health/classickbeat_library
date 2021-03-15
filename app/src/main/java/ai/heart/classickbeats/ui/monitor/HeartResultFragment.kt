package ai.heart.classickbeats.ui.monitor

import ai.heart.classickbeats.MainActivity
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentHeartResultBinding
import ai.heart.classickbeats.domain.TestType
import ai.heart.classickbeats.graph.LineGraph
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.mikephil.charting.charts.LineChart
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.math.roundToInt


@AndroidEntryPoint
class HeartResultFragment : Fragment(R.layout.fragment_heart_result) {

    private val binding by viewBinding(FragmentHeartResultBinding::bind)

    private lateinit var navController: NavController

    private lateinit var testAgainButton: MaterialButton

    private lateinit var chart1: LineChart

    private lateinit var chart2: LineChart

    private lateinit var chart3: LineChart

    private lateinit var chart4: LineChart

    private lateinit var chart5: LineChart

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).showSystemUI()

        navController = findNavController()

        testAgainButton = binding.reTestButton

        monitorViewModel.hearRateResult?.let {
            binding.quality.text = it.quality
            binding.aFib.text = it.aFib
            binding.bpm.text = it.bpm.roundToInt().toString()
            binding.hrv.text = "%.2f ms".format(it.hrv)
        }

        testAgainButton.setSafeOnClickListener {
            navigateToScanFragment()
        }

        Glide.with(this).load(File("/storage/emulated/0/Pictures/ppg.jpg")).diskCacheStrategy(
            DiskCacheStrategy.NONE
        )
            .skipMemoryCache(true).into(binding.graph)

        chart1 = binding.lineChart1.apply {
            setDrawGridBackground(false)
            description.isEnabled = false
            legend.isEnabled = false
            setNoDataText("")
            invalidate()
        }

        chart2 = binding.lineChart2.apply {
            setDrawGridBackground(false)
            description.isEnabled = false
            legend.isEnabled = false
            setNoDataText("")
            invalidate()
        }

        chart3 = binding.lineChart3.apply {
            setDrawGridBackground(false)
            description.isEnabled = false
            legend.isEnabled = false
            setNoDataText("")
            invalidate()
        }

        chart4 = binding.lineChart4.apply {
            setDrawGridBackground(false)
            description.isEnabled = false
            legend.isEnabled = false
            setNoDataText("")
            invalidate()
        }

        chart5 = binding.lineChart5.apply {
            setDrawGridBackground(false)
            description.isEnabled = false
            legend.isEnabled = false
            setNoDataText("")
            invalidate()
        }

        // LineGraph.drawLineGraph(chart1, monitorViewModel.interpolatedList!!)

        LineGraph.drawLineGraph(chart1, monitorViewModel.outputList!!)

        LineGraph.drawLineGraph(chart2, monitorViewModel.centeredSignal!!)

        LineGraph.drawLineGraph(chart3, monitorViewModel.envelopeAverage!!)

        LineGraph.drawLineGraph(chart4, monitorViewModel.leveledSignal!!)

        LineGraph.drawLineGraph(chart5, monitorViewModel.filtOut!!)
    }

    private fun navigateToScanFragment() {
        val action =
            HeartResultFragmentDirections.actionHeartResultFragmentToScanFragment(testType = TestType.HEART_RATE)
        navController.navigate(action)
    }
}
