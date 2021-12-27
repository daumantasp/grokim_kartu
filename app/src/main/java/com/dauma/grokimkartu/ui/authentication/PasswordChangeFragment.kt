package com.dauma.grokimkartu.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentPasswordChangeBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.viewmodels.authentication.PasswordChangeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordChangeFragment : Fragment() {
    private val passwordChangeViewModel by viewModels<PasswordChangeViewModel>()

    private var _binding: FragmentPasswordChangeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPasswordChangeBinding.inflate(inflater, container, false)
        binding.model = passwordChangeViewModel
        val view = binding.root
        setupObservers()

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            passwordChangeViewModel.backClicked()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        passwordChangeViewModel.passwordChangeForm().getFormFields().observe(viewLifecycleOwner) {
            passwordChangeViewModel.passwordChangeClicked(it[0], it[1], it[2])
        }
        passwordChangeViewModel.oldPasswordError.observe(viewLifecycleOwner) {
            binding.oldPasswordTextInput.error =
                if (it != -1) requireContext().getString(it) else ""
        }
        passwordChangeViewModel.newPasswordError.observe(viewLifecycleOwner) {
            binding.newPasswordTextInput.error = if (it != -1) requireContext().getString(it) else ""
        }
        passwordChangeViewModel.repeatPasswordError.observe(viewLifecycleOwner) {
            binding.repeatPasswordTextInput.error = if (it != -1) requireContext().getString(it) else ""
        }
        passwordChangeViewModel.showSuccess.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                binding.oldPasswordTextInput.visibility = View.GONE
                binding.newPasswordTextInput.visibility = View.GONE
                binding.repeatPasswordTextInput.visibility = View.GONE
                binding.changePasswordButton.visibility = View.GONE

                binding.passwordChangedSuccessfullyTextView.visibility = View.VISIBLE
                binding.okButton.visibility = View.VISIBLE
            }
        })
        passwordChangeViewModel.navigateToProfile.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_passwordChangeFragment_to_settingsFragment)
        })
    }
}