package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentLogBpBinding
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LogBpFragment : Fragment(R.layout.fragment_log_bp) {

    private val binding by viewBinding(FragmentLogBpBinding::bind)

    private lateinit var navController: NavController

    private lateinit var cameraButton: AppCompatImageView

    private lateinit var submitButton: AppCompatButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        cameraButton = binding.camera

        submitButton = binding.submitBtn

        submitButton.setSafeOnClickListener {
            navController.navigateUp()
        }
    }

}