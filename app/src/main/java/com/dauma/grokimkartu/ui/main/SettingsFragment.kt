package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentSettingsBinding
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.general.pushnotificationsmanager.PushNotificationsSettings
import com.dauma.grokimkartu.general.thememodemanager.ThemeMode
import com.dauma.grokimkartu.general.thememodemanager.ThemeModeManager
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.locale.Language
import com.dauma.grokimkartu.viewmodels.main.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val settingsViewModel by viewModels<SettingsViewModel>()
    @Inject lateinit var utils: Utils
    @Inject lateinit var themeModeManager: ThemeModeManager

    private var _binding: FragmentSettingsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.model = settingsViewModel
        val view = binding.root

        setLanguageRowValue()
        setThemeModeRowValue()

        setupOnClickers()
        setupObservers()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickers() {
        binding.languageRowViewElement.setOnClick {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToLanguagesFragment())
        }
        binding.themeModeRowViewElement.setOnClick {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToUiModesFragment())
        }
        binding.changePasswordRowViewElement.setOnClick {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToPasswordChangeFragment())
        }
        binding.deleteMyAccountRowViewElement.setOnClick {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToDeleteUserFragment())
        }
        binding.arePushNotificationsEnabledRowViewElement.setOnSwitchChecked {
            settingsViewModel.enablePushNotificationsChanged()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    settingsViewModel.navigation.collect {
                        handleNavigation(it)
                    }
                }
                launch {
                    settingsViewModel.uiState.collect {
                        when (it) {
                            is SettingsViewModel.UiState.Main -> {
                                binding.arePushNotificationsEnabledRowViewElement
                                    .setSwitchEnabled(it.pushNotificationSettings != PushNotificationsSettings.DISABLED)
                            }
                            is SettingsViewModel.UiState.LogoutStarted -> {
                                binding.logoutButton.showAnimation(true)
                            }
                            is SettingsViewModel.UiState.LogoutCompleted -> {
                                binding.logoutButton.showAnimation(false)
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun setLanguageRowValue() {
        val language = utils.localeUtils.getCurrentLanguage(requireContext())
        val value = when (language) {
            Language.LT -> getString(R.string.language_lt)
            Language.EN -> getString(R.string.language_en)
        }
        binding.languageRowViewElement.setValue(value)
    }

    private fun setThemeModeRowValue() {
        val themeMode = themeModeManager.currentThemeMode
        val value = when (themeMode) {
            ThemeMode.Light -> getString(R.string.theme_mode_light)
            ThemeMode.Dark -> getString(R.string.theme_mode_dark)
            ThemeMode.Device -> getString(R.string.theme_mode_device)
        }
        binding.themeModeRowViewElement.setValue(value)
    }

    private fun handleNavigation(navigationCommand: NavigationCommand?) {
        when (navigationCommand) {
            is NavigationCommand.ToDirection -> findNavController().navigate(navigationCommand.directions)
            is NavigationCommand.Back -> findNavController().popBackStack()
            is NavigationCommand.CloseApp -> activity?.finish()
            else -> {}
        }
    }
}