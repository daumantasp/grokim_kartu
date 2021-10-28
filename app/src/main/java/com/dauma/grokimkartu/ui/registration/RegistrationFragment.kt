package com.dauma.grokimkartu.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentRegistrationBinding
import com.dauma.grokimkartu.models.EventObserver
import com.dauma.grokimkartu.viewmodels.registration.RegistrationViewModelImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : Fragment() {
    private val registrationViewModel by viewModels<RegistrationViewModelImpl>()

    private var _binding: FragmentRegistrationBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        binding.model = registrationViewModel
        val view = binding.root

        // TODO: Implement it in MVVM pattern
        binding.closeImageButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }

        registrationViewModel.getRegistrationForm().getFormFields().observe(viewLifecycleOwner) {
            registrationViewModel.createUser(it.get(0), it.get(1), it.get(2))
        }

        registrationViewModel.emailVerificationSent.observe(viewLifecycleOwner, EventObserver {
            if (it as Boolean) {
                findNavController().navigate(R.id.action_registrationFragment_to_registrationConfirmFragment)
            }
        })

        registrationViewModel.emailError.observe(viewLifecycleOwner, Observer {
            binding.emailTextInput.error = if (it != -1) requireContext().getString(it) else ""
        })

        registrationViewModel.passwordError.observe(viewLifecycleOwner, Observer {
            binding.passwordTextInput.error = if (it != -1) requireContext().getString(it) else ""
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}