package ai.heart.classickbeats.ui.monitor

import ai.heart.classickbeats.MainActivity
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentHeartResultBinding
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

    private lateinit var chart: LineChart

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
            navigateToSelectionFragment()
        }

        Glide.with(this).load(File("/storage/emulated/0/Pictures/ppg.jpg")).diskCacheStrategy(
            DiskCacheStrategy.NONE
        )
            .skipMemoryCache(true).into(binding.graph)

        chart = binding.lineChart.apply {
            setDrawGridBackground(false)
            description.isEnabled = false
            legend.isEnabled = false
            setNoDataText("")
            invalidate()
        }

//        drawLine(monitorViewModel.outputList!!)
//        drawLine(monitorViewModel.centeredSignal!!)
        LineGraph.drawLineGraph(chart, monitorViewModel.finalSignal!!)
    }

    private fun navigateToSelectionFragment() {
        val action =
            HeartResultFragmentDirections.actionHeartResultFragmentToCameraSelectionFragment()
        navController.navigate(action)
    }
}
