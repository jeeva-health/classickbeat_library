package ai.heart.classickbeats.monitor

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentHeartResultBinding
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton


class HeartResultFragment : Fragment(R.layout.fragment_heart_result) {

    private val binding by viewBinding(FragmentHeartResultBinding::bind)

    private lateinit var navController: NavController

    private lateinit var testAgainButton: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        testAgainButton = binding.reTestButton

        testAgainButton.setSafeOnClickListener {
            navigateToSelectionFragment()
        }
    }

    private fun navigateToSelectionFragment() {
        val action =
            HeartResultFragmentDirections.actionHeartResultFragmentToCameraSelectionFragment()
        navController.navigate(action)
    }
}
