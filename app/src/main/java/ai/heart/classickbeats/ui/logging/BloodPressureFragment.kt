package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.logging.model.DateTimeValueModel
import ai.heart.classickbeats.ui.theme.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment

class BloodPressureFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MainCompose()
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun MainCompose() {
        ClassicBeatsTheme {
            Column(
                modifier = Modifier
                    .background(color = colorResource(id = R.color.ice_blue))
                    .fillMaxWidth()
                    .fillMaxHeight()

            ) {
                ToolBarLayout(modifier = Modifier)
                GraphLayout(modifier = Modifier)
                var d1 = DateTimeValueModel("12 March", "12:00 PM", "120/190")
                var d2 = DateTimeValueModel("12 March", "12:00 PM", "120/190")
                var d3 = DateTimeValueModel("12 March", "12:00 PM", "120/190")
                var dd: List<DateTimeValueModel> = arrayListOf(d1, d2, d3)

                HistoryLayout(modifier = Modifier, dtvList = dd)
            }
        }
    }

    @Composable
    fun ToolBarLayout(modifier: Modifier) {
        Row(
            modifier = modifier
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
                    .clickable { },
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

            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )

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
                    .align(Alignment.CenterVertically)
                    .padding(8.dp, 8.dp)
                    .clickable {/*TODO*/ }
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
                    text = "Add",
                    fontSize = 16.sp,
                    color = RosyPink,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier
                        .padding(2.dp, 0.dp)
                        .align(alignment = Alignment.CenterVertically)
                )
            }
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
                    .padding(0.dp,16.dp)
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(color = RosyPink),
                color = RosyPink
            ) {}
        }
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

    @Composable
    fun HistoryLayout(modifier: Modifier, dtvList: List<DateTimeValueModel>) {
        Column(
            modifier = modifier
                .padding(16.dp, 4.dp)
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.background, shape = RoundedCornerShape(8.dp))
                .padding(16.dp, 4.dp)
        ) {
            Text(
                text = "History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalGray,
                modifier = Modifier.padding(0.dp,16.dp)
            )
            Row(
                modifier = Modifier
                    .background(color = Color(0xFFEDF3F5), shape = RoundedCornerShape(4.dp))
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
                    text = "mmHg",
                    color = WarmGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

            }
            LazyColumn {
                items(dtvList) { dtv: DateTimeValueModel ->
                    ItemHistory(modifier = Modifier, dtv = dtv)
                    Divider(color = Color(0xFFEDF3F5), thickness = 1.dp)
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


}