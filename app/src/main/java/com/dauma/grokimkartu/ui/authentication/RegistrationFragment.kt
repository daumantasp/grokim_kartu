package com.dauma.grokimkartu.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dauma.grokimkartu.databinding.FragmentRegistrationBinding
import com.dauma.grokimkartu.viewmodels.authentication.RegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        setupOnClickers()
        setupObservers()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickers() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            registrationViewModel.backClicked()
        }
    }

    fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    registrationViewModel.uiState.collect {
                        binding.registerButton.showAnimation(it.isRegistrationStarted)
                        if (it.isRegistrationSuccessful) {
                            binding.inputsLinearLayout.visibility = View.INVISIBLE
                            binding.registerButton.visibility = View.INVISIBLE
                            binding.registrationSuccessfulTextView.visibility = View.VISIBLE
                            binding.registrationSuccessLinearLayout.visibility = View.VISIBLE
                        } else {
                            binding.emailTextInput.error = if (it.emailError != null) requireContext().getString(it.emailError) else ""
                            binding.passwordTextInput.error = if (it.passwordError != null) requireContext().getString(it.passwordError) else ""
                        }
                    }
                }
            }
        }
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