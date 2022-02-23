package com.dauma.grokimkartu.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentLoginBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.ui.MainActivity
import com.dauma.grokimkartu.ui.StatusBarTheme
import com.dauma.grokimkartu.viewmodels.authentication.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val loginViewModel by viewModels<LoginViewModel>()

    private var _binding: FragmentLoginBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickListeners() {
        binding.registerTextView.setOnClickListener {
            loginViewModel.registrationClicked()
        }
        binding.forgotPasswordTextView.setOnClickListener {
            loginViewModel.forgotPasswordClicked()
        }
    }

    private fun setupObservers() {
        loginViewModel.getLoginForm().getFormFields().observe(viewLifecycleOwner) {
            this.loginViewModel.loginUser(it.get(0), it.get(1))
        }
        loginViewModel.loginInProgress.observe(viewLifecycleOwner) {
            this.binding.loginButton.showAnimation(it)
        }
        // https://stackoverflow.com/questions/50740757/how-to-use-android-navigation-without-binding-to-ui-in-viewmodel-mvvm
        // https://stackoverflow.com/questions/60622645/navigate-from-one-fragment-to-another-when-using-mvvm-pattern-for-android
        loginViewModel.navigateToPlayers.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(R.id.action_loginFragment_to_homeGraph)
        })
        loginViewModel.navigateToRegistration.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        })
        loginViewModel.navigateToForgotPassword.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        })
        loginViewModel.emailError.observe(viewLifecycleOwner, Observer {
            this.binding.emailTextInput.error = if (it != -1) requireContext().getString(it) else ""
        })
        loginViewModel.passwordError.observe(viewLifecycleOwner, Observer {
            this.binding.passwordTextInput.error = if (it != -1) requireContext().getString(it) else ""
        })
        loginViewModel.closeApp.observe(viewLifecycleOwner, EventObserver {
            this.activity?.finish()
        })
    }
}