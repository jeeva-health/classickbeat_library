package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentWellnessCategoryBinding
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
                    backgroundImage.setImageResource(wellnessCategory.getBackgroundImage())
                }
            }

            pageCategory.text = wellnessCategory.name

            pageTitle.text = getString(wellnessModel.title)

            pageMessage.text = getString(wellnessModel.message)

            shortMeditationCard.setSafeOnClickListener {
                playMeditation(
                    wellnessCategory,
                    "https://public-sound.s3.ap-south-1.amazonaws.com/public.mp3"
                )
            }

            longMeditationCard.setSafeOnClickListener {
                playMeditation(
                    wellnessCategory,
                    "https://public-sound.s3.ap-south-1.amazonaws.com/public.mp3"
                )
            }

            notificationTxt.text = getString(wellnessCategory.getReminderMessage())
        }
    }

    private fun playMeditation(wellnessCategory: WellnessType, mediaUrl: String) {
        val action =
            WellnessCategoryFragmentDirections.actionWellnessCategoryFragmentToMediaPlayerFragment(
                mediaUrl = mediaUrl,
                wellnessType = wellnessCategory
            )
        navController.navigate(action)
    }
}