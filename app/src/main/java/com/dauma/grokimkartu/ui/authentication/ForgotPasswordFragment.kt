package com.dauma.grokimkartu.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.databinding.FragmentForgotPasswordBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.viewmodels.authentication.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private val forgotPasswordViewModel by viewModels<ForgotPasswordViewModel>()

    private var _binding: FragmentForgotPasswordBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        binding.model = forgotPasswordViewModel
        val view = binding.root
        setupObservers()

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            forgotPasswordViewModel.backClicked()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        forgotPasswordViewModel.getForgotPasswordForm().getFormFields().observe(viewLifecycleOwner) {
            forgotPasswordViewModel.resetClicked(it.get(0))
        }
        forgotPasswordViewModel.passwordResetInProgress.observe(viewLifecycleOwner, {
            binding.forgotPasswordButton.showAnimation(it)
        })
        forgotPasswordViewModel.emailError.observe(viewLifecycleOwner) {
            binding.emailTextInput.error = if (it != -1) requireContext().getString(it) else ""
        }
        forgotPasswordViewModel.showSuccess.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                binding.forgotPasswordDescriptionTextView.visibility = View.INVISIBLE
                binding.emailTextInput.visibility = View.INVISIBLE
                binding.forgotPasswordButton.visibility = View.INVISIBLE
                binding.forgotPasswordResetSuccessLinearLayout.visibility = View.VISIBLE
            }
        })
        forgotPasswordViewModel.navigation.observe(viewLifecycleOwner, EventObserver {
            handleNavigation(it)
        })
    }

    private fun handleNavigation(navigationCommand: NavigationCommand) {
        when (navigationCommand) {
            is NavigationCommand.ToDirection -> findNavController().navigate(navigationCommand.directions)
            is NavigationCommand.Back -> findNavController().popBackStack()
            is NavigationCommand.CloseApp -> activity?.finish()
        }
    }
}