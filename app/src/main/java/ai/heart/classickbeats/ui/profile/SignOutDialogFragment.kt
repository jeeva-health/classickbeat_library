package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.MainActivity
import ai.heart.classickbeats.databinding.FragmentSignOutDialogBinding
import ai.heart.classickbeats.ui.login.LoginViewModel
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels


class SignOutDialogFragment : DialogFragment() {

    private var binding: FragmentSignOutDialogBinding? = null

    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignOutDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.cancelButton?.setSafeOnClickListener {
            dismiss()
        }

        binding?.signOutButton?.setSafeOnClickListener {
            loginViewModel.logoutUser()
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}