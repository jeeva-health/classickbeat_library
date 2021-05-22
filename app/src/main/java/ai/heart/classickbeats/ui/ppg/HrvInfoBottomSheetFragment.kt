package ai.heart.classickbeats.ui.ppg

import ai.heart.classickbeats.databinding.FragmentHrvInfoBottomSheetBinding
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class HrvInfoBottomSheetFragment : BottomSheetDialogFragment() {

    private var binding: FragmentHrvInfoBottomSheetBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHrvInfoBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.close?.setSafeOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}