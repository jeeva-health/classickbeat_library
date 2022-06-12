package ai.heart.classickbeats.ui.logging.compose

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.theme.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlucoseTagItemView(
    icon: Int,
    tag: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = columnModifier.clickable(onClick = onClick),
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = tag,
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

@Preview
@Composable
fun PreviewGlucoseTagItemView() {
    ClassicBeatsTheme {
        Surface {
            GlucoseTagItemView(
                icon = R.drawable.juice,
                tag = "Juice",
                selected = false,
                onClick = { }
            )
        }
    }
}

@Preview
@Composable
fun PreviewGlucoseTagItemViewSelected() {
    ClassicBeatsTheme {
        Surface {
            GlucoseTagItemView(
                icon = R.drawable.juice,
                tag = "Juice",
                selected = true,
                onClick = { }
            )
        }
    }
}
