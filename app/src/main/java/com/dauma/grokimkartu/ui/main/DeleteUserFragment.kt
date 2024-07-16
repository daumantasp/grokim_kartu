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
import com.dauma.grokimkartu.databinding.FragmentDeleteUserBinding
import com.dauma.grokimkartu.viewmodels.main.DeleteUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeleteUserFragment : Fragment() {
    private val deleteUserViewModel by viewModels<DeleteUserViewModel>()

    private var _binding: FragmentDeleteUserBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDeleteUserBinding.inflate(inflater, container, false)
        binding.model = deleteUserViewModel
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
        binding.deleteUserHeaderViewElement.setOnBackClick {
            deleteUserViewModel.back()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            deleteUserViewModel.back()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    deleteUserViewModel.uiState.collect {
                        binding.deleteUserButton.showAnimation(it.isDeleteStarted)
                        binding.passwordTextInput.error = getErrorFor(it.passwordError)
                        if (it.isDeleteSuccessful)
                            findNavController().navigate(DeleteUserFragmentDirections.actionDeleteUserFragmentToAuthGraph())
                        else if (it.close)
                            findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun getErrorFor(errorId: Int): String = if (errorId != -1) requireContext().getString(errorId) else ""
}