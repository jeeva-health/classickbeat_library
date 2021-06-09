package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentWellnessCategoryBinding
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
//        val action =
//            WellnessCategoryFragmentDirections.actionWellnessCategoryFragmentToMediaPlayerFragment()
//        navController.navigate(action)
    }

    private fun playLongMeditation() {
        startActivity(Intent(requireActivity(), MediaPlayerActivity::class.java))
//        val action =
//            WellnessCategoryFragmentDirections.actionWellnessCategoryFragmentToMediaPlayerFragment()
//        navController.navigate(action)
    }
}