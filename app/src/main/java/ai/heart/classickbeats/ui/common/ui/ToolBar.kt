package ai.heart.classickbeats.ui.common.ui

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.theme.CharcoalGray
import ai.heart.classickbeats.ui.theme.RosyPink
import ai.heart.classickbeats.ui.theme.White
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun ToolBar(){

}

@Composable
fun ToolBarWithBackAndAction(
    modifier: Modifier,
    title: String,
    backAction: () -> Unit,
    action: @Composable ()->Unit
) {
    Row(
        modifier = modifier
            .padding(0.dp,50.dp,0.dp,0.dp)
            .height(56.dp)
            .fillMaxWidth()
            .background(color = White),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp)
                .width(24.dp)
                .height(24.dp)
                .clickable { backAction },
            content = {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_arrow_back),
                    contentDescription = null
                )
            }
        )

        Text(
            // modifier = Modifier.padding(16.dp),
            text = title,
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            color = CharcoalGray,
        )

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        )
        action()
    }
}

@Composable
fun AddIcon(onAction: () -> Unit, actionTitle: String) {
    Row(
        modifier = Modifier
            .padding(8.dp, 10.dp)
            .fillMaxHeight()
            .fillMaxHeight()
            .border(
                width = 2.dp,
                color = RosyPink,
                shape = RoundedCornerShape(25.dp)
            )
            .padding(8.dp, 8.dp)
            .clickable { onAction.invoke() } //invoke call the function
            .clickable(onClick = onAction) // both do the same thing
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_plus),
            contentDescription = null,
            modifier = Modifier.fillMaxHeight(),
            colorFilter = ColorFilter.tint(RosyPink),
            alignment = Alignment.Center,

            )

        Text(
            // modifier = Modifier.padding(16.dp),
            text = actionTitle,
            fontSize = 16.sp,
            color = RosyPink,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier
                .padding(2.dp, 0.dp)
                .align(alignment = Alignment.CenterVertically)
        )
    }
}


