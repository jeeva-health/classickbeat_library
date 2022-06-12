package ai.heart.classickbeats.ui.common.compose

import ai.heart.classickbeats.R
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.util.*

data class BarModel(
    val reading: Float,
    val date: Date
)

class CustomBarChat @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, dataPoints: List<BarModel>
) : FrameLayout(context, attrs) {
 

    init {
        val view = inflate(context, R.layout.layout_bar_chat, this)
        val chart: BarChart = view.findViewById(R.id.custom_bar_chart)

        val entries: MutableList<BarEntry> = ArrayList<BarEntry>()

        for ((j, data) in dataPoints.withIndex()) {
            entries.add(BarEntry(dataPoints[j].reading, j.toFloat() ))
        }

        val dataSet = BarDataSet(entries, "")

        dataSet.apply {
            color = R.color.rosy_pink
            barBorderWidth = 3f
            barBorderColor = R.color.ice_blue
        }

        val candleData = BarData(dataSet)
        chart.apply {
            data = candleData
            axisLeft.setDrawLabels(false)
            axisLeft.setDrawAxisLine(false)
            axisLeft.setDrawGridLines(false)
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