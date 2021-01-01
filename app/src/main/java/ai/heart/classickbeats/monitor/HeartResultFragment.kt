package ai.heart.classickbeats.monitor

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentHeartResultBinding
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import java.io.File
import kotlin.math.roundToInt


class HeartResultFragment : Fragment(R.layout.fragment_heart_result) {

    private val binding by viewBinding(FragmentHeartResultBinding::bind)

    private lateinit var navController: NavController

    private lateinit var testAgainButton: MaterialButton

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        testAgainButton = binding.reTestButton

        monitorViewModel.hearRateResult?.let {
            binding.quality.text = it.quality
            binding.aFib.text = it.aFib
            binding.bpm.text = it.bpm.roundToInt().toString()
            binding.hrv.text = "%.2f ms".format(it.hrv)
        }

        testAgainButton.setSafeOnClickListener {
            navigateToSelectionFragment()
        }

        Glide.with(this).load(File("/storage/emulated/0/Pictures/ppg.jpg")).into(binding.graph)
    }

    private fun navigateToSelectionFragment() {
        val action =
            HeartResultFragmentDirections.actionHeartResultFragmentToCameraSelectionFragment()
        navController.navigate(action)
    }
}
