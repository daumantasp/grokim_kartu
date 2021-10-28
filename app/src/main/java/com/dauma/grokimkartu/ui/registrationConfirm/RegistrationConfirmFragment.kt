package com.dauma.grokimkartu.ui.registrationConfirm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentRegistrationConfirmBinding
import com.dauma.grokimkartu.models.EventObserver
import com.dauma.grokimkartu.viewmodels.registrationConfirm.RegistrationConfirmViewModelImpl

class RegistrationConfirmFragment : Fragment() {
    private val registrationConfirmViewModel by viewModels<RegistrationConfirmViewModelImpl>()

    private var _binding: FragmentRegistrationConfirmBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRegistrationConfirmBinding.inflate(inflater, container, false)
        binding.model = registrationConfirmViewModel
        val view = binding.root

        registrationConfirmViewModel.navigateToLogin.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                findNavController().navigate(R.id.action_registrationConfirmFragment_to_loginFragment)
            }
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}