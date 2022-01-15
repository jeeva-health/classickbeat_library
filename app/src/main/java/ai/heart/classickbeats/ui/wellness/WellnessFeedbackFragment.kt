package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentWellnessFeedbackBinding
import ai.heart.classickbeats.model.WellnessType
import ai.heart.classickbeats.utils.setDarkStatusBar
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WellnessFeedbackFragment : Fragment(R.layout.fragment_wellness_feedback) {

    private val binding by viewBinding(FragmentWellnessFeedbackBinding::bind)

    private val args: WellnessFeedbackFragmentArgs by navArgs()

    private val wellnessViewModel: WellnessViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDarkStatusBar()

        navController = findNavController()

        val wellnessCategory = args.wellnessType

        binding.apply {

            completionMessage.text = "Well done! You have completed a meditation."

            cross.setSafeOnClickListener {
                navigateUp()
            }

            when (wellnessCategory) {
                WellnessType.ANGER -> {
                    backgroundCircle1.visibility = View.VISIBLE
                    backgroundCircle2.visibility = View.VISIBLE
                    backgroundImage.visibility = View.GONE
                }
                else -> {
                    backgroundCircle1.visibility = View.GONE
                    backgroundCircle2.visibility = View.GONE
                    backgroundImage.visibility = View.VISIBLE
                    backgroundImage.setImageResource(wellnessCategory.getBackgroundImage())
                }
            }
        }
    }

    private fun navigateUp() {
        navController.navigateUp()
    }

}
