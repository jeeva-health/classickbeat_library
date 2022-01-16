package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentWellnessCategoryBinding
import ai.heart.classickbeats.model.MeditationMedia
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

    private lateinit var meditationAdapter: MeditationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wellnessViewModel.getMeditationList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDarkStatusBar()

        navController = findNavController()

        meditationAdapter = MeditationAdapter(requireContext(), itemClickListener)
        binding.meditationRv.adapter = meditationAdapter

        val wellnessCategory = args.wellnessType
        val wellnessModel = wellnessViewModel.wellnessCategoryMap[wellnessCategory]!!

        wellnessViewModel.showLoading.observe(viewLifecycleOwner, {
            if (!it) {
                showUI()
            }
        })

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

            pageCategory.text = getString(wellnessCategory.getName())

            pageTitle.text = getString(wellnessModel.title)

            pageMessage.text = getString(wellnessModel.message)

            notificationTxt.text = getString(wellnessCategory.getReminderMessage())

            showUI()
        }
    }

    private fun showUI() {
        val wellnessCategory = args.wellnessType

        wellnessViewModel.meditationList?.let {
            val list = it.filter { it.wellnessType == wellnessCategory }
            meditationAdapter.submitList(list)
            meditationAdapter.notifyDataSetChanged()
        }
    }

    private fun playMeditation(wellnessCategory: WellnessType, mediaId: Long, mediaUrl: String) {
        val action =
            WellnessCategoryFragmentDirections.actionWellnessCategoryFragmentToMediaPlayerFragment(
                mediaId = mediaId,
                wellnessType = wellnessCategory,
                mediaUrl = mediaUrl
            )
        navController.navigate(action)
    }

    private val itemClickListener = fun(item: MeditationMedia) {
        playMeditation(
            wellnessCategory = item.wellnessType,
            mediaId = item.id,
            mediaUrl = item.resourceUrl
        )
    }
}
