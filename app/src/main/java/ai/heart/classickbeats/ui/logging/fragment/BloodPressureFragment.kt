package ai.heart.classickbeats.ui.logging.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.logging.BloodPressureViewModel
import ai.heart.classickbeats.ui.logging.compose.BloodPressureScreen
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@AndroidEntryPoint
class BloodPressureFragment : Fragment() {

    private val viewModel: BloodPressureViewModel by viewModels()

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
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MainCompose()
            }
        }
    }


    @Composable
    @Preview(showBackground = true)
    fun MainCompose() {
        BloodPressureScreen(
            onBackPress = {onNavigateBack()},
            onAddPressure = {onNavigateAddPressure()}
        )
    }

    private fun onNavigateBack() {
        navController.navigateUp()
    }

    private fun onNavigateAddPressure() {
        navController.navigate(BloodPressureFragmentDirections.actionBloodPressureFragmentToAddBloodPressureFragment())
    }
}
