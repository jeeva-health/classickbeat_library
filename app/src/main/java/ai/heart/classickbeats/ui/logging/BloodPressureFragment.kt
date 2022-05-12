package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment

class BloodPressureFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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


    @SuppressLint("ResourceType")
    @Composable
    @Preview
    fun MainCompose() {
        val brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFE1F2F6), Color(0xFFD3062A)),

            )
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(brush = brush)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                ToolBarLayout()
                MaterialTheme() {
                    Text("first compose")

                }
            }


        }

    }

    @Composable
    fun ToolBarLayout() {
        Row(
            modifier = Modifier
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
//            {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_baseline_arrow_back),
//                    contentDescription = null
//                )
//            }
            Text(
                // modifier = Modifier.padding(16.dp),
                text = "Blood Pressure",
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif,
            )
        }
    }


    //..........................FUNCTIONS...........................//
    private fun onBackPressed() {
        TODO("Not yet implemented")
    }


}

