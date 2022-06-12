package ai.heart.classickbeats.ui.logging.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.common.ui.*
import ai.heart.classickbeats.ui.logging.model.DateTimeValueModel
import ai.heart.classickbeats.ui.theme.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import java.util.*

class BloodPressureFragment : Fragment() {

    private val navController: NavController by lazy {
        Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )
    }

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
                ToolBarWithBackAndAction(
                    modifier = Modifier,
                    title = "Blood Pressure",
                    backAction = { onNavigateBack() },
                ) {
                    AddIcon(onAction = { onNavigateAddPressure() }, actionTitle = "Add")
                }
                GraphLayout(modifier = Modifier)
                val d1 = DateTimeValueModel("12 March", "12:00 PM", "120/190")
                val d2 = DateTimeValueModel("12 March", "12:00 PM", "120/190")
                val d3 = DateTimeValueModel("12 March", "12:00 PM", "120/190")
                val dd: List<DateTimeValueModel> = arrayListOf(d1, d2, d3)

                HistoryLayout(
                    modifier = Modifier,
                    title = "History",
                    unit = "",
                    dtvList = dd
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
                    .padding(0.dp, 16.dp)
                    .fillMaxWidth()
                    .height(250.dp)
            ) {

                val chatData: MutableList<CandleStickChartModel> = ArrayList()

                chatData.add(CandleStickChartModel(20f, 10f, 12f, 15f, Date(2022, 2, 1)))
                chatData.add(CandleStickChartModel(20f, 10f, 14f, 15f, Date(2022, 2, 2)))
                chatData.add(CandleStickChartModel(20f, 10f, 15f, 15f, Date(2022, 2, 3)))
                chatData.add(CandleStickChartModel(20f, 10f, 18f, 15f, Date(2022, 2, 4)))
                chatData.add(CandleStickChartModel(18f, 11f, 18f, 15f, Date(2022, 2, 4)))
                chatData.add(CandleStickChartModel(25f, 10f, 18f, 15f, Date(2022, 2, 4)))
                chatData.add(CandleStickChartModel(12f, 10f, 10f, 15f, Date(2022, 2, 5)))

                AndroidView(modifier = modifier
                    .fillMaxWidth()
                    .height(500.dp),
                    factory = { context ->
                        CustomCandleStickChat(context, null, chatData)
                    },
                    update = {/**/ })
            }
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


    //..............................Functions......................................//
    private fun onNavigateBack() {
        navController.navigateUp()
    }

    private fun onNavigateAddPressure() {
        navController.navigate(BloodPressureFragmentDirections.actionBloodPressureFragmentToAddBloodPressureFragment())
    }

}