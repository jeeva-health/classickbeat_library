package ai.heart.classickbeats.ui.logging.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.logging.BloodGlucoseViewModel
import ai.heart.classickbeats.ui.logging.compose.BloodGlucoseScreen
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@AndroidEntryPoint
class BloodGlucoseFragment : Fragment() {

    private val viewModel: BloodGlucoseViewModel by viewModels()

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
                BloodGlucoseScreen(
                    onBackPressed = { onNavigateBack() },
                    onAddGlucose = { onNavigateAddGlucose() }
                )
            }
        }
    }

    private fun onNavigateBack() {
        navController.navigateUp()
    }

    private fun onNavigateAddGlucose() {
        navController.navigate(BloodGlucoseFragmentDirections.actionBloodGlucoseFragmentToAddBloodGlucoseFragment())
    }
}
