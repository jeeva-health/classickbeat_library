package ai.heart.classickbeats.ui.logging.fragment.heartRate

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.common.compose.DateTimeSelectionView
import ai.heart.classickbeats.ui.common.compose.LineChart
import ai.heart.classickbeats.ui.common.compose.LineChartModel
import ai.heart.classickbeats.ui.common.compose.ToolBarWithBackAndAction
import ai.heart.classickbeats.ui.logging.model.HistoryItemViewData
import ai.heart.classickbeats.ui.theme.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*


@Composable
fun HeartRateScreen(
    onToggleTagView: () -> Unit,
    onNavigateMeasureBPMKnowMore: () -> Unit,
    onMeasureHeartRhythm: () -> Unit,
    onBackPress: () -> Unit
) {
    val linear = Brush.linearGradient(listOf(IceBlue, IceBlue, LightPink))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = linear)
    ) {
        ToolBarWithBackAndAction(
            modifier = Modifier,
            title = "Heart Rate",
            onBackPressed = { onBackPress() },
        ) {
        }
        HeartRateMonitor(
            onNavigateMeasureBPMKnowMore = { onNavigateMeasureBPMKnowMore() },
            onMeasureHeartRhythm = { onMeasureHeartRhythm() }
        )
        ZoneScreen(onToggleTagView)

        val d1 = HistoryItemViewData("12 March", "12:00 PM", "120/190")
        val d2 = HistoryItemViewData("12 March", "12:00 PM", "120/190")
        val d3 = HistoryItemViewData("12 March", "12:00 PM", "120/190")
        val dd: List<HistoryItemViewData> = arrayListOf(d1, d2, d3)

//            HistoryLayout(modifier = Modifier, title = "History", unit = "mmHg", dtvList = dd)
//            HistoryLayoutTypeTwo(modifier = Modifier)
        Text(
            text = "Performance History",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn {
            itemsIndexed(dd) { index: Int, data: HistoryItemViewData ->
                DateTimeSelectionView(
                    icon = R.drawable.heart_rate,
                    actionIcon = R.drawable.ic_arrow_forward,
                    label = data.value,
                    value = data.date,
                    onClick = { /*TODO*/ })
            }
        }


    }
}


@Composable
fun HeartRateMonitor(
    onNavigateMeasureBPMKnowMore: () -> Unit,
    onMeasureHeartRhythm: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth()
            .background(color = White)
            .padding(24.dp)
    ) {
        Text(text = "Heart Rate ", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            buildAnnotatedString {
                ClickableText(text = buildAnnotatedString {

                    withStyle(style = SpanStyle(color = WarmGray)) {
                        append("You can quickly measure and monitor your heart rate with your phone camera.")
                    }

                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append(" Know more ->")
                    }
                },
                    onClick = { onNavigateMeasureBPMKnowMore.invoke() }
                )
            }
        )
        Button(
            onClick = { onMeasureHeartRhythm.invoke() },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = RosyPink,
                contentColor = White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(shape = RoundedCornerShape(6.dp), color = RosyPink),

            ) {
            Text(
                text = "MEASURE HEART RHYTHM",
                fontSize = 16.sp,
                color = White,
            )
        }
    }
}

