package com.dauma.grokimkartu.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentLoginBinding
import com.dauma.grokimkartu.databinding.FragmentRegistrationBinding
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
        binding.registerTextView.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }

        loginViewModel.getLoginForm().getFormFields().observe(viewLifecycleOwner) {
            loginViewModel.loginUser(it.get(0), it.get(1))
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}