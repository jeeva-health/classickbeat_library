package ai.heart.classickbeatslib.graph

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

object RunningGraph {

    private const val movingAvgWindow = 5

    private val movingList = mutableListOf<Double>()

    private fun createSet(): LineDataSet {
        val set = LineDataSet(null, "DataSet 1")
        set.lineWidth = 2.5f
        set.color = Color.rgb(255, 255, 255)
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.valueTextSize = 10f
        set.setDrawValues(false)
        set.setDrawCircles(false)
        return set
    }

    fun addEntry(chart: LineChart, x: Int, y: Double) {
        var data = chart.data
        if (data == null) {
            data = LineData()
            chart.data = data
        }

        var set = data.getDataSetByIndex(0)
        if (set == null) {
            set = createSet()
            data.addDataSet(set)
        }

//        val yValue = if (movingList.size < movingAvgWindow) {
//            movingList.add(y)
//            y
//        } else {
//            movingList.removeAt(0)
//            movingList.add(y)
//            movingList.average()
//        }

        data.addEntry(Entry(x.toFloat(), y.toFloat()), 0)
        data.notifyDataChanged()

        chart.notifyDataSetChanged()
        chart.setVisibleXRangeMaximum(150f)
        //chart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);

        chart.moveViewTo((data.entryCount - 7).toFloat(), 0f, YAxis.AxisDependency.LEFT)
    }
}