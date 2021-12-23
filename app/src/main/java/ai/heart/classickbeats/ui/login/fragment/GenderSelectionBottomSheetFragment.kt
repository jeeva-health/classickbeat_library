package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.BottomSheetStringAdapter
import ai.heart.classickbeats.databinding.FragmentGenderSelectionBottomSheetBinding
import ai.heart.classickbeats.model.Gender
import ai.heart.classickbeats.ui.login.LoginViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GenderSelectionBottomSheetFragment : BottomSheetDialogFragment() {

    private var binding: FragmentGenderSelectionBottomSheetBinding? = null

    private val logInViewModel by activityViewModels<LoginViewModel>()

    private var selectedGender: Gender? = null

    private val genderSelectorFun = fun(index: Int) {
        selectedGender = logInViewModel.genderList[index]
        dismiss()
    }

    private val bottomSheetStringAdapter =
        BottomSheetStringAdapter(itemClickListener = genderSelectorFun)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGenderSelectionBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.bottomSheetListItem?.adapter = bottomSheetStringAdapter
        bottomSheetStringAdapter.submitList(logInViewModel.genderListStr)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
