package ai.heart.classickbeats.ui.onboarding

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentOnboardingBinding
import ai.heart.classickbeats.model.OnBoardingModel
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.utils.setDarkStatusBar
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

class OnBoardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val PAGE_COUNT = 4

    private var binding: FragmentOnboardingBinding? = null

    private val sliderData = mutableListOf<OnBoardingModel>()

    private val onBoardingViewModel: OnBoardingViewModel by activityViewModels()

    private var shouldAutoScroll = true

    private var autoScrollCancelled = false

    private lateinit var timer: Timer

    private val onPageChangeCallback by lazy {
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    shouldAutoScroll = false
                }
                val currentPosition = binding?.illustrationVp?.currentItem
                if (currentPosition == PAGE_COUNT - 1) {
                    autoScrollCancelled = true
                    binding?.skip?.visibility = View.INVISIBLE
                    binding?.nextBtn?.text = getString(R.string.get_started)
                    timer.cancel()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sliderData.add(
            OnBoardingModel(
                R.drawable.ic_heart_with_beat,
                R.drawable.bg_gradient_rect_blue,
                getString(R.string.onboarding_message_1)
            )
        )
        sliderData.add(
            OnBoardingModel(
                R.drawable.ic_plus_bars,
                R.drawable.bg_gradient_rect_green,
                getString(R.string.onboarding_message_2)
            )
        )
        sliderData.add(
            OnBoardingModel(
                R.drawable.ic_three_bars,
                R.drawable.bg_gradient_rect_pink,
                getString(R.string.onboarding_message_3)
            )
        )
        sliderData.add(
            OnBoardingModel(
                R.drawable.ic_flower_design,
                R.drawable.bg_gradient_rect_orange,
                getString(R.string.onboarding_message_4)
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOnboardingBinding.bind(view)

        setDarkStatusBar()

        binding?.apply {
            illustrationVp.adapter = OnBoardingSlidingPagerAdapter(requireContext(), sliderData)
            illustrationVp.registerOnPageChangeCallback(onPageChangeCallback)

            TabLayoutMediator(sliderIndicator, illustrationVp) { _, _ ->
            }.attach()

            skip.setSafeOnClickListener {
                onBoardingViewModel.getStartedClick()
            }

            nextBtn.setOnClickListener {
                illustrationVp.apply {
                    val nextPosition = (currentItem + 1) % PAGE_COUNT
                    if (nextPosition == 0 || autoScrollCancelled) {
                        onBoardingViewModel.getStartedClick()
                    } else {
                        post {
                            setCurrentItem(nextPosition, true)
                        }
                    }
                }
            }
        }

        onBoardingViewModel.navigateToSignUpFragment.observe(viewLifecycleOwner, EventObserver {
            navigateToSignUpFragment()
        })
    }

    override fun onResume() {
        super.onResume()

        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                binding?.illustrationVp?.apply {
                    if (!shouldAutoScroll) {
                        shouldAutoScroll = true
                        return
                    }
                    val nextPosition = (currentItem + 1) % PAGE_COUNT
                    post {
                        setCurrentItem(nextPosition, true)
                    }
                }
            }
        }, 3000L, 3000L)
    }

    private fun navigateToSignUpFragment() {
        val action = OnBoardingFragmentDirections.actionOnBoardingFragmentToNavLogin()
        findNavController().navigate(action)
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    override fun onDestroyView() {
        timer.cancel()
        binding?.illustrationVp?.unregisterOnPageChangeCallback(onPageChangeCallback)
        binding = null
        super.onDestroyView()
    }
}