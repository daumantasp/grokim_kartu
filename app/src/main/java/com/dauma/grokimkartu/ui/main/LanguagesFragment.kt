package com.dauma.grokimkartu.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentLanguagesBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.locale.Language
import com.dauma.grokimkartu.ui.BottomMenuManager
import com.dauma.grokimkartu.viewmodels.main.LanguagesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguagesFragment : Fragment() {
    private val languagesViewModel by viewModels<LanguagesViewModel>()
    private var bottomMenuManager: BottomMenuManager? = null

    private var _binding: FragmentLanguagesBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bottomMenuManager = context as? BottomMenuManager
    }

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
        languagesViewModel.viewIsReady(requireContext())
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        bottomMenuManager = null
    }

    private fun setupObservers() {
        languagesViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })
        languagesViewModel.language.observe(viewLifecycleOwner, EventObserver {
            selectLanguage(it)
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
            languagesViewModel.languageClicked(requireContext(), Language.LT)
        }
        binding.enLanguageViewElement.setOnClick {
            languagesViewModel.languageClicked(requireContext(), Language.EN)
        }
    }

    private fun selectLanguage(language: Language) {
        when (language) {
            Language.LT -> {
                binding.enLanguageViewElement.isSelected = false
                binding.ltLanguageViewElement.isSelected = true
            }
            Language.EN -> {
                binding.ltLanguageViewElement.isSelected = false
                binding.enLanguageViewElement.isSelected = true
            }
        }
        refreshHeaderTitle()
        refreshMenuItemTitles()
    }

    private fun refreshHeaderTitle() {
        val title = getString(R.string.settings_language)
        binding.languagesHeaderViewElement.setTitle(title)
    }

    private fun refreshMenuItemTitles() {
        bottomMenuManager?.refreshBottomMenuItemTitles()
    }
}