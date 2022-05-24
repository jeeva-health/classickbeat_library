package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.common.ui.*
import ai.heart.classickbeats.ui.logging.model.DateTimeValueModel
import ai.heart.classickbeats.ui.logging.model.GlucoseTagModel
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

class BloodGlucoseFragment : Fragment() {

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


    @OptIn(ExperimentalMaterialApi::class)
    @Preview(showBackground = true)
    @Composable
    fun MainCompose() {
        ClassicBeatsTheme {

            BottomSheetContainer(modifier = Modifier)
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    private var bottomSheetScaffoldState: BottomSheetScaffoldState? = null

    private var coroutineScope: CoroutineScope? = null

    @ExperimentalMaterialApi
    @Composable
    fun BottomSheetContainer(modifier: Modifier) {

        bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
        )
        coroutineScope = rememberCoroutineScope()

        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState!!,
            sheetContent = {
                Dialog(modifier = modifier)
            },
            sheetPeekHeight = 0.dp
        ) {

            Column(
                modifier = Modifier
                    .background(color = colorResource(id = R.color.ice_blue))
                    .fillMaxWidth()
                    .fillMaxHeight()

            ) {
                ToolBarWithBackAndAction(
                    modifier = Modifier,
                    title = "Blood Glucose Level",
                    backAction = ::onNavigateBack,
                ) {
                    AddIcon(onAction = { onNavigateAddGlucose() }, actionTitle = "Add")
                }


                GraphLayout(modifier = Modifier)
                val d1 = DateTimeValueModel("12 March", "12:00 PM", "120/190")
                val d2 = DateTimeValueModel("12 March", "12:00 PM", "120/190")
                val d3 = DateTimeValueModel("12 March", "12:00 PM", "120/190")
                val dd: List<DateTimeValueModel> = arrayListOf(d1, d2, d3)
                HistoryLayout(modifier = Modifier, title = "History", unit = "mmHg", dtvList = dd)
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
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
                        .clickable {
                            coroutineScope!!.launch {
                                if (bottomSheetScaffoldState!!.bottomSheetState.isCollapsed) {
                                    bottomSheetScaffoldState!!.bottomSheetState.expand()
                                } else {
                                    bottomSheetScaffoldState!!.bottomSheetState.collapse()
                                }
                            }
                        }
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
                    .height(150.dp)
                    .background(color = RosyPink),
                color = RosyPink
            ) {
                var data: MutableList<LineChartModel> = ArrayList()

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
        AndroidView(modifier = modifier.fillMaxWidth(),
            factory = { context ->
                CustomLineGraph(context, null, data)
            },
            update = {})
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
    fun Dialog(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(color = IceBlue)
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp),
                    text = "Filter by Tag",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )

                val d1 = GlucoseTagModel(R.drawable.juice, "Fasting", true)
                val d2 = GlucoseTagModel(R.drawable.juice, "Post Meal", false)
                val d3 = GlucoseTagModel(R.drawable.juice, "Bedtime", true)
                val d4 = GlucoseTagModel(R.drawable.juice, "Random", false)
                val d5 = GlucoseTagModel(R.drawable.juice, "Random", false)
                val d6 = GlucoseTagModel(R.drawable.juice, "Random", false)
                val ddList: List<GlucoseTagModel> = arrayListOf(d1, d2, d3, d4, d5, d6)


                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(ddList) { dd: GlucoseTagModel ->
                        ItemTag(modifier = Modifier, dd.icon,
                            tag = dd.tag, selected = dd.selected,
                            onClick = {}
                        )
                    }
                }
            }
        }
    }

    //........................................Function...................................//
    private fun onNavigateBack() {
        navController.navigate(BloodGlucoseFragmentDirections.actionBloodGlucoseFragmentToMyHealthFragment())
    }

    private fun onNavigateAddGlucose() {
        navController.navigate(BloodGlucoseFragmentDirections.actionBloodGlucoseFragmentToAddBloodGlucoseFragment())
    }


}