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
import com.dauma.grokimkartu.general.event.EventObserver
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
                binding.inputsLinearLayout.visibility = View.INVISIBLE
                binding.registerButton.visibility = View.INVISIBLE
                binding.registrationSuccessfulTextView.visibility = View.VISIBLE
                binding.registrationSuccessLinearLayout.visibility = View.VISIBLE
            }
        })
        registrationViewModel.registrationInProgress.observe(viewLifecycleOwner) {
            this.binding.registerButton.showAnimation(it)
        }
        registrationViewModel.emailError.observe(viewLifecycleOwner, Observer {
            binding.emailTextInput.error = if (it != -1) requireContext().getString(it) else ""
        })
        registrationViewModel.passwordError.observe(viewLifecycleOwner, Observer {
            binding.passwordTextInput.error = if (it != -1) requireContext().getString(it) else ""
        })
        registrationViewModel.navigateBack.observe(viewLifecycleOwner, {
            findNavController().popBackStack()
        })
//        registrationViewModel.enableResendButton.observe(viewLifecycleOwner, {
//            binding.resendTextView.isEnabled = it
//        })
//        registrationViewModel.verificationEmailWillBeAllowedToSentInSeconds.observe(viewLifecycleOwner, {
//            val resendButtonTitle: String
//            if (it > 0) {
//                resendButtonTitle = "${requireContext().getString(R.string.registration_resendButton)} (${it}s.)"
//            } else {
//                resendButtonTitle = requireContext().getString(R.string.registration_resendButton)
//            }
//            binding.resendTextView.text = resendButtonTitle
//        })
    }
}