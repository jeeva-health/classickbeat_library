package ai.heart.classickbeats.graph

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet


object BarGraph {

    fun draw(context: Context, chart: BarChart, data1: List<Double>, data2: List<Double>? = null) {
        val values: ArrayList<BarEntry> = ArrayList()
        data1.forEachIndexed { i, d -> values.add(BarEntry(i.toFloat(), d.toFloat())) }

//        val startColor: Int = ContextCompat.getColor(context, R.color.holo_orange_light)
//        val endColor: Int = ContextCompat.getColor(context, R.color.holo_purple)
//        val gradientFills = ArrayList<Fill>()
//        gradientFills.add(Fill(startColor, endColor))

        val dataSet: BarDataSet = createDataSet(values)

        val dataSets: ArrayList<IBarDataSet> = ArrayList()
        dataSets.add(dataSet)

        val data = BarData(dataSets)
        data.barWidth = 0.9f

        chart.data = data
    }

    private fun createDataSet(values: List<BarEntry>): BarDataSet {
        val set = BarDataSet(values, "DataSet 1")
        set.color = Color.rgb(255, 0, 0)
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.valueTextSize = 10f
        set.setDrawValues(false)
        return set
    }
}