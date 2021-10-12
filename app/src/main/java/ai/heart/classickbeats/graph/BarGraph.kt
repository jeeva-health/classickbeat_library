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

        val values: ArrayList<BarEntry> = ArrayList()

//        val startColor: Int = ContextCompat.getColor(context, R.color.holo_orange_light)
//        val endColor: Int = ContextCompat.getColor(context, R.color.holo_purple)
//        val gradientFills = ArrayList<Fill>()
//        gradientFills.add(Fill(startColor, endColor))

        //Reversing the lists because most recent data is coming first
        val data1Rev = data1.reversed()
        val dateListRev = dateList.reversed()

        val endDateIndex = endDate.getDayPart()
        val startDateIndex = startDate.getDayPart()
        //val adjustedData1 = ArrayList<Double>(endDateIndex)
        val adjustedData1 = mutableListOf<Double>()
        var counter = 0
        for (i in startDateIndex until (endDateIndex+1)) {
            var dateIndex = 100
            if (counter < dateListRev.size){
                dateIndex = dateListRev[counter].getDayPart()
            }
            if (i == dateIndex) {
                adjustedData1.add(data1Rev[counter])
                counter += 1
            }
            else{
                adjustedData1.add(0.0)
            }
        }
        for (i in 0 until dateList.size){
            Timber.i("Date ${dateListRev[i].getDayPart()}")
        }
        Timber.i("data: ${Arrays.toString(data1Rev.toDoubleArray())}")
        Timber.i("Adjusted data: ${Arrays.toString(adjustedData1.toDoubleArray())}")
        adjustedData1.forEachIndexed { i, d -> values.add(BarEntry((i+1).toFloat(), d.toFloat())) }

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