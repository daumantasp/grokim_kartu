package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.databinding.FragmentUiModesBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.viewmodels.main.UiModesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UiModesFragment : Fragment() {
    private val uiModesViewModel by viewModels<UiModesViewModel>()

    private var _binding: FragmentUiModesBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUiModesBinding.inflate(inflater, container, false)
        binding.model = uiModesViewModel
        val view = binding.root
        setupObservers()
        setupBackHandlers()
        setupOnClicks()
        uiModesViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        uiModesViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })
    }

    private fun setupBackHandlers() {
        binding.uiModesHeaderViewElement.setOnBackClick {
            uiModesViewModel.backClicked()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            uiModesViewModel.backClicked()
        }
    }

    private fun setupOnClicks() {
    }
}