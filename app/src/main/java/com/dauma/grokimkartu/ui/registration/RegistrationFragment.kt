package com.dauma.grokimkartu.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.viewmodels.registration.RegistrationViewModelImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : Fragment() {
    private val registrationViewModel by viewModels<RegistrationViewModelImpl>()
    private var closeImageButton: ImageButton? = null
    private var nameEditText: EditText? = null
    private var emailTextView: EditText? = null
    private var passwordEditText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_registration, container, false)
        closeImageButton = rootView.findViewById(R.id.closeImageButton)
        nameEditText = rootView.findViewById(R.id.nameEditText)
        emailTextView = rootView.findViewById(R.id.emailEditText)
        passwordEditText = rootView.findViewById(R.id.passwordEditText)

        // TODO: Implement it in MVVM pattern
        closeImageButton!!.setOnClickListener {
            it.findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }

        return rootView
    }
}