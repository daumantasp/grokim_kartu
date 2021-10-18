package com.dauma.grokimkartu.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentLoginBinding
import com.dauma.grokimkartu.models.EventObserver
import com.dauma.grokimkartu.viewmodels.login.LoginViewModelImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val loginViewModel by viewModels<LoginViewModelImpl>()

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

        // TODO: Implement it in MVVM pattern
        // https://stackoverflow.com/questions/50740757/how-to-use-android-navigation-without-binding-to-ui-in-viewmodel-mvvm
        // https://stackoverflow.com/questions/60622645/navigate-from-one-fragment-to-another-when-using-mvvm-pattern-for-android
        binding.registerTextView.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }

        loginViewModel.getLoginForm().getFormFields().observe(viewLifecycleOwner) {
            loginViewModel.loginUser(it.get(0), it.get(1))
        }

        loginViewModel.navigateToPlayers.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(it as Int)
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}