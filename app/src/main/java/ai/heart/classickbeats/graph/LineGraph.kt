package ai.heart.classickbeats.graph

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

object LineGraph {

    private fun createLineDataSet(values: ArrayList<Entry>): LineDataSet {
        val set = LineDataSet(values, "DataSet 1")
        set.lineWidth = 1.0f
        set.color = Color.rgb(255, 0, 0)
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.valueTextSize = 10f
        set.setDrawValues(false)
        set.setDrawCircles(false)
        return set
    }

    fun drawLineGraph(chart: LineChart, data: List<Double>) {
        val values: ArrayList<Entry> = ArrayList()
        data.forEachIndexed { index, d ->
            values.add(Entry(index.toFloat(), d.toFloat()))
        }

        val lineDataSet = createLineDataSet(values)

        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(lineDataSet)

        val lineData = LineData(dataSets)
        chart.data = lineData
        chart.data.isHighlightEnabled = false
    }
}