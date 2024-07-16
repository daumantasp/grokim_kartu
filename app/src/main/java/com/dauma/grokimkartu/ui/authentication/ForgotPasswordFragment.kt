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
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.databinding.FragmentForgotPasswordBinding
import com.dauma.grokimkartu.viewmodels.authentication.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        binding.model = forgotPasswordViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickers()
        setupObservers()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickers() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            forgotPasswordViewModel.backClicked()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    forgotPasswordViewModel.uiState.collect {
                        binding.forgotPasswordButton.showAnimation(it.isPasswordResetInProgress)
                        if (it.isPasswordResetSuccessful) {
                            binding.forgotPasswordDescriptionTextView.visibility = View.INVISIBLE
                            binding.emailTextInput.visibility = View.INVISIBLE
                            binding.forgotPasswordButton.visibility = View.INVISIBLE
                            binding.forgotPasswordResetSuccessLinearLayout.visibility = View.VISIBLE
                        } else {
                            binding.emailTextInput.error = if (it.emailError != null) requireContext().getString(it.emailError) else ""
                        }
                        if (it.close) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }
}