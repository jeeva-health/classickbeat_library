package ai.heart.classickbeats.graph

import ai.heart.classickbeats.model.GraphData
import ai.heart.classickbeats.shared.util.getDayPart
import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet


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

        val endDateIndex = endDate.getDayPart()
        val adjustedData1 = ArrayList<Double>(endDateIndex)
        for (i in 0..endDateIndex) {
            val dateIndex = dateList[0].getDayPart() - 1
            if (i == dateIndex) {
                adjustedData1[dateIndex] = data1[0]
                data1
            }
        }

        data1.forEachIndexed { i, d -> values.add(BarEntry(i.toFloat(), d.toFloat())) }

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
}