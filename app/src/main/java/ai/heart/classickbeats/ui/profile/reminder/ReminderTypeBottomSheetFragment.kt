package ai.heart.classickbeats.ui.profile.reminder

import ai.heart.classickbeats.BottomSheetStringAdapter
import ai.heart.classickbeats.databinding.FragmentReminderTypeBottomSheetBinding
import ai.heart.classickbeats.model.Reminder
import ai.heart.classickbeats.utils.toName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderTypeBottomSheetFragment : BottomSheetDialogFragment() {

    private var binding: FragmentReminderTypeBottomSheetBinding? = null

    private val reminderViewModel by activityViewModels<ReminderViewModel>()

    private val reminderList = Reminder.Type.values().toList()

    private val reminderListStr: List<String> by lazy { reminderList.map { it.toName(requireContext()) } }

    private val reminderSelectorFun = fun(index: Int) {
        val reminderType = reminderList[index]
        reminderViewModel.updateReminderType(reminderType)
        dismiss()
    }

    private val bottomSheetStringAdapter =
        BottomSheetStringAdapter(itemClickListener = reminderSelectorFun)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReminderTypeBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.bottomSheetListItem?.adapter = bottomSheetStringAdapter
        bottomSheetStringAdapter.submitList(reminderListStr)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
