package ai.heart.classickbeats.ui.common.ui

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.logging.model.DateTimeValueModel
import ai.heart.classickbeats.ui.theme.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView


@Preview
@Composable
fun CommonComponents() {
    DateTimeItem(
        modifier = Modifier,
        icon = R.drawable.date,
        unit = "Date",
        value = "Today"
    )

    ItemTag(modifier = Modifier, 0,
        tag = "tag", selected = true,
        onClick = {}
    )
}

@Composable
fun HistoryLayout(
    modifier: Modifier,
    title: String,
    unit: String,
    dtvList: List<DateTimeValueModel>
) {
    Column(
        modifier = modifier
            .padding(16.dp, 4.dp)
            .fillMaxWidth()
            .background(color = White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp, 4.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = CharcoalGray,
            modifier = Modifier.padding(0.dp, 16.dp)
        )
        Row(
            modifier = Modifier
                .background(color = PaleGray, shape = RoundedCornerShape(4.dp))
                .padding(12.dp, 9.dp)
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
            items(dtvList) { dtv: DateTimeValueModel ->
                ItemHistory(modifier = Modifier, dtv = dtv)
                Divider(color = PaleGray, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun ItemHistory(modifier: Modifier, dtv: DateTimeValueModel) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp, 8.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column() {
            Text(
                text = dtv.date,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dtv.time,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = dtv.value,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxHeight(),

            )
    }
}

@Composable
fun DateTimeItem(modifier: Modifier, icon: Int, unit: String, value: String) {
    Row(
        modifier = modifier
            .padding(16.dp, 4.dp)
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.white),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
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
                    text = unit
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

@Composable
fun ItemTag(modifier: Modifier, icon: Int, tag: String, selected: Boolean, onClick: () -> Unit) {
    val columnModifier: Modifier
    val colorPrefix: Color
    if (selected) {
        columnModifier = modifier
            .padding(4.dp)
            .height(88.dp)
            .widthIn(min = 68.dp)
            .background(color = LightPink)
            .border(
                width = 1.dp, color = RosyPink,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(12.dp)
        colorPrefix = RosyPink

    } else {
        columnModifier = Modifier
            .padding(4.dp)
            .height(88.dp)
            .widthIn(min = 68.dp)
            .border(
                width = 1.dp, color = PaleGray,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(12.dp)
        colorPrefix = CharcoalGray

    }
    Column(
        modifier = columnModifier.clickable(onClick = { onClick }),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "",
            colorFilter = ColorFilter.tint(colorPrefix)
        )
        Text(
            text = tag,
            fontSize = 12.sp,
            modifier = Modifier.padding(0.dp, 10.dp),
            color = colorPrefix
        )
    }
}

