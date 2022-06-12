package ai.heart.classickbeats.ui.common.compose

import ai.heart.classickbeats.R
import ai.heart.classickbeats.model.Date
import ai.heart.classickbeats.model.Time
import ai.heart.classickbeats.ui.logging.model.HistoryItemViewData
import ai.heart.classickbeats.ui.theme.CharcoalGray
import ai.heart.classickbeats.ui.theme.ClassicBeatsTheme
import ai.heart.classickbeats.ui.theme.PaleGray
import ai.heart.classickbeats.ui.theme.WarmGray
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistoryLayout(
    title: String,
    unit: String,
    dtvList: List<HistoryItemViewData>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalGray,
                modifier = Modifier.padding(8.dp)
            )
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .padding(top = 4.dp)
                    .background(color = PaleGray, shape = RoundedCornerShape(4.dp))
                    .padding(vertical = 8.dp, horizontal = 12.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Date & Time",
                    color = WarmGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = unit,
                    color = WarmGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            LazyColumn {
                itemsIndexed(dtvList) { index: Int, data: HistoryItemViewData ->
                    HistoryItemView(data = data)
                    if (index != dtvList.size - 1) {
                        Divider(color = PaleGray, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewHistoryLayout() {
    val list = listOf(
        HistoryItemViewData(date = "Today", time = "12:45 pm", value = "125"),
        HistoryItemViewData(date = "Today", time = "12:45 pm", value = "125"),
        HistoryItemViewData(date = "Today", time = "12:45 pm", value = "125"),
        HistoryItemViewData(date = "Today", time = "12:45 pm", value = "125"),
        HistoryItemViewData(date = "Today", time = "12:45 pm", value = "125")
    )
    ClassicBeatsTheme {
        Surface {
            HistoryLayout(title = "History", unit = "mg/dl", dtvList = list)
        }
    }
}

@Composable
fun HistoryItemView(
    data: HistoryItemViewData,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.padding(start = 4.dp)) {
            Text(
                text = data.date,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = data.time,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = data.value,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxHeight()
        )
    }
}

@Composable
fun DateTimeSelectionView(
    icon: Int,
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(16.dp, 4.dp)
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.white),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
            .clickable { onClick.invoke() },
    ) {
        Image(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            painter = painterResource(id = icon), contentDescription = null
        )
        Column(modifier = Modifier
            .padding(16.dp, 0.dp)
            .weight(8f),
            content = {
                Text(
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 4.dp),
                    color = CharcoalGray,
                    fontSize = 12.sp,
                    text = label
                )
                Text(
                    color = CharcoalGray,
                    fontSize = 16.sp,
                    text = value,
                    fontWeight = FontWeight.Bold
                )
            }
        )
        Spacer(modifier = Modifier.weight(1f))

        Image(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            painter = painterResource(R.drawable.ic_baseline_keyboard_arrow_down_24),
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun PreviewDateTimeSelectionView() {
    ClassicBeatsTheme {
        Surface {
            DateTimeSelectionView(
                icon = R.drawable.date,
                label = "Unit",
                value = "Value",
                onClick = {},
            )
        }
    }
}

fun showTimePicker(
    context: Context,
    defaultTime: Time,
    onTimeChange: (Time) -> Unit
) {
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            val time = Time(hour, minute)
            onTimeChange(time)
        }, defaultTime.hourOfDay, defaultTime.minute, false
    )
    timePickerDialog.show()
}

fun showDatePicker(
    context: Context,
    defaultDate: Date,
    onDateChange: (Date) -> Unit
) {
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val date = Date(dayOfMonth, month, year)
            onDateChange(date)
        }, defaultDate.year, defaultDate.month, defaultDate.day
    )
    datePickerDialog.show()
}

@Composable
fun ItemGraph(modifier: Modifier, color: Color, type: String, value: String) {
    Row(
        modifier = modifier
            .padding(12.dp, 25.dp)
    ) {
        Box(
            modifier = Modifier

                .size(16.dp)
                .background(color = color, shape = CircleShape)
                .border(width = 3.dp, color = Color.White, shape = CircleShape)
                .align(alignment = Alignment.Top)
        )
        Column(modifier = modifier) {

            Text(
                text = type,
                fontSize = 16.sp,
                color = CharcoalGray,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(4.dp, 0.dp)
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
}
