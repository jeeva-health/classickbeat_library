package ai.heart.classickbeats.monitor

import ai.heart.classickbeats.MainActivity
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentHeartResultBinding
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.button.MaterialButton
import java.io.File
import kotlin.math.roundToInt


class HeartResultFragment : Fragment(R.layout.fragment_heart_result), OnChartValueSelectedListener {

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
            setOnChartValueSelectedListener(this@HeartResultFragment)
            setDrawGridBackground(false)
            description.isEnabled = false
            legend.isEnabled = false
            setNoDataText("")
            invalidate()
        }

//        drawLine(monitorViewModel.outputList!!)
        drawLine(monitorViewModel.filtOut!!)
//        drawLine(monitorViewModel.centeredSignal!!)
    }

    private fun navigateToSelectionFragment() {
        val action =
            HeartResultFragmentDirections.actionHeartResultFragmentToCameraSelectionFragment()
        navController.navigate(action)
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        //TODO("Not yet implemented")
    }

    override fun onNothingSelected() {
        //TODO("Not yet implemented")
    }

    private fun createSet(values: ArrayList<Entry>): LineDataSet {
        val set = LineDataSet(values, "DataSet 1")
        set.lineWidth = 1.0f
        set.color = Color.rgb(0, 0, 0)
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.valueTextSize = 10f
        set.setDrawValues(false)
        set.setDrawCircles(false)
        return set
    }

    private fun drawLine(data: List<Double>) {
        val values: ArrayList<Entry> = ArrayList()
        data.forEachIndexed { index, d ->
            values.add(Entry(index.toFloat(), d.toFloat()))
        }

        val lineDataSet = createSet(values)

        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(lineDataSet)

        val lineData = LineData(dataSets)
        chart.data = lineData
    }
}
