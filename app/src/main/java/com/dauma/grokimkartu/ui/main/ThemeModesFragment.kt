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
import com.dauma.grokimkartu.databinding.FragmentThemeModesBinding
import com.dauma.grokimkartu.general.thememodemanager.ThemeMode
import com.dauma.grokimkartu.general.thememodemanager.ThemeModeManager
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.StatusBarManager
import com.dauma.grokimkartu.ui.StatusBarTheme
import com.dauma.grokimkartu.viewmodels.main.ThemeModesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ThemeModesFragment : Fragment() {

    private val themeModesViewModel by viewModels<ThemeModesViewModel>()
    private var statusBarManager: StatusBarManager? = null
    @Inject lateinit var utils: Utils
    @Inject lateinit var themeModeManager: ThemeModeManager

    private var _binding: FragmentThemeModesBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThemeModesBinding.inflate(inflater, container, false)
        binding.model = themeModesViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupOnClickers()
        showAvailableThemeModes()
        showCurrentThemeModeAsSelected()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        statusBarManager = context as? StatusBarManager
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        statusBarManager = null
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    themeModesViewModel.uiState.collect {
                        it.selectedThemeMode?.let { themeMode ->
                            themeModeManager.selectThemeMode(themeMode)
                            showCurrentThemeModeAsSelected()

                            // NOTE: Theme change requires to recreate activity
                            // which takes some time. Navigation does not look
                            // good if it occurs during theme change
                            lifecycleScope.launch {
                                delay(300)
                                findNavController().popBackStack()
                            }
                        }
                        if (it.isCanceled) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun setupOnClickers() {
        binding.lightModeRowViewElement.setOnClick {
            themeModesViewModel.themeModeSelected(ThemeMode.Light)
        }
        binding.darkModeRowViewElement.setOnClick {
            themeModesViewModel.themeModeSelected(ThemeMode.Dark)
        }
        binding.deviceModeRowViewElement.setOnClick {
            themeModesViewModel.themeModeSelected(ThemeMode.Device)
        }
        binding.themeModesHeaderViewElement.setOnBackClick {
            themeModesViewModel.back()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            themeModesViewModel.back()
        }
    }

    private fun showAvailableThemeModes() {
        val availableThemeModes = themeModeManager.availableThemeModes
        if (availableThemeModes.contains(ThemeMode.Light)) {
            binding.lightModeRowViewElement.visibility = View.VISIBLE
        }
        if (availableThemeModes.contains(ThemeMode.Dark)) {
            binding.darkModeRowViewElement.visibility = View.VISIBLE
        }
        if (availableThemeModes.contains(ThemeMode.Device)) {
            binding.deviceModeRowViewElement.visibility = View.VISIBLE
        }
    }

    private fun showCurrentThemeModeAsSelected() {
        val currentThemeMode = themeModeManager.currentThemeMode
        binding.lightModeRowViewElement.showIcon(currentThemeMode == ThemeMode.Light)
        binding.darkModeRowViewElement.showIcon(currentThemeMode == ThemeMode.Dark)
        binding.deviceModeRowViewElement.showIcon(currentThemeMode == ThemeMode.Device)
        statusBarManager?.changeStatusBarTheme(StatusBarTheme.MAIN)
    }
}