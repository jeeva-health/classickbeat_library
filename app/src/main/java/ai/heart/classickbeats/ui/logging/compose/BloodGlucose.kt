package ai.heart.classickbeats.ui.logging.compose

import ai.heart.classickbeats.R
import ai.heart.classickbeats.domain.BloodGlucose
import ai.heart.classickbeats.domain.toStringValue
import ai.heart.classickbeats.ui.common.compose.*
import ai.heart.classickbeats.ui.logging.model.GlucoseTagModel
import ai.heart.classickbeats.ui.logging.model.HistoryItemViewData
import ai.heart.classickbeats.ui.theme.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalMaterialApi
@Composable
fun BloodGlucoseScreen(
    onAddGlucose: () -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    val onToggleTagView = {
        coroutineScope.launch {
            if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                bottomSheetScaffoldState.bottomSheetState.expand()
            } else {
                bottomSheetScaffoldState.bottomSheetState.collapse()
            }
        }
        Unit
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            GlucoseTagBottomSheetDialog(
                selectedTag = null,
                onTagSelect = { tag: BloodGlucose.TAG -> },
                modifier = modifier
            )
        },
        sheetPeekHeight = 0.dp
    ) {
        GlucoseMainPageView(
            title = "Blood Glucose Level",
            onBackPressed = onBackPressed,
            onAddGlucose = onAddGlucose,
            onToggleTagView = onToggleTagView
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun GlucoseMainPageView(
    title: String,
    onBackPressed: () -> Unit,
    onAddGlucose: () -> Unit,
    onToggleTagView: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ToolBarWithBackAndAction(
            modifier = Modifier,
            title = title,
            onBackPressed = onBackPressed,
        ) {
            AddIcon(onAction = onAddGlucose, actionTitle = "Add")
        }

        GraphLayout(onToggleTagView = onToggleTagView)

        val d1 = HistoryItemViewData("12 March", "12:00 PM", "120/190")
        val d2 = HistoryItemViewData("12 March", "12:00 PM", "120/190")
        val d3 = HistoryItemViewData("12 March", "12:00 PM", "120/190")
        val dd: List<HistoryItemViewData> = arrayListOf(d1, d2, d3)

        HistoryLayout(modifier = Modifier, title = "History", unit = "mmHg", dtvList = dd)
    }
}

@ExperimentalMaterialApi
@Composable
fun GraphLayout(
    onToggleTagView: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                type = "Abnormal",
                value = "above 200 mg/dl\nbelow 70 mg/dl",
                color = RosyPink
            )
            ItemGraph(
                modifier = Modifier
                    .weight(1f),
                type = "Ideal Range",
                value = "70 to 200 mg/dl",
                color = DarkSkyBlue,
            )
        }

        Row(
            modifier = Modifier.padding(16.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Last Seven Days",
                color = CharcoalGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .padding(8.dp, 10.dp)
                    .border(
                        width = 2.dp,
                        color = DarkIndigo,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .align(Alignment.CenterVertically)
                    .padding(8.dp, 4.dp)
                    .clickable(onClick = onToggleTagView)
            ) {
                Text(
                    // modifier = Modifier.padding(16.dp),
                    text = "Fasting",
                    fontSize = 16.sp,
                    color = DarkIndigo,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier
                        .padding(2.dp, 0.dp)
                        .align(alignment = Alignment.CenterVertically)
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24),
                    contentDescription = null,
                    //modifier = Modifier.fillMaxHeight(),
                    colorFilter = ColorFilter.tint(DarkIndigo),
                    alignment = Alignment.Center
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
        ) {
            ChatIndicator(modifier = Modifier.weight(1f), color = DarkSkyBlue, value = "Normal")
            ChatIndicator(modifier = Modifier.weight(1f), color = RosyPink, value = "High")
            ChatIndicator(modifier = Modifier.weight(1f), color = DarkSkyBlue, value = "Low")
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

@Composable
fun LineChart(modifier: Modifier, data: List<LineChartModel>) {
    ClassicBeatsTheme() {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .height(500.dp),
            factory = { context ->
                CustomLineGraph(context, null, data)
            },
            update = {}
        )
    }
}

@Composable
fun ChatIndicator(modifier: Modifier, color: Color, value: String) {
    Row(
        modifier = modifier
            .padding(12.dp, 10.dp)
    ) {
        Box(
            modifier = Modifier

                .size(16.dp)
                .background(color = White, shape = CircleShape)
                .border(width = 3.dp, color = color, shape = CircleShape)
                .align(alignment = Alignment.Top)
        )

        Text(
            text = value,
            fontSize = 12.sp,
            color = WarmGray,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(4.dp, 0.dp)
        )

    }
}


@Composable
fun GlucoseTagBottomSheetDialog(
    selectedTag: BloodGlucose.TAG? = null,
    onTagSelect: (BloodGlucose.TAG) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = IceBlue)
            .padding(32.dp)
    ) {
        Column {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = "Filter by tag",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )

            val fasting = GlucoseTagModel(R.drawable.juice, BloodGlucose.TAG.FASTING)
            val postMeal = GlucoseTagModel(R.drawable.juice, BloodGlucose.TAG.POST_MEAL)
            val bedTime = GlucoseTagModel(R.drawable.juice, BloodGlucose.TAG.BED_TIME)
            val random = GlucoseTagModel(R.drawable.juice, BloodGlucose.TAG.RANDOM)
            val tagList: List<GlucoseTagModel> = arrayListOf(fasting, postMeal, bedTime, random)

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                items(tagList) { glucoseTag: GlucoseTagModel ->
                    val isSelected = glucoseTag.tag == selectedTag
                    GlucoseTagItemView(
                        icon = glucoseTag.icon,
                        tag = glucoseTag.tag.toStringValue(context),
                        selected = isSelected,
                        onClick = { onTagSelect.invoke(glucoseTag.tag) },
                    )
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun PreviewGraphLayout() {
    ClassicBeatsTheme {
        Surface {
            GraphLayout(onToggleTagView = {})
        }
    }
}

@Preview
@Composable
fun PreviewGlucoseTagBottomSheetDialog() {
    ClassicBeatsTheme {
        Surface {
            GlucoseTagBottomSheetDialog(selectedTag = null, onTagSelect = {})
        }
    }
}
