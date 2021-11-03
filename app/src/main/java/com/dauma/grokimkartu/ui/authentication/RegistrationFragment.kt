package com.dauma.grokimkartu.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentRegistrationBinding
import com.dauma.grokimkartu.general.EventObserver
import com.dauma.grokimkartu.viewmodels.authentication.RegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : Fragment() {
    private val registrationViewModel by viewModels<RegistrationViewModel>()

    private var _binding: FragmentRegistrationBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        binding.model = registrationViewModel
        val view = binding.root
        setupObservers()

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            registrationViewModel.backClicked()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setupObservers() {
        registrationViewModel.getRegistrationForm().getFormFields().observe(viewLifecycleOwner) {
            registrationViewModel.createUser(it.get(0), it.get(1), it.get(2))
        }
        registrationViewModel.emailVerificationSent.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                binding.nameDescriptionTextView.visibility = View.GONE
                binding.nameTextInput.visibility = View.GONE
                binding.emailTextInput.visibility = View.GONE
                binding.passwordDescriptionTextView.visibility = View.GONE
                binding.passwordTextInput.visibility = View.GONE
                binding.registerButton.visibility = View.GONE

                binding.registrationSuccessfulTextView.visibility = View.VISIBLE
                binding.resendButton.visibility = View.VISIBLE
                binding.okButton.visibility = View.VISIBLE
            }
        })
        registrationViewModel.emailError.observe(viewLifecycleOwner, Observer {
            binding.emailTextInput.error = if (it != -1) requireContext().getString(it) else ""
        })
        registrationViewModel.passwordError.observe(viewLifecycleOwner, Observer {
            binding.passwordTextInput.error = if (it != -1) requireContext().getString(it) else ""
        })
        registrationViewModel.navigateToLogin.observe(viewLifecycleOwner, {
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        })
        registrationViewModel.enableResendButton.observe(viewLifecycleOwner, {
            binding.resendButton.isEnabled = it
        })
        registrationViewModel.verificationEmailWillBeAllowedToSentInSeconds.observe(viewLifecycleOwner, {
            val resendButtonTitle: String
            if (it > 0) {
                resendButtonTitle = "${requireContext().getString(R.string.registration_resendButton)} (${it}s.)"
            } else {
                resendButtonTitle = requireContext().getString(R.string.registration_resendButton)
            }
            binding.resendButton.text = resendButtonTitle
        })
    }
}