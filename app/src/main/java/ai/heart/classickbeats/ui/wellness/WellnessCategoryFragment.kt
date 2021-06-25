package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentWellnessCategoryBinding
import ai.heart.classickbeats.model.WellnessType
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
                WellnessType.SLEEP, WellnessType.BLOOD_PRESSURE -> {
                    backgroundCircle1.visibility = View.GONE
                    backgroundCircle2.visibility = View.GONE
                    backgroundImage1.visibility = View.VISIBLE
                    backgroundImage2.visibility = View.GONE
                    backgroundImage1.setImageResource(getBackgroundImage(wellnessCategory))
                }
                WellnessType.ANGER -> {
                    backgroundCircle1.visibility = View.VISIBLE
                    backgroundCircle2.visibility = View.VISIBLE
                    backgroundImage1.visibility = View.GONE
                    backgroundImage2.visibility = View.GONE
                }
                WellnessType.STRESS, WellnessType.IMMUNITY -> {
                    backgroundCircle1.visibility = View.GONE
                    backgroundCircle2.visibility = View.GONE
                    backgroundImage1.visibility = View.GONE
                    backgroundImage2.visibility = View.VISIBLE
                    backgroundImage2.setImageResource(getBackgroundImage(wellnessCategory))
                }
            }

            pageCategory.text = wellnessCategory.name

            pageTitle.text = wellnessModel.title

            pageMessage.text = wellnessModel.message

            shortMeditationCard.setSafeOnClickListener {
                playShortMeditation()
            }

            longMeditationCard.setSafeOnClickListener {
                playLongMeditation()
            }
        }
    }

    private fun playShortMeditation() {
        startActivity(Intent(requireActivity(), MediaPlayerActivity::class.java))
    }

    private fun playLongMeditation() {
        startActivity(Intent(requireActivity(), MediaPlayerActivity::class.java))
    }

    private fun getBackgroundImage(wellnessType: WellnessType) = when (wellnessType) {
        WellnessType.SLEEP -> R.drawable.bg_star
        WellnessType.BLOOD_PRESSURE -> R.drawable.bg_curved_lines_2
        WellnessType.STRESS -> R.drawable.bg_contour_2
        WellnessType.IMMUNITY -> R.drawable.bg_shade_2
        else -> 0
    }
}