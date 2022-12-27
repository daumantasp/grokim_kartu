package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.databinding.FragmentThemeModesBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.thememodemanager.ThemeMode
import com.dauma.grokimkartu.viewmodels.main.ThemeModesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThemeModesFragment : Fragment() {
    private val themeModesViewModel by viewModels<ThemeModesViewModel>()

    private var _binding: FragmentThemeModesBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThemeModesBinding.inflate(inflater, container, false)
        binding.model = themeModesViewModel
        val view = binding.root
        setupObservers()
        setupBackHandlers()
        setupOnClicks()
        themeModesViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        themeModesViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })
        themeModesViewModel.availableThemeModes.observe(viewLifecycleOwner, EventObserver {
            if (it.contains(ThemeMode.Light)) {
                binding.lightModeRowViewElement.visibility = View.VISIBLE
            }
            if (it.contains(ThemeMode.Dark)) {
                binding.darkModeRowViewElement.visibility = View.VISIBLE
            }
            if (it.contains(ThemeMode.Device)) {
                binding.deviceModeRowViewElement.visibility = View.VISIBLE
            }
        })
    }

    private fun setupBackHandlers() {
        binding.themeModesHeaderViewElement.setOnBackClick {
            themeModesViewModel.backClicked()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            themeModesViewModel.backClicked()
        }
    }

    private fun setupOnClicks() {
    }
}