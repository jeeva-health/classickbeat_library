package ai.heart.classickbeats.graph

import ai.heart.classickbeats.model.GraphData
import ai.heart.classickbeats.shared.util.toDailyMinutes
import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet


object ScatterPlotGraph {

    fun draw(
        context: Context,
        chart: ScatterChart,
        graphData: GraphData
    ) {
        val (_, _, _, data1, data2, dateList, _, _) = graphData

        val values: ArrayList<Entry> = ArrayList()
        val values2: ArrayList<Entry> = ArrayList()

        val duration = 24 * 60
        val adjustedData = MutableList(duration) { 0.0 }
        val adjustedData2 = MutableList(duration) { 0.0 }

        dateList.forEachIndexed { index, date ->
            val dailyMinutes = date.toDailyMinutes()
            adjustedData[dailyMinutes] = data1[index]
            data2.getOrNull(index)?.let { adjustedData2[dailyMinutes] = it }
        }

        adjustedData.forEachIndexed { i, d ->
            if (d != 0.0) {
                values.add(
                    Entry(
                        (i + 1).toFloat(),
                        d.toFloat()
                    )
                )
            }
        }

        if (data2.isNotEmpty()){
            adjustedData2.forEachIndexed { i, d ->
                if (d != 0.0) {
                    values2.add(
                        Entry(
                            (i + 1).toFloat(),
                            d.toFloat()
                        )
                    )
                }
            }
        }

        chart.invalidate()
        chart.requestLayout()

        val set1 = ScatterDataSet(values, "DS 1")
        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE)
        set1.color = Color.rgb(255, 0, 0)
        set1.scatterShapeSize = 8f;
        val dataSets: ArrayList<IScatterDataSet> = ArrayList()
        dataSets.add(set1)

        if (values2.isNotEmpty()) {
            val set2 = ScatterDataSet(values2, "DS 2")
            set2.setScatterShape(ScatterChart.ScatterShape.TRIANGLE)
            set2.color = Color.rgb(0, 255, 0)
            set2.scatterShapeSize = 8f;
            dataSets.add(set2)
        }

        val data = ScatterData(dataSets)

        chart.data = data
        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()
    }
}