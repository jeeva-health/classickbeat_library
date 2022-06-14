package ai.heart.classickbeats.ui.logging.compose

import ai.heart.classickbeats.R
import ai.heart.classickbeats.model.Date
import ai.heart.classickbeats.ui.common.compose.*
import ai.heart.classickbeats.ui.logging.model.HistoryItemViewData
import ai.heart.classickbeats.ui.theme.CharcoalGray
import ai.heart.classickbeats.ui.theme.DarkSkyBlue
import ai.heart.classickbeats.ui.theme.RosyPink
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun BloodPressureScreen(
    onBackPress:()-> Unit,
    onAddPressure: ()-> Unit

) {
    Column(
        modifier = Modifier
            .background(color = colorResource(id = R.color.ice_blue))
            .fillMaxWidth()
            .fillMaxHeight()

    ) {
        ToolBarWithBackAndAction(
            modifier = Modifier,
            title = "Blood Pressure",
            onBackPressed = { onBackPress() },
        ) {
            AddIcon(onAction = { onAddPressure() }, actionTitle = "Add")
        }
        GraphLayout(modifier = Modifier)
        val d1 = HistoryItemViewData("12 March", "12:00 PM", "120/190")
        val d2 = HistoryItemViewData("12 March", "12:00 PM", "120/190")
        val d3 = HistoryItemViewData("12 March", "12:00 PM", "120/190")
        val dd: List<HistoryItemViewData> = arrayListOf(d1, d2, d3)

        HistoryLayout(
            modifier = Modifier,
            title = "History",
            unit = "",
            dtvList = dd
        )
    }
}


@Composable
fun GraphLayout(modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp, 4.dp)
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            ItemGraph(
                modifier = Modifier
                    .weight(1f),
                type = "Systolic",
                value = "90 to 250 (mmHg)",
                color = RosyPink
            )
            ItemGraph(
                modifier = Modifier
                    .weight(1f),
                type = "Diastolic",
                value = "60 to 140 (mmHg)",
                color = DarkSkyBlue,
            )

        }
        Text(
            text = "Last Seven Days",
            modifier = Modifier.padding(16.dp, 0.dp),
            color = CharcoalGray,
            fontWeight = FontWeight.Bold
        )
        Surface(
            modifier = Modifier
                .padding(0.dp, 16.dp)
                .fillMaxWidth()
                .height(250.dp)
        ) {

            val chatData: MutableList<CandleStickChartModel> = ArrayList()
            chatData.add(CandleStickChartModel(20f, 10f, 12f, 15f, Date(4, 2, 2022)))
            chatData.add(CandleStickChartModel(20f, 10f, 14f, 15f, Date(4, 2, 2022)))
            chatData.add(CandleStickChartModel(20f, 10f, 15f, 15f, Date(4, 2, 2022)))
            chatData.add(CandleStickChartModel(20f, 10f, 18f, 15f, Date(4, 2, 2022)))
            chatData.add(CandleStickChartModel(18f, 11f, 18f, 15f, Date(4, 2, 2022)))
            chatData.add(CandleStickChartModel(25f, 10f, 18f, 15f, Date(4, 2, 2022)))
            chatData.add(CandleStickChartModel(12f, 10f, 10f, 15f, Date(4, 2, 2022)))

            AndroidView(modifier = modifier
                .fillMaxWidth()
                .height(500.dp),
                factory = { context ->
                    CustomCandleStickChat(context, null, chatData)
                },
                update = {/**/ })
        }
    }
}

