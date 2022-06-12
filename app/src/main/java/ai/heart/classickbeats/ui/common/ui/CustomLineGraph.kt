package ai.heart.classickbeats.ui.common.ui

import ai.heart.classickbeats.R
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*

data class LineChartModel(val float: Float, val date: Date)

@SuppressLint("ViewConstructor")
class CustomLineGraph @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, dataPoints: List<LineChartModel>
) : FrameLayout(context, attrs) {


    init {
        val view = inflate(context, R.layout.layout_line_graph, this)
        val chart: LineChart = view.findViewById(R.id.line_chart_custom)


        val entries: MutableList<Entry> = ArrayList<Entry>()
        for ((j, i) in dataPoints.withIndex()) {

            if (i.float >= 150) {
                //high blood glucose level
                entries.add(Entry(j.toFloat(), i.float))
            } else if (i.float < 150 && i.float > 100) {
                //mid blood glucose level
                entries.add(Entry(j.toFloat(), i.float))
            } else {
                //low blood glucose level
                entries.add(Entry(j.toFloat(), i.float))
            }
        }

        val dataset = LineDataSet(entries, "")
        dataset.setDrawCircles(true)
        dataset.circleRadius = 10f
        dataset.circleHoleRadius = 5f
        dataset.circleHoleColor = R.color.white
        dataset.valueTextSize = 3f

        for (i in dataPoints) {
            if (i.float >= 150) {
                //high blood glucose level
                dataset.setCircleColor(R.color.ice_blue)
                dataset.valueTextColor = R.color.ice_blue
            } else if (i.float < 150 && i.float > 110) {
                //mid blood glucose level

            } else {
                //low blood glucose level
                dataset.setCircleColor(R.color.rosy_pink)
                dataset.valueTextColor = R.color.rosy_pink
            }
        }
        dataset.mode = LineDataSet.Mode.HORIZONTAL_BEZIER //set it to curve
        dataset.color = R.color.ice_blue
        dataset.setDrawFilled(true)
        dataset.apply {
            lineWidth = 3f

        }

        val lineData = LineData(dataset)
        chart.apply {
            data = lineData
//            axisLeft.setDrawLabels(false)
//            axisLeft.setDrawAxisLine(false)
//            axisLeft.setDrawGridLines(false)
            axisRight.setDrawLabels(false)
            axisRight.setDrawAxisLine(false)
            axisRight.setDrawGridLines(false)
            xAxis.setDrawLabels(false)
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)

            isDragEnabled = false
            legend.isEnabled = false
        }
        chart.invalidate() // refresh


    }
}