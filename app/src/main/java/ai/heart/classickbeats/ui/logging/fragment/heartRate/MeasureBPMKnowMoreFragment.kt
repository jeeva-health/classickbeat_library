package ai.heart.classickbeats.ui.logging.fragment.heartRate

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.theme.ClassicBeatsTheme
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation

class MeasureBPMKnowMoreFragment : Fragment() {


    private val navController: NavController by lazy {
        Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ClassicBeatsTheme {
                    MainCompose()
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun MainCompose() {
        MeasureBPMKnowMoreScreen { onBackPress() }
    }

    private fun onBackPress() {
        navController.navigate(MeasureBPMKnowMoreFragmentDirections.actionMeasureBPMKnowMoreFragmentToHeartRateFragment())
    }

}