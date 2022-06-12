package ai.heart.classickbeats.ui.logging.compose

import ai.heart.classickbeats.R
import ai.heart.classickbeats.domain.BloodGlucose
import ai.heart.classickbeats.domain.toStringValue
import ai.heart.classickbeats.model.Date
import ai.heart.classickbeats.model.Time
import ai.heart.classickbeats.ui.common.ui.*
import ai.heart.classickbeats.ui.logging.BloodGlucoseViewModel
import ai.heart.classickbeats.ui.logging.model.BloodGlucoseViewData
import ai.heart.classickbeats.ui.logging.model.GlucoseTagModel
import ai.heart.classickbeats.ui.theme.*
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.paging.ExperimentalPagingApi
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@Composable
fun AddBloodGlucoseScreen(viewModel: BloodGlucoseViewModel) {
    val title = "Add Blood Glucose"
    val data = viewModel.defaultData
    val onSubmit = { glucoseData: BloodGlucoseViewData ->
        viewModel.uploadGlucoseLevelEntry(glucoseData)
    }
    val onBackPressed = {

    }
    AddBloodGlucoseView(
        title = title,
        data = data,
        onSubmit = onSubmit,
        onBackPressed = onBackPressed
    )
}

@Composable
fun AddBloodGlucoseView(
    title: String,
    data: BloodGlucoseViewData,
    onSubmit: (BloodGlucoseViewData) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = LightPink)
    ) {
        ToolBarWithBackAndAction(
            title = title,
            onBackPressed = { onBackPressed() },
            action = {},
            modifier = Modifier,
        )

        DateTimeItem(
            modifier = Modifier,
            icon = R.drawable.date,
            unit = "Date",
            value = data.dateString
        ) { showDatePicker(context, 0, 0, 0, { date: Date -> Unit }) }

        DateTimeItem(
            modifier = Modifier,
            icon = R.drawable.time,
            unit = "Time",
            value = data.timeString,
            onClick = { showTimePicker(context, 0, 0, { time: Time -> Unit }) }
        )

        ReadingLayout(
            context = context,
            defaultReading = data.reading,
            defaultTag = data.tag,
            modifier = Modifier
        )

        Spacer(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        Button(
            onClick = { onSubmit.invoke(data) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(shape = RectangleShape, color = RosyPink),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = RosyPink,
                contentColor = White
            )
        ) {
            Text(
                text = "SAVE",
                fontSize = 16.sp,
                color = White,
            )
        }
    }
}

@Composable
fun ReadingLayout(
    context: Context,
    defaultReading: Int,
    defaultTag: BloodGlucose.TAG,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp, 10.dp)
            .background(color = White, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GLucoseScaleLayout(
            defaultReading = defaultReading,
            maxValue = 300,
            modifier = Modifier
                .padding(0.dp, 16.dp)
                .fillMaxWidth(),
        )

        GlucoseTagView(context, defaultTag)

        Row(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Start)
                .clickable {/*TODO*/ }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_plus),
                contentDescription = null,
                modifier = Modifier.align(alignment = Alignment.CenterVertically),
                colorFilter = ColorFilter.tint(DarkIndigo),

                )

            Text(
                text = "Add Note",
                fontSize = 14.sp,
                color = DarkIndigo,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .padding(2.dp, 0.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun GlucoseTagView(
    context: Context,
    defaultTag: BloodGlucose.TAG,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Add a Tag",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = CharcoalGray,
            modifier = Modifier.Companion
                .align(Alignment.Start)
                .padding(16.dp)
        )

        val (selectedTag, setTag) = remember { mutableStateOf(defaultTag) }

        val d1 = GlucoseTagModel(R.drawable.juice, BloodGlucose.TAG.FASTING)
        val d2 = GlucoseTagModel(R.drawable.juice, BloodGlucose.TAG.POST_MEAL)
        val d3 = GlucoseTagModel(R.drawable.juice, BloodGlucose.TAG.BED_TIME)
        val d4 = GlucoseTagModel(R.drawable.juice, BloodGlucose.TAG.RANDOM)
        val ddList: List<GlucoseTagModel> = arrayListOf(d1, d2, d3, d4)

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(ddList) { dd: GlucoseTagModel ->
                GlucoseTagItemView(
                    modifier = Modifier,
                    icon = dd.icon,
                    tag = dd.tag.toStringValue(context),
                    selected = selectedTag == dd.tag,
                    onClick = { setTag.invoke(dd.tag) }
                )
            }
        }
    }
}

@Composable
fun GLucoseScaleLayout(defaultReading: Int,
                maxValue: Int,
                modifier: Modifier = Modifier) {

    val (glucoseLevelReading, updateReading) =
        remember { mutableStateOf(defaultReading) }

    Row(
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center

    ) {

        Text(
            text = glucoseLevelReading.toString(),
            modifier = Modifier
                .padding(0.dp, 8.dp, 8.dp, 0.dp),
            fontSize = 24.sp,
        )
        Text(
            text = "mg/dl",
            modifier = Modifier
                .align(alignment = Alignment.Bottom)
        )
    }

    AndroidView(modifier = modifier.fillMaxWidth()
        .background(color = RosyPink),
        factory = { context ->
            CustomSliderScale(
                context = context,
                currentReading = glucoseLevelReading,
                maxValue = maxValue,
                onReadingChange = { newValue ->
                    updateReading.invoke(newValue)
                })
        },
        update = {
        })
}
