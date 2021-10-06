package ai.heart.classickbeats.ui.ppg.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanTutorialBinding
import ai.heart.classickbeats.model.ScanTutorialModel
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.ppg.ScanSlidingPagerAdapter
import ai.heart.classickbeats.ui.ppg.viewmodel.ScanViewModel
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagingApi
@AndroidEntryPoint
class ScanTutorialFragment : Fragment(R.layout.fragment_scan_tutorial) {

    private val PAGE_COUNT = 5

    private var binding: FragmentScanTutorialBinding? = null

    private val sliderData = mutableListOf<ScanTutorialModel>()

    private val scanViewModel: ScanViewModel by activityViewModels()

    private val onPageChangeCallback by lazy {
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                val currentPosition = binding?.illustrationVp?.currentItem
                if (currentPosition == PAGE_COUNT - 1) {
                    binding?.nextBtn?.text = getString(R.string.start_scan)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sliderData.add(
            ScanTutorialModel(
                R.drawable.tutorial_animation_1,
                getString(R.string.scan_tutorial_1)
            )
        )
        sliderData.add(
            ScanTutorialModel(
                R.drawable.tutorial_animation_2,
                getString(R.string.scan_tutorial_2)
            )
        )
        sliderData.add(
            ScanTutorialModel(
                R.drawable.tutorial_animation_3,
                getString(R.string.scan_tutorial_3)
            )
        )
        sliderData.add(
            ScanTutorialModel(
                R.drawable.tutorial_animation_4,
                getString(R.string.scan_tutorial_4)
            )
        )
        sliderData.add(
            ScanTutorialModel(
                R.drawable.tutorial_animation_5,
                getString(R.string.scan_tutorial_5)
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentScanTutorialBinding.bind(view)

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireActivity(), R.color.very_dark_blue)

        binding?.apply {
            illustrationVp.adapter = ScanSlidingPagerAdapter(sliderData)
            illustrationVp.registerOnPageChangeCallback(onPageChangeCallback)

            TabLayoutMediator(sliderIndicator, illustrationVp) { _, _ ->
            }.attach()

            nextBtn.setOnClickListener {
                illustrationVp.apply {
                    val nextPosition = (currentItem + 1) % 5
                    if (nextPosition == 0) {
                        scanViewModel.getStartedClick()
                    } else {
                        post {
                            setCurrentItem(nextPosition, true)
                        }
                    }
                }
            }
        }

        scanViewModel.navigateToScanFragment.observe(viewLifecycleOwner, EventObserver {
            navigateToScanFragment()
        })
    }

    private fun navigateToScanFragment() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        binding?.illustrationVp?.unregisterOnPageChangeCallback(onPageChangeCallback)
        binding = null
        super.onDestroyView()
    }
}
