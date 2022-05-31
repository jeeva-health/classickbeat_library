package ai.heart.classickbeats.ui.common.ui

import ai.heart.classickbeats.R
import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.compose.ui.graphics.PaintingStyle.Companion.Fill
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import java.util.*

data class CandleStickChartModel(
    val shadowH: Float,
    val shadowL: Float,
    val open: Float,
    val close: Float,
    val date: Date
)

class CustomCandleStickChat @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, dataPoints: List<CandleStickChartModel>
) : FrameLayout(context, attrs) {


    init {
        val view = inflate(context, R.layout.layout_candelstick_chat, this)
        val chart: CandleStickChart = view.findViewById(R.id.line_candlestick_custom)

        val entries: MutableList<CandleEntry> = ArrayList<CandleEntry>()
        for ((j, i) in dataPoints.withIndex()) {
            entries.add(CandleEntry(j.toFloat(), i.shadowH, i.shadowL, i.open, i.close))
        }

        val candleDataSet = CandleDataSet(entries, "Blood Pressure")

        candleDataSet.apply {
            color = R.color.rosy_pink
            shadowColor = R.color.ice_blue
            decreasingColor=R.color.moderate_green
            decreasingPaintStyle = Paint.Style.FILL
            increasingColor = R.color.pale_red
            increasingPaintStyle = Paint.Style.FILL
        }

        val candleData = CandleData(candleDataSet)
        chart.apply {
            data = candleData
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