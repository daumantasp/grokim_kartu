package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentSettingsBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.viewmodels.main.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private val settingsViewModel by viewModels<SettingsViewModel>()

    private var _binding: FragmentSettingsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.model = settingsViewModel
        val view = binding.root
        setupObservers()
        if (savedInstanceState == null) {
            // TODO: Still reloads on device rotate, probably need to save state instance
            settingsViewModel.loadSettings()
        }
        settingsViewModel.viewIsReady()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        settingsViewModel.viewIsDiscarded()
    }

    private fun setupObservers() {
        settingsViewModel.navigateToLogin.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(R.id.action_settingsFragment_to_authGraph)
        })
        settingsViewModel.navigateToDeleteUser.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(R.id.action_settingsFragment_to_deleteUserFragment)
        })
        settingsViewModel.navigateToPasswordChange.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(R.id.action_settingsFragment_to_passwordChangeFragment)
        })
    }
}