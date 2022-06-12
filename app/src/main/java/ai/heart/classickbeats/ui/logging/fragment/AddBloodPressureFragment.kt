package ai.heart.classickbeats.ui.logging.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.logging.compose.AddPressureMainCompose
import ai.heart.classickbeats.ui.logging.model.BloodPressureViewData
import ai.heart.classickbeats.ui.theme.ClassicBeatsTheme
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.ExperimentalPagingApi
import java.util.*

class AddBloodPressureFragment : Fragment() {

    private val navController: NavController by lazy {
        Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )
    }


    @OptIn(ExperimentalPagingApi::class)
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
                    //  MainCompose()
                    val date = remember { mutableStateOf("") }
                    val dateCalendar = Calendar.getInstance()
                    val year = dateCalendar.get(Calendar.YEAR)
                    val month = dateCalendar.get(Calendar.MONTH)
                    val day = dateCalendar.get(Calendar.DAY_OF_MONTH)
                    date.value = "$day/" + (month + 1) + "/$year"
                    dateCalendar.time = Date()

                    val calendar = Calendar.getInstance()
                    val hour = calendar[Calendar.HOUR_OF_DAY]
                    val minute = calendar[Calendar.MINUTE]
                    val time = remember { mutableStateOf("") }
                    time.value = "$hour:$minute"

                    val systolic = remember { mutableStateOf(0) }
                    val diastolic = remember { mutableStateOf(0) }


                    val model = BloodPressureViewData(
                        date,
                        time,
                        systolic,
                        diastolic
                    )


                    AddPressureMainCompose(
                        context = context,
                        title = "Blood Pressure Level",
                        data = model,
                        onSubmit = { TODO() },
                        onBackPressed = { onBackPressed() }

                    )
                }
            }
        }
    }



    //..........................FUNCTIONS...........................//
    private fun onBackPressed() {
        navController.navigate(AddBloodPressureFragmentDirections.actionBloodPressureFragmentSelf())
    }
}

