package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.databinding.FragmentDeleteUserBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.viewmodels.main.DeleteUserViewModel
import dagger.hilt.android.AndroidEntryPoint

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
    ): View? {
        _binding = FragmentDeleteUserBinding.inflate(inflater, container, false)
        binding.model = deleteUserViewModel
        val view = binding.root
        setupObservers()

        binding.deleteUserHeaderViewElement.setOnBackClick {
            deleteUserViewModel.backClicked()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            deleteUserViewModel.backClicked()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        deleteUserViewModel.navigation.observe(viewLifecycleOwner, EventObserver {
            handleNavigation(it)
        })
        deleteUserViewModel.passwordError.observe(viewLifecycleOwner) {
            binding.passwordTextInput.error = if (it != -1) requireContext().getString(it) else ""
        }
        deleteUserViewModel.deleteInProgress.observe(viewLifecycleOwner, {
            binding.deleteUserButton.showAnimation(it)
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