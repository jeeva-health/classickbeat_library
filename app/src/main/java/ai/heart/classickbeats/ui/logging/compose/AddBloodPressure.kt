package ai.heart.classickbeats.ui.logging.compose

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.common.ui.CustomSliderScale
import ai.heart.classickbeats.ui.common.ui.DateTimeItem
import ai.heart.classickbeats.ui.common.ui.ToolBarWithBackAndAction
import ai.heart.classickbeats.ui.logging.model.BloodGlucoseViewData
import ai.heart.classickbeats.ui.logging.model.BloodPressureViewData
import ai.heart.classickbeats.ui.theme.*
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.flow.MutableStateFlow

@SuppressLint("ResourceType")
@Composable
fun AddPressureMainCompose(
    title: String,
    data: BloodPressureViewData,
    onSubmit: (BloodPressureViewData) -> Unit,
    onBackPressed: () -> Unit
) {
    val brush = Brush.verticalGradient(
        listOf(Color(0xFFE1F2F6), Color(0xFFD3062A)),
    )


    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(color = LightPink)
    ) {
        ToolBarWithBackAndAction(
            modifier = Modifier,
            title = "Blood Glucose Level",
            backAction = { onBackPressed() },
        )
        DateTimeItem(
            modifier = Modifier,
            icon = R.drawable.date,
            unit = "Date",
            value = "Today",
            onClick = {}
        )
        DateTimeItem(
            modifier = Modifier,
            icon = R.drawable.time,
            unit = "Time",
            value = "2:30 PM"
        ){}
        ReadingLayout(modifier = Modifier)

        Spacer(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(color = Color.Transparent)

        )

        Button(
            onClick = { onBackPressed() },
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
fun ReadingLayout(modifier: Modifier) {
    var sysReading = remember {
        MutableStateFlow(0)
    }
    var diaReading = remember {
        MutableStateFlow(0)
    }
    Column(
        modifier = modifier
            .padding(16.dp, 10.dp)
            .background(color = White, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 32.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .align(alignment = Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center

        ) {
            Text(
                modifier = Modifier
                    .padding(0.dp, 32.dp, 8.dp, 0.dp),
                text = "" + sysReading.collectAsState().value + "/" + diaReading.collectAsState().value,
                fontSize = 24.sp,

                )
            Text(
                text = "(mmHg)",
                modifier = Modifier
                    .align(alignment = Alignment.Bottom)
            )

        }
        ScaleLayout(
            modifier = Modifier,
            diagnostic = "Systolic (High)",
            color = RosyPink,
            maxValue = 360,
            reading = sysReading
        )
        ScaleLayout(
            modifier = Modifier,
            diagnostic = "Diastolic (Low)",
            color = DarkSkyBlue,
            maxValue = 360,
            reading = diaReading
        )
    }
}

@Composable
fun ScaleLayout(
    modifier: Modifier,
    diagnostic: String,
    color: Color,
    maxValue: Int,
    reading: MutableStateFlow<Int>
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp, 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(color = color, shape = CircleShape)
                    .border(width = 3.dp, color = White, shape = CircleShape)
                    .align(alignment = Alignment.CenterVertically)
            )
            Text(
                text = diagnostic,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalGray,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(4.dp, 0.dp)
            )
        }

        Surface(
            modifier = Modifier
                .padding(0.dp, 16.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            color = color
        ) {
            AndroidView(modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    CustomSliderScale(context,
                        reading.value, maxValue,
                        {})
                },
                update = {
                    //run only for first time
                })
        }

    }

}
