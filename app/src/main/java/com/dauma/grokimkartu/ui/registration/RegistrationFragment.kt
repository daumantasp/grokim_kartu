package com.dauma.grokimkartu.ui.registration

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentRegistrationBinding
import com.dauma.grokimkartu.viewmodels.registration.RegistrationViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.auth.FirebaseAuth

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

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}