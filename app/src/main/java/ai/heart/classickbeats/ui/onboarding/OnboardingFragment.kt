package ai.heart.classickbeats.ui.onboarding

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentOnboardingBinding
import ai.heart.classickbeats.model.OnboardingModel
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.utils.setLightStatusBar
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val PAGE_COUNT = 4

    private var binding: FragmentOnboardingBinding? = null

    private val onboardingViewModel: OnboardingViewModel by viewModels()

    private val onboardModelList: MutableList<OnboardingModel> = ArrayList()

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
                val currentPosition = binding?.viewPager?.currentItem ?: 0
                val item = onboardModelList[currentPosition]
                binding?.apply {
                    actionText.setText(item.actionResId)
                    descriptionText.setText(item.descriptionResId)
                    circularProgress.progress = (currentPosition + 1) * 25
                    if (currentPosition == PAGE_COUNT - 1) {
                        autoScrollCancelled = true
                        skip.visibility = View.INVISIBLE
                        nextPageBtn.visibility = View.INVISIBLE
                        circularProgress.visibility = View.INVISIBLE
                        bottomLayout.visibility = View.VISIBLE
                        timer.cancel()
                    } else {
                        skip.visibility = View.VISIBLE
                        nextPageBtn.visibility = View.VISIBLE
                        circularProgress.visibility = View.VISIBLE
                        bottomLayout.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        populateOnboardModelList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOnboardingBinding.bind(view)

        setLightStatusBar()

        binding?.apply {
            viewPager.adapter = OnboardingSlidingPagerAdapter(requireContext(), onboardModelList)
            viewPager.registerOnPageChangeCallback(onPageChangeCallback)

            TabLayoutMediator(pageIndicator, viewPager) { _, _ ->
            }.attach()

            skip.setOnClickListener {
                onboardingViewModel.getStartedClick()
            }

            nextPageBtn.setOnClickListener {
                viewPager.apply {
                    val nextPosition = (currentItem + 1) % PAGE_COUNT
                    post {
                        setCurrentItem(nextPosition, true)
                    }
                }
            }

            bottomLayout.setOnClickListener {
                onboardingViewModel.getStartedClick()
            }
        }

        onboardingViewModel.navigateToSignUpFragment.observe(viewLifecycleOwner, EventObserver {
            navigateToSignUpFragment()
        })
    }

    override fun onResume() {
        super.onResume()

        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                binding?.viewPager?.apply {
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

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    override fun onDestroyView() {
        timer.cancel()
        binding?.viewPager?.unregisterOnPageChangeCallback(onPageChangeCallback)
        binding = null
        super.onDestroyView()
    }

    private fun populateOnboardModelList() {
        onboardModelList.add(
            OnboardingModel(
                R.drawable.scan_puls,
                R.string.onboarding_action_1,
                R.string.onboarding_message_1
            )
        )
        onboardModelList.add(
            OnboardingModel(
                R.drawable.daily_health_vitals,
                R.string.onboarding_action_2,
                R.string.onboarding_message_2
            )
        )
        onboardModelList.add(
            OnboardingModel(
                R.drawable.analysis,
                R.string.onboarding_action_3,
                R.string.onboarding_message_3
            )
        )
        onboardModelList.add(
            OnboardingModel(
                R.drawable.meditate,
                R.string.onboarding_action_4,
                R.string.onboarding_message_4
            )
        )
    }

    private fun navigateToSignUpFragment() {
        val action = OnboardingFragmentDirections.actionOnboardingFragmentToNavLogin()
        findNavController().navigate(action)
    }
}