@Composable
fun ZoneScreen(onToggleTagView: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth()
            .background(color = White, shape = RoundedCornerShape(8.dp))
            .wrapContentHeight()

    ) {
        Column(
            modifier = Modifier
                .background(color = Beige, shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp))
                .padding(16.dp)
        ) {
            Row(Modifier.padding(0.dp, 16.dp)) {
                Text(
                    text = "Heart Rate Zone",
                    color = CharcoalGray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Row {
                    Text(
                        text = "Know more",
                        fontSize = 16.sp,
                        color = RosyPink,
                        modifier = Modifier.clickable { onToggleTagView.invoke() }
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_forward),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(RosyPink)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(shape = RoundedCornerShape(3.dp), color = Color.Transparent)
            ) {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            color = Maize,
                            shape = RoundedCornerShape(3.dp, 0.dp, 0.dp, 3.dp)
                        )
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(color = Orangeish)
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(color = WarmPink)
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            color = Strawberry,
                            shape = RoundedCornerShape(0.dp, 3.dp, 3.dp, 0.dp)
                        )
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = "60",
                    color = CharcoalGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "102",
                    color = CharcoalGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "142",
                    color = CharcoalGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "183",
                    color = CharcoalGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "220",
                    color = CharcoalGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Average",
                    color = Maize,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Healthy",
                    color = Orangeish,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Maximum",
                    color = WarmPink,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Danger",
                    color = Strawberry,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(modifier = Modifier) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Daily",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalGray,
                    modifier = Modifier
                        .background(color = WhiteTwo, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp, 9.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Weekly",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalGray,
                    modifier = Modifier
                        .background(color = WhiteTwo, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp, 9.dp)

                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Monthly",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalGray,
                    modifier = Modifier
                        .background(color = WhiteTwo, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp, 9.dp)
                        .wrapContentWidth()
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Surface(
                modifier = Modifier
                    .padding(0.dp, 4.dp)
                    .fillMaxWidth()
                    .height(150.dp),
            ) {
                val data: MutableList<LineChartModel> = ArrayList()

                data.add(LineChartModel(150f, Date(2022, 2, 1)))
                data.add(LineChartModel(126f, Date(2022, 2, 3)))
                data.add(LineChartModel(100f, Date(2022, 2, 6)))
                data.add(LineChartModel(160f, Date(2022, 2, 4)))
                data.add(LineChartModel(100f, Date(2022, 2, 6)))
                LineChart(modifier = Modifier, data = data)
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun HeartRateTagBottomSheetDialog(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color = White, shape = RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
            .fillMaxWidth()
            .padding(8.dp, 16.dp, 8.dp, 8.dp)
    ) {
        Column(modifier = Modifier) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        color = IceBlue_Two,
                        shape = RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp)
                    )
            ) {
                Text(
                    text = "Find Your Target Heart Rate For Exercise",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Maximum heart rate = 220-Age",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(color = DuckEggBlue, shape = RoundedCornerShape(4.dp)),
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier
                    .padding(0.dp, 4.dp)
                    .background(
                        color = CharcoalGray_Two,
                        shape = RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp)
                    )
                    .fillMaxWidth()
                    .padding(3.dp)
            ) {
                Text(text = "Training Zone", color = White)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Age and Beats per minute", color = White)

            }
            InfoItem(
                R.drawable.max_zone_icon,
                FadePink,
                RosyPink,
                DustRed,
                "Maximum",
                "90-100%",
                listOf("180", "171", "162", "153", "144"),
                "effort Helps athlete develop speed."
            )
            InfoItem(
                R.drawable.hard_zone_icon,
                Pale,
                Mango,
                BrowniesOrange,
                "Maximum",
                "90-100%",
                listOf("180", "171", "162", "153", "144"),
                "effort Helps athlete develop speed."
            )

            InfoItem(
                R.drawable.light_zone_icon,
                Light_Blue_Gray_Two,
                FlatBlue,
                DarkSlateBLue,
                "Maximum",
                "90-100%",
                listOf("180", "171", "162", "153", "144"),
                "effort Helps athlete develop speed."
            )

            InfoItem(
                R.drawable.moderate_zone_icon,
                LightSage,
                MidGreen,
                MossyGreen,
                "Maximum",
                "90-100%",
                listOf("180", "171", "162", "153", "144"),
                "effort Helps athlete develop speed."
            )

            InfoItem(
                R.drawable.very_light_zone_icon,
                LightGrayGreen,
                DarkGrayBlue,
                DarkGreenBlue,
                "Maximum",
                "90-100%",
                listOf("180", "171", "162", "153", "144"),
                "effort Helps athlete develop speed."
            )
        }

    }
}

