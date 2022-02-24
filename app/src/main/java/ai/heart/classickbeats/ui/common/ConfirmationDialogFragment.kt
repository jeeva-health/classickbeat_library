package ai.heart.classickbeats.ui.common

import ai.heart.classickbeats.databinding.FragmentConfirmationDialogBinding
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmationDialogFragment : DialogFragment() {

    private var binding: FragmentConfirmationDialogBinding? = null

    private val viewModel: ConfirmationViewModel by activityViewModels()

    private val args: ConfirmationDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfirmationDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleStr = args.title
        val messageStr = args.message
        val positiveKeyStr = args.positiveKey
        val negativeKeyStr = args.negativeKey
        val isCancellable = args.cancellable

        binding?.apply {

            heading.text = titleStr
            if (messageStr == null || messageStr.isBlank()) {
                message.visibility = View.GONE
            } else {
                message.text = messageStr
            }
            positiveButton.text = positiveKeyStr
            negativeButton.text = negativeKeyStr

            negativeButton.setSafeOnClickListener {
                viewModel.onNegative()
            }

            positiveButton.setSafeOnClickListener {
                viewModel.onPositive()
            }
        }

        viewModel.dismissEvent.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                dismiss()
            }
        })
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
