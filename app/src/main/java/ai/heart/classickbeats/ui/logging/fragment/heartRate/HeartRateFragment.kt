package ai.heart.classickbeats.ui.logging.fragment.heartRate

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.theme.ClassicBeatsTheme
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class HeartRateFragment : Fragment() {

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
                ClassicBeatsTheme {
                    MainCompose()
                }
            }
        }
    }


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    @Preview(showBackground = true)
    fun MainCompose() {
        val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
        )
        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        val onToggleTagView = {
            coroutineScope.launch {
                if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                    bottomSheetScaffoldState.bottomSheetState.expand()
                } else {
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                }
            }
            Unit
        }

        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                HeartRateTagBottomSheetDialog()
            },
            sheetPeekHeight = 0.dp
        ) {
            HeartRateScreen(
                onToggleTagView = onToggleTagView,
                onNavigateMeasureBPMKnowMore = { onNavigateMeasureBPMKnowMore() },
                onMeasureHeartRhythm = { onMeasureHeartRhythm() },
                onBackPress = {onBackPress()}

            )
        }
    }

    private fun onNavigateMeasureBPMKnowMore() {
        navController.navigate(HeartRateFragmentDirections.actionHeartRateFragmentToMeasureBPMKnowMoreFragment())
    }

    private fun onMeasureHeartRhythm() {
        navController.navigate(HeartRateFragmentDirections.actionHeartRateFragmentToScanFragment())
    }

    private fun onBackPress() {
        navController.navigate(HeartRateFragmentDirections.actionHeartRateFragmentToMyHealthFragment())
    }

}