@Composable
fun InfoItem(
    icon: Int,
    color: Color,
    color_two: Color,
    color_three: Color,
    level: String,
    range: String,
    list: List<String>,
    info: String
) {
    Row(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .height(62.dp)
            .background(color = color, shape = RoundedCornerShape(4.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.padding(4.dp, 8.dp)) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .width(27.dp)
                    .height(27.dp)
            )
            Text(text = level, fontSize = 10.sp, color = color_two)
        }
        Surface(
            Modifier
                .width(1.dp)
                .height(52.dp)
                .background(color = CharcoalGray_Two)
        ) {}

        Text(
            text =
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = color_two
                    )
                ) {
                    append(text = range + "\n")
                }
                withStyle(style = SpanStyle(fontSize = 10.sp, color = color_two)) {
                    append(info)
                }
            },

            Modifier
                .padding(4.dp, 8.dp)
                .weight(1f)
                .width(120.dp)
                .fillMaxHeight()
        )

        Surface(
            Modifier
                .width(1.dp)
                .height(52.dp)
                .background(color = CharcoalGray_Two)
        ) {}

        LazyRow(
            Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(4.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            itemsIndexed(list) { index, item ->
                ShortItem(
                    color_two = color_two,
                    color_three = color_three,
                    year = ((index + 2) * 10).toString(),
                    value = item
                )

            }
        }
    }
}

@Composable
fun ShortItem(color_two: Color, color_three: Color, year: String, value: String) {

    Column(
        modifier = Modifier
            .padding(2.dp, 0.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = year + "Y", fontSize = 10.sp, color = color_two)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = color_three
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

data class knowMore(val image: Int, val text: String)
@Composable
fun MeasureBPMKnowMoreScreen(onBackPress:()-> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkIndigo)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 60.dp, 16.dp, 16.dp)
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "How to Scan Your Pulse?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_cross),
                contentDescription = null,
                Modifier
                    .height(12.dp)
                    .width(12.dp)
                    .clickable { onBackPress() },
                colorFilter = ColorFilter.tint(White)
            )
        }


        val m1 = knowMore(
            R.drawable.ic_banner_invite_friends,
            "Place the tip of your finger on the camera and the flash, completely covering them."
        )
        val m2 = knowMore(
            R.drawable.ic_banner_invite_friends,
            "Apply gentle pressure in order to stabilize the finger."
        )
        val m3 = knowMore(
            R.drawable.ic_banner_invite_friends,
            "If the smartphone is large to hold it in one hand comfortably, use one hand to hold the phone and the other to take the reading"
        )
        val m4 =knowMore(
            R.drawable.ic_banner_invite_friends,
            "Preferably, sit down on a chair and place your arm on a table at roughly the same level as the chest."
        )
        val m5 = knowMore(
            R.drawable.ic_banner_invite_friends,
            "Do not move or talk. Make sure to follow all instructions for accurate results.",
        )
        val mm = listOf(m1, m2, m3, m4, m5)
        LazyColumn(Modifier.padding(32.dp, 0.dp)) {
            itemsIndexed(mm) { index: Int, item: knowMore ->
                MeasureBPMItem(index, item)
            }
        }
    }
}

@Composable
fun MeasureBPMItem(index: Int, item: knowMore) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Card(
            Modifier
                .padding(10.dp, 0.dp, 0.dp, 0.dp)
                .fillMaxWidth()
                .background(color = White, shape = RoundedCornerShape(16.dp))
                .border(border = BorderStroke(2.dp, RosyPink), shape = RoundedCornerShape(4.dp))
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = item.text, Modifier.padding(16.dp, 32.dp))
                Image(
                    painter = painterResource(id = item.image),
                    contentDescription = null,
                    modifier = Modifier.wrapContentHeight(),
                    alignment = Alignment.Center,
                )
            }
        }

        Text(
            text = (index + 1).toString(),
            modifier = Modifier
                .border(
                    border = BorderStroke(2.dp, RosyPink),
                    shape = CircleShape
                )
                .background(color = White, shape = CircleShape)
                .height(30.dp)
                .width(30.dp)
                .padding(0.dp, 5.dp, 0.dp, 0.dp),
            textAlign = TextAlign.Center,
        )
    }
}
