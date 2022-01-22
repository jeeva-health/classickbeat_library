package ai.heart.classickbeats.graph

import ai.heart.classickbeats.model.GraphData
import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


object BarGraph {

    fun draw(
        context: Context,
        chart: BarChart,
        graphData: GraphData
    ) {
        val (model, timelineType, isDecimal, data1, data2, dateList, startDate, endDate) = graphData
//        Timber.i("Data1: ${data1.size}, ${Arrays.toString(data1)}")

        val values: ArrayList<BarEntry> = ArrayList()
        val values2: ArrayList<BarEntry> = ArrayList()

        val duration = Utils.getDuration(timelineType, startDate)
        val adjustedData = MutableList(duration) { 0.0 }
        val adjustedData2 = MutableList(duration) { 0.0 }

        dateList.forEachIndexed { index, date ->
            val i = Utils.getIndexForDate(timelineType, date)
            adjustedData[i] = data1[index]
            data2.getOrNull(index)?.let { adjustedData2[i] = it }
        }

        adjustedData.forEachIndexed { i, d ->
            values.add(
                BarEntry(
                    (i + 1).toFloat(),
                    d.toFloat()
                )
            )
        }

        if (data2.isNotEmpty()){
            adjustedData2.forEachIndexed { i, d ->
                values2.add(
                    BarEntry(
                        (i + 1).toFloat(),
                        d.toFloat()
                    )
                )
            }
        }

        setDataSet(chart, values, values2)
    }

    private fun setDataSet(
        chart: BarChart,
        values: List<BarEntry>,
        values2: List<BarEntry>
    ) {
        chart.invalidate()
        chart.requestLayout()
        val set: BarDataSet
        var set2: BarDataSet? = null
        if (chart.data != null && chart.data.dataSetCount > 0) {
            set = chart.data.getDataSetByIndex(0) as BarDataSet
            set.values = values
            if (chart.data.dataSetCount == 2) {
                set2 = chart.data.getDataSetByIndex(1) as BarDataSet
                set2.values = values2
            } else if (values2.isNotEmpty()) {

            }
        } else {
            set = createDataSet(
                values = values,
                label = "DataSet 1",
                color = Color.rgb(255, 0, 0)
            )
            if (values2.isNotEmpty()) {
                set2 = createDataSet(
                    values = values2,
                    label = "DataSet 2",
                    color = Color.rgb(0, 255, 0)
                )
            }
            val barData = BarData(set)
            set2?.let { barData.addDataSet(it) }
            chart.data = barData
        }
        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()
    }

    private fun createDataSet(
        values: List<BarEntry>,
        label: String,
        color: Int
    ): BarDataSet {
        val set = BarDataSet(values, label)
        set.color = color
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.valueTextSize = 10f
        set.setDrawValues(false)
        set.setDrawIcons(false)
        return set
    }
}