package ai.heart.classickbeats.ui.ppg.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentHrvInfoBottomSheetBinding
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
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

        binding?.apply {
            Glide.with(image1).load(R.drawable.tutorial_animation_1).into(image1)
            Glide.with(image2).load(R.drawable.tutorial_animation_2).into(image2)
            Glide.with(image3).load(R.drawable.tutorial_animation_3).into(image3)
            Glide.with(image4).load(R.drawable.tutorial_animation_4).into(image4)
            Glide.with(image5).load(R.drawable.tutorial_animation_5).into(image5)
        }

        binding?.close?.setSafeOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}