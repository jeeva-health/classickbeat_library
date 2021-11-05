package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentWellnessCategoryBinding
import ai.heart.classickbeats.model.WellnessType
import ai.heart.classickbeats.utils.setDarkStatusBar
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WellnessCategoryFragment : Fragment(R.layout.fragment_wellness_category) {

    private val binding by viewBinding(FragmentWellnessCategoryBinding::bind)

    private lateinit var navController: NavController

    private val wellnessViewModel: WellnessViewModel by activityViewModels()

    private val args: WellnessCategoryFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDarkStatusBar()

        navController = findNavController()

        val wellnessCategory = args.wellnessType
        val wellnessModel = wellnessViewModel.wellnessCategoryMap[wellnessCategory]
            ?: wellnessViewModel.sleepMeditation

        binding.apply {

            backArrow.setSafeOnClickListener {
                navController.navigateUp()
            }

            // To handle different background image for categories
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
                    backgroundImage.setImageResource(getBackgroundImage(wellnessCategory))
                }
            }

            pageCategory.text = wellnessCategory.name

            pageTitle.text = getString(wellnessModel.title)

            pageMessage.text = getString(wellnessModel.message)

            shortMeditationCard.setSafeOnClickListener {
                playShortMeditation()
            }

            longMeditationCard.setSafeOnClickListener {
                playLongMeditation()
            }

            notificationTxt.text = getString(getReminderMessage(wellnessCategory))
        }
    }

    private fun playShortMeditation() {
        startActivity(Intent(requireActivity(), MediaPlayerActivity::class.java))
    }

    private fun playLongMeditation() {
        startActivity(Intent(requireActivity(), MediaPlayerActivity::class.java))
    }

    private fun getBackgroundImage(wellnessType: WellnessType) = when (wellnessType) {
        WellnessType.SLEEP -> R.drawable.bg_star_2
        WellnessType.BLOOD_PRESSURE -> R.drawable.bg_curved_lines_2
        WellnessType.STRESS -> R.drawable.bg_contour_2
        WellnessType.IMMUNITY -> R.drawable.bg_shade_2
        else -> 0
    }

    private fun getReminderMessage(wellnessType: WellnessType) = when (wellnessType) {
        WellnessType.SLEEP -> R.string.reminder_sleep_message
        WellnessType.BLOOD_PRESSURE -> R.string.reminder_bp_message
        WellnessType.ANGER -> R.string.reminder_anger_message
        WellnessType.STRESS -> R.string.reminder_stress_message
        WellnessType.IMMUNITY -> R.string.reminder_immunity_message
    }
}