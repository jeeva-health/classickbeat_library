package ai.heart.classickbeats.graph

import ai.heart.classickbeats.model.GraphData
import ai.heart.classickbeats.model.TimelineType
import ai.heart.classickbeats.shared.util.getDayOfWeek
import ai.heart.classickbeats.shared.util.getDayPart
import ai.heart.classickbeats.shared.util.getNumberOfDaysInMonth
import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.util.*
import kotlin.collections.ArrayList


object BarGraph {

    fun draw(
        context: Context,
        chart: BarChart,
        graphData: GraphData
    ) {

        val (model, timelineType, isDecimal, data1, data2, dateList, startDate, endDate) = graphData

        val values: ArrayList<BarEntry> = ArrayList()

//        val startColor: Int = ContextCompat.getColor(context, R.color.holo_orange_light)
//        val endColor: Int = ContextCompat.getColor(context, R.color.holo_purple)
//        val gradientFills = ArrayList<Fill>()
//        gradientFills.add(Fill(startColor, endColor))

        val duration = getDuration(timelineType, startDate)
        val adjustedData = MutableList(duration) { 0.0 }

        dateList.forEachIndexed { index, date ->
            val i = getIndexForDate(timelineType, date)
            val data = data1[index]
            adjustedData[i] = data
        }

        adjustedData.forEachIndexed { i, d ->
            values.add(
                BarEntry(
                    (i + 1).toFloat(),
                    d.toFloat()
                )
            )
        }

        val dataSet: BarDataSet = createDataSet(chart, values)

        val dataSets: ArrayList<IBarDataSet> = ArrayList()
        dataSets.add(dataSet)

        val data = BarData(dataSets)
        data.barWidth = 0.9f

        chart.data = data
    }

    private fun createDataSet(chart: BarChart, values: List<BarEntry>): BarDataSet {
        val set: BarDataSet
        if (chart.data != null && chart.data.dataSetCount > 0) {
            set = chart.data.getDataSetByIndex(0) as BarDataSet
            set.values = values
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            set = BarDataSet(values, "DataSet 1")
            set.color = Color.rgb(255, 0, 0)
            set.axisDependency = YAxis.AxisDependency.LEFT
            set.valueTextSize = 10f
            set.setDrawValues(false)
        }
        return set
    }

    private fun getDuration(type: TimelineType, startDate: Date): Int =
        when (type) {
            TimelineType.Daily -> 1
            TimelineType.Weekly -> 7
            TimelineType.Monthly -> startDate.getNumberOfDaysInMonth()
        }

    private fun getIndexForDate(type: TimelineType, date: Date): Int =
        when (type) {
            TimelineType.Daily -> TODO()
            TimelineType.Weekly -> {
                (date.getDayOfWeek() + 5) % 7
            }
            TimelineType.Monthly -> {
                date.getDayPart() - 1
            }
        }
}