package ai.heart.classickbeats.graph

import ai.heart.classickbeats.model.GraphData
import ai.heart.classickbeats.shared.util.toDailyMinutes
import android.content.Context
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.utils.ColorTemplate


object ScatterPlotGraph {

    fun draw(
        context: Context,
        chart: ScatterChart,
        graphData: GraphData
    ) {
        val (_, _, _, data1, data2, dateList, _, _) = graphData

        val values: ArrayList<Entry> = ArrayList()

        val duration = 24 * 60
        val adjustedData = MutableList(duration) { 0.0 }
        val adjustedData2 = MutableList(duration) { 0.0 }

        dateList.forEachIndexed { index, date ->
            val dailyMinutes = date.toDailyMinutes()
            adjustedData[dailyMinutes] = data1[index]
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

        val set1 = ScatterDataSet(values, "DS 1")
        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE)
        set1.color = ColorTemplate.COLORFUL_COLORS[0]
        set1.scatterShapeSize = 8f;

        val dataSets: ArrayList<IScatterDataSet> = ArrayList()
        dataSets.add(set1)

        val data = ScatterData(dataSets)

        chart.data = data
        chart.invalidate()
    }
}