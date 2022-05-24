package ai.heart.classickbeats.ui.common.ui

import ai.heart.classickbeats.R
import ai.heart.classickbeats.shared.util.getDayOfWeek
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*

data class LineChartModel(val float: Float, val date: Date)

class CustomLineGraph @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, dataPoints: List<LineChartModel>
) : FrameLayout(context, attrs) {


    init {
        val view = inflate(context, R.layout.layout_line_graph, this)
        val chart: LineChart = view.findViewById(R.id.line_chart_custom)

        var entries:MutableList<Entry> = ArrayList<Entry>()

        for (i in dataPoints) {

            if (i.float >= 150) {
                //high blood glucose level

            } else if (i.float < 150 && i.float > 100) {
                //mid blood glucose level

            } else {
                //low blood glucose level

            }
            entries.add(Entry(i.date.day.toFloat(),i.float))
        }

        val dataset = LineDataSet(entries, "")
        dataset.setDrawCircles(false)
        dataset.color = R.color.ice_blue
        dataset.apply {
            lineWidth = 3f
        }

        val lineData = LineData(dataset)
        chart.data = lineData
        chart.invalidate() // refresh


    }
}