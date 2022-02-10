package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.databinding.FragmentFeedbackDialogBinding
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.utils.hideLoadingBar
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.showLoadingBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedbackDialogFragment : DialogFragment() {

    private var binding: FragmentFeedbackDialogBinding? = null

    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedbackDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.submitButton?.setSafeOnClickListener {
            val feedback = binding?.input?.text?.toString() ?: ""
            profileViewModel.submitFeedback(feedback)
        }

        profileViewModel.showLoading.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                showLoadingBar()
            } else {
                hideLoadingBar()
            }
        })

        profileViewModel.feedbackSubmitted.observe(viewLifecycleOwner) {
            if (it) {
                dismiss()
            }
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
