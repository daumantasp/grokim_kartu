package com.dauma.grokimkartu.ui.forgotPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.databinding.FragmentForgotPasswordBinding
import com.dauma.grokimkartu.models.Event
import com.dauma.grokimkartu.models.EventObserver
import com.dauma.grokimkartu.viewmodels.forgotPassword.ForgotPasswordViewModelImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private val forgotPasswordViewModel by viewModels<ForgotPasswordViewModelImpl>()

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

        forgotPasswordViewModel.getForgotPasswordForm().getFormFields().observe(viewLifecycleOwner) {
            forgotPasswordViewModel.resetClicked(it.get(0))
        }

        forgotPasswordViewModel.emailError.observe(viewLifecycleOwner) {
            binding.emailTextInput.error = if (it != -1) requireContext().getString(it) else ""
        }

        forgotPasswordViewModel.showSuccess.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                binding.emailTextInput.visibility = View.GONE
                binding.forgotPasswordButton.visibility = View.GONE
                binding.passwordResetEmailSentTextView.visibility = View.VISIBLE
                binding.okButton.visibility = View.VISIBLE
            }
        })

        forgotPasswordViewModel.navigateToLogin.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(it)
        })

        // Inflate the layout for this fragment
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}