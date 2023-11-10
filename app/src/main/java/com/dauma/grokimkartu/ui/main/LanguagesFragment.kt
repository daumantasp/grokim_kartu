package com.dauma.grokimkartu.ui.main

import android.content.Context
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
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentLanguagesBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.locale.Language
import com.dauma.grokimkartu.ui.BottomMenuManager
import com.dauma.grokimkartu.viewmodels.main.LanguagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LanguagesFragment : Fragment() {

    private val languagesViewModel by viewModels<LanguagesViewModel>()
    private var bottomMenuManager: BottomMenuManager? = null
    @Inject lateinit var utils: Utils

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
    ): View {
        _binding = FragmentLanguagesBinding.inflate(inflater, container, false)
        binding.model = languagesViewModel
        val view = binding.root
        setupObservers()
        setupOnClickers()
        showCurrentLanguageAsSelected()
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    languagesViewModel.uiState.collect {
                        when (it) {
                            is LanguagesViewModel.UiState.Loaded -> {}
                            is LanguagesViewModel.UiState.LanguageSelected -> {
                                utils.localeUtils.setLanguage(requireContext(), it.language)
                                showCurrentLanguageAsSelected()
                                findNavController().popBackStack()
                            }
                            is LanguagesViewModel.UiState.Canceled -> {
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupOnClickers() {
        binding.ltLanguageViewElement.setOnClick {
            languagesViewModel.languageSelected(Language.LT)
        }
        binding.enLanguageViewElement.setOnClick {
            languagesViewModel.languageSelected(Language.EN)
        }
        binding.languagesHeaderViewElement.setOnBackClick {
            languagesViewModel.back()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            languagesViewModel.back()
        }
    }

    private fun showCurrentLanguageAsSelected() {
        val language = utils.localeUtils.getCurrentLanguage(requireContext())
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
        val title = getString(R.string.settings_language)
        binding.languagesHeaderViewElement.setTitle(title)
        bottomMenuManager?.refreshBottomMenuItemTitles()
    }
}