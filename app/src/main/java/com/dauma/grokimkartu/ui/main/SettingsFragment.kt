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
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.general.pushnotificationsmanager.PushNotificationsSettings
import com.dauma.grokimkartu.general.thememodemanager.ThemeMode
import com.dauma.grokimkartu.general.utils.locale.Language
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
        setupOnClickers()
        setupObservers()
        if (savedInstanceState == null) {
            // TODO: Still reloads on device rotate, probably need to save state instance
            settingsViewModel.loadSettings()
        }
        binding.arePushNotificationsEnabledRowViewElement.setOnSwitchChecked {
            this.settingsViewModel.enablePushNotificationsChanged()
        }
        settingsViewModel.viewIsReady(requireContext())

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        settingsViewModel.viewIsDiscarded()
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
    }

    private fun setupObservers() {
        settingsViewModel.navigation.observe(viewLifecycleOwner, EventObserver {
            handleNavigation(it)
        })
        settingsViewModel.language.observe(viewLifecycleOwner, EventObserver {
            updateLanguageRowValue(it)
        })
        settingsViewModel.themeMode.observe(viewLifecycleOwner, EventObserver {
            updateThemeModeRowValue(it)
        })
        settingsViewModel.pushNotificationsSettingsEnabled.observe(viewLifecycleOwner, EventObserver {
            binding.arePushNotificationsEnabledRowViewElement.setSwitchEnabled(it != PushNotificationsSettings.DISABLED)
        })
    }

    private fun updateLanguageRowValue(language: Language) {
        val value = when (language) {
            Language.LT -> getString(R.string.language_lt)
            Language.EN -> getString(R.string.language_en)
        }
        binding.languageRowViewElement.setValue(value)
    }

    private fun updateThemeModeRowValue(themeMode: ThemeMode) {
        binding.themeModeRowViewElement.setValue(when (themeMode) {
            ThemeMode.Light -> getString(R.string.theme_mode_light)
            ThemeMode.Dark -> getString(R.string.theme_mode_dark)
            ThemeMode.Device -> getString(R.string.theme_mode_device)
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