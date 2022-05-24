package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.common.ui.CustomSliderScale
import ai.heart.classickbeats.ui.common.ui.DateTimeItem
import ai.heart.classickbeats.ui.common.ui.ItemTag
import ai.heart.classickbeats.ui.common.ui.ToolBarWithBackAndAction
import ai.heart.classickbeats.ui.logging.model.GlucoseTagModel
import ai.heart.classickbeats.ui.theme.*
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation

class AddBloodGlucoseFragment : Fragment() {

    private val navController: NavController by lazy {
        Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )
    }
   // var glucoseLevelReading = ""

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


        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(color = LightPink)
        ) {

            ToolBarWithBackAndAction(
                modifier = Modifier,
                title = "Blood Glucose Level",
                backAction = {onBackPressed()},
            ) {}

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
                    .background(color = Color.Transparent)

            )

            Button(
                onClick = { /*todo*/ },
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
        Column(
            modifier = modifier
                .padding(16.dp, 10.dp)
                .background(color = White, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ScaleLayout(
                modifier = Modifier
                    .padding(0.dp, 16.dp)
                    .fillMaxWidth(),
                maxValue = 300
            )

            Text(
                text = "Add a Tag",
                fontSize = 16.sp,
                fontWeight = Bold,
                color = CharcoalGray,
                modifier = Modifier
                    .align(Start)
                    .padding(16.dp)
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

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Start)
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
                    fontWeight = Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier
                        .padding(2.dp, 0.dp)
                        .align(alignment = Alignment.CenterVertically)
                )
            }
        }
    }

    @Composable
    fun ScaleLayout(modifier: Modifier, maxValue: Int) {

        var re:MutableLiveData<String> = MutableLiveData("00")

        var  glucoseLevelReading by remember {
            mutableStateOf("00")
        }
        var reading:String? = ""


        Row(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
                .fillMaxWidth()
                ,
            horizontalArrangement = Arrangement.Center

        ) {

            Text(
                text = "" + re.value,
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

        AndroidView(modifier = modifier.fillMaxWidth(),
            factory = { context ->
                CustomSliderScale(context, null, maxValue)
            },
            update = {
                re.value = it.reading.toString()
            })

    }


    //..........................FUNCTIONS...........................//
    private fun onBackPressed() {
        navController.navigate(AddBloodGlucoseFragmentDirections.actionAddBloodGlucoseFragmentToBloodGlucoseFragment())
    }


}

