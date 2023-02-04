package com.dauma.grokimkartu.ui.authentication

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentLoginBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.ui.MainActivity
import com.dauma.grokimkartu.ui.StatusBarTheme
import com.dauma.grokimkartu.viewmodels.authentication.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val loginViewModel by viewModels<LoginViewModel>()
    private var notificationsPermissionLauncher: ActivityResultLauncher<String>

    private var _binding: FragmentLoginBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    init {
        notificationsPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
            loginViewModel.enableNotifications(isGranted)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.model = loginViewModel
        val view = binding.root
        setupOnClickListeners()
        setupObservers()

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            loginViewModel.backClicked()
        }
        (requireActivity() as MainActivity).changeStatusBarTheme(StatusBarTheme.LOGIN)

        loginViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        loginViewModel.viewIsDiscarded()
    }

    private fun setupOnClickListeners() {
        binding.registerTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
        binding.forgotPasswordTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    private fun setupObservers() {
        // https://stackoverflow.com/questions/50740757/how-to-use-android-navigation-without-binding-to-ui-in-viewmodel-mvvm
        // https://stackoverflow.com/questions/60622645/navigate-from-one-fragment-to-another-when-using-mvvm-pattern-for-android
        loginViewModel.navigation.observe(viewLifecycleOwner, EventObserver {
            handleNavigation(it)
        })
        loginViewModel.getLoginForm().getFormFields().observe(viewLifecycleOwner) {
            this.loginViewModel.loginUser(it.get(0), it.get(1))
        }
        loginViewModel.askForNotificationsPermissionIfAllowed.observe(viewLifecycleOwner, EventObserver {
            askForNotificationsPermissionIfAllowedElseEnableNotifications()
        })
        loginViewModel.loginInProgress.observe(viewLifecycleOwner) {
            this.binding.loginButton.showAnimation(it)
        }
        loginViewModel.emailError.observe(viewLifecycleOwner, Observer {
            this.binding.emailTextInput.error = if (it != -1) requireContext().getString(it) else ""
        })
        loginViewModel.passwordError.observe(viewLifecycleOwner, Observer {
            this.binding.passwordTextInput.error = if (it != -1) requireContext().getString(it) else ""
        })
    }

    private fun askForNotificationsPermissionIfAllowedElseEnableNotifications() {
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            val permission = POST_NOTIFICATIONS
            val isGranted = ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
            if (!isGranted && !shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                notificationsPermissionLauncher.launch(permission)
            }
        } else {
            loginViewModel.enableNotifications(true)
        }
    }

    private fun handleNavigation(navigationCommand: NavigationCommand) {
        when (navigationCommand) {
            is NavigationCommand.ToDirection -> findNavController().navigate(navigationCommand.directions)
            is NavigationCommand.Back -> findNavController().popBackStack()
            is NavigationCommand.CloseApp -> activity?.finish()
        }
    }
}