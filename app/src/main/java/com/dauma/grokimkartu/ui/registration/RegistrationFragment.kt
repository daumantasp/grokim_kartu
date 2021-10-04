package com.dauma.grokimkartu.ui.registration

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.viewmodels.registration.RegistrationViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.auth.FirebaseAuth

@AndroidEntryPoint
class RegistrationFragment : Fragment() {
    private val registrationViewModel by viewModels<RegistrationViewModelImpl>()
    private var mAuth: FirebaseAuth? = null
    private var closeImageButton: ImageButton? = null
    private var nameEditText: EditText? = null
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var registerButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_registration, container, false)
        closeImageButton = rootView.findViewById(R.id.closeImageButton)
        nameEditText = rootView.findViewById(R.id.nameEditText)
        emailEditText = rootView.findViewById(R.id.emailEditText)
        passwordEditText = rootView.findViewById(R.id.passwordEditText)
        registerButton = rootView.findViewById(R.id.registerButton)

        mAuth = FirebaseAuth.getInstance();

        // TODO: Implement it in MVVM pattern
        closeImageButton!!.setOnClickListener {
            it.findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }

        registerButton!!.setOnClickListener {
            registerUser()
        }

        return rootView
    }

    private fun registerUser() {
        val name = nameEditText?.text.toString().trim()
        val email = emailEditText?.text.toString().trim()
        val password = passwordEditText?.text.toString().trim()

        if (name.isEmpty()) {
            nameEditText?.error = "Name is required!"
            nameEditText?.requestFocus()
            return
        }

        if (email.isEmpty()) {
            emailEditText?.error = "Email is required!"
            emailEditText?.requestFocus()
            return
        }

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() == false) {
            emailEditText?.error = "Please provie valid email!"
            emailEditText?.requestFocus()
            return
        }

        if (password.isEmpty()) {
            passwordEditText?.error = "Password is required!"
            passwordEditText?.requestFocus()
            return
        }

        if (password.length < 6) {
            passwordEditText?.error = "Min password length should be 6 characters!"
            passwordEditText?.requestFocus()
            return
        }

        registrationViewModel.createUser(name, email, password)
    }
}