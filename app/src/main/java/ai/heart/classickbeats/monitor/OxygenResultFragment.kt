package ai.heart.classickbeats.monitor

import ai.heart.classickbeats.MainActivity
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentOxygenResultBinding
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton


class OxygenResultFragment : Fragment(R.layout.fragment_oxygen_result) {

    private val binding by viewBinding(FragmentOxygenResultBinding::bind)

    private lateinit var navController: NavController

    private lateinit var testAgainButton: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).showSystemUI()

        navController = findNavController()

        testAgainButton = binding.reTestButton

        testAgainButton.setSafeOnClickListener {
            navigateToSelectionFragment()
        }
    }

    private fun navigateToSelectionFragment() {
        val action =
            OxygenResultFragmentDirections.actionOxygenResultFragmentToCameraSelectionFragment()
        navController.navigate(action)
    }
}