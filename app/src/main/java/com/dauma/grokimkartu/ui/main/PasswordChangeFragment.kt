package com.dauma.grokimkartu.ui.main

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
import com.dauma.grokimkartu.databinding.FragmentPasswordChangeBinding
import com.dauma.grokimkartu.viewmodels.main.PasswordChangeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
    ): View {
        _binding = FragmentPasswordChangeBinding.inflate(inflater, container, false)
        binding.model = passwordChangeViewModel
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
        binding.changePasswordHeaderViewElement.setOnBackClick {
            passwordChangeViewModel.back()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            passwordChangeViewModel.back()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    passwordChangeViewModel.uiState.collect {
                        binding.changePasswordButton.showAnimation(it.isPasswordChangeStarted)
                        binding.oldPasswordTextInput.error = getErrorFor(it.oldPasswordError)
                        binding.newPasswordTextInput.error = getErrorFor(it.newPasswordError)
                        binding.repeatPasswordTextInput.error = getErrorFor(it.repeatPasswordError)
                        if (it.isPasswordChangeSuccessful) {
                            binding.inputsAndButtonLinearLayout.visibility = View.GONE
                            binding.passwordChangeSuccessfulLinearLayout.visibility = View.VISIBLE
                        } else if (it.close) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun getErrorFor(errorId: Int): String = if (errorId != -1) requireContext().getString(errorId) else ""
}