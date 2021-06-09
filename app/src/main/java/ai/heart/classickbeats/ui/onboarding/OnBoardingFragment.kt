package ai.heart.classickbeats.ui.onboarding

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentOnboardingBinding
import ai.heart.classickbeats.model.OnBoardingModel
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.utils.postOnMainLooper
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

class OnBoardingFragment : Fragment(R.layout.fragment_onboarding) {

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
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sliderData.add(
            OnBoardingModel(
                R.drawable.ic_heart_with_beat,
                R.drawable.ic_gradient_rect_blue,
                "Scan your pulse with your phone camera."
            )
        )
        sliderData.add(
            OnBoardingModel(
                R.drawable.ic_plus_bars,
                R.drawable.ic_gradient_rect_green,
                "Track and log your daily health vitals"
            )
        )
        sliderData.add(
            OnBoardingModel(
                R.drawable.ic_three_bars,
                R.drawable.ic_gradient_rect_pink,
                "Get a detailed analysis of your stress levels"
            )
        )
        sliderData.add(
            OnBoardingModel(
                R.drawable.ic_flower_design,
                R.drawable.ic_gradient_rect_orange,
                "Meditate and stay calm with curated sounds"
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOnboardingBinding.bind(view)

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireActivity(), R.color.very_dark_blue)

        binding?.apply {
            illustrationVp.adapter = SlidingPagerAdapter(requireContext(), sliderData)
            illustrationVp.registerOnPageChangeCallback(onPageChangeCallback)

            TabLayoutMediator(sliderIndicator, illustrationVp) { _, _ ->
            }.attach()

            skip.setSafeOnClickListener {
                onBoardingViewModel.getStartedClick()
            }

            nextBtn.setOnClickListener {
                illustrationVp.apply {
                    val nextPosition = (currentItem + 1) % 4
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
                    val nextPosition = (currentItem + 1) % 4
                    post {
                        setCurrentItem(nextPosition, true)
                    }
                    if (nextPosition == 3) {
                        postOnMainLooper {
                            autoScrollCancelled = true
                            binding?.skip?.visibility = View.INVISIBLE
                            binding?.nextBtn?.text = "Get Started"
                        }
                        timer.cancel()
                        return
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