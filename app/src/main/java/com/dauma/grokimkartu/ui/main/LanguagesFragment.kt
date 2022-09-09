package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.databinding.FragmentLanguagesBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.viewmodels.main.LanguagesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguagesFragment : Fragment() {
    private val languagesViewModel by viewModels<LanguagesViewModel>()

    private var _binding: FragmentLanguagesBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLanguagesBinding.inflate(inflater, container, false)
        binding.model = languagesViewModel
        val view = binding.root
        setupObservers()
        setupBackHandlers()
        setupOnClicks()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun setupObservers() {
        languagesViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })
    }

    private fun setupBackHandlers() {
        binding.languagesHeaderViewElement.setOnBackClick {
            languagesViewModel.backClicked()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            languagesViewModel.backClicked()
        }
    }

    private fun setupOnClicks() {
        binding.ltLanguageViewElement.setOnClick {
            binding.enLanguageViewElement.isSelected = false
            binding.ltLanguageViewElement.isSelected = true
        }
        binding.enLanguageViewElement.setOnClick {
            binding.ltLanguageViewElement.isSelected = false
            binding.enLanguageViewElement.isSelected = true
        }
    }
}