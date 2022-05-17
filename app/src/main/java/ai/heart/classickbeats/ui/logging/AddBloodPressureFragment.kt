package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.theme.ClassicBeatsTheme
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment

class AddBloodPressureFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ClassicBeatsTheme() {
                    MainCompose()
                }
            }
        }
    }


    @SuppressLint("ResourceType")
    @Composable
    @Preview(showBackground = true)
    fun MainCompose() {
        val brush = Brush.verticalGradient(
            listOf(Color(0xFFE1F2F6), Color(0xFFD3062A)),
        )


        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.ice_blue))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                ToolBarLayout(modifier = Modifier)
                DateTimeItem(
                    modifier = Modifier,
                    icon = R.drawable.date,
                    unit = "Date",
                    value = "Today"
                )
                DateTimeItem(
                    modifier = Modifier,
                    icon = R.drawable.time,
                    unit = "Time",
                    value = "2:30 PM"
                )
                ReadingLayout(modifier = Modifier)

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                Button(
                    onClick = { onBackPressed() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(color = Color(R.color.red)),
                ) {
                    Text(
                        text = "SAVE",
                        color = colorResource(id = R.color.white),
                        fontSize = 16.sp,

                        )

                }


            }
        }

    }

    @Composable
    fun ToolBarLayout(modifier: Modifier) {
        Row(
            modifier = modifier
                .height(56.dp)
                .fillMaxWidth()
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(16.dp)
                    .width(24.dp)
                    .height(24.dp)
                    .clickable { onBackPressed() },
                content = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_arrow_back),
                        contentDescription = null
                    )
                }
            )

            Text(
                // modifier = Modifier.padding(16.dp),
                text = "Blood Pressure",
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif,
            )
        }
    }

    @Composable
    fun DateTimeItem(modifier: Modifier, icon: Int, unit: String, value: String) {
        Row(
            modifier = modifier
                .padding(16.dp, 4.dp)
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.white), shape = RectangleShape)
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
                        color = Color.Black,
                        fontSize = 12.sp,
                        text = unit
                    )
                    Text(
                        color = Color.Black,
                        fontSize = 16.sp,
                        text = value
                    )
                }
            )

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
    fun ReadingLayout(modifier: Modifier) {
        Column(
            modifier = modifier
                .padding(16.dp, 10.dp)
                .background(color = Color.White)
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
                    text = "75/65",
                    fontSize = 24.sp,

                    )
                Text(
                    text = "(mmgh)",
                    modifier = Modifier
                        .align(alignment = Alignment.Bottom)
                )

            }
            ScaleLayout(modifier = Modifier, diagnostic = "Systolic (High)", color = R.color.red)
            ScaleLayout(modifier = Modifier, diagnostic = "Systolic (High)", color = R.color.bright_blue_2)
        }
    }

    @Composable
    fun ScaleLayout(modifier: Modifier, diagnostic: String,color:Int) {

        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp, 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color = colorResource(id = color), shape = CircleShape)
                        .border(width = 3.dp, color = Color.White, shape = CircleShape)
                        .align(alignment = Alignment.Bottom)
                )
                Text(
                    text = diagnostic,
                    fontSize = 16.sp,
                    fontWeight = Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(4.dp, 0.dp)
                )
            }

            Surface(
                modifier = Modifier
                    .padding(0.dp, 16.dp)
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(color = colorResource(id = color))

            ) {

            }

        }

    }


    //..........................FUNCTIONS...........................//
    private fun onBackPressed() {
        TODO("Not yet implemented")
    }


}

