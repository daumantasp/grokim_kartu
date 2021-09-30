package com.dauma.grokimkartu.ui.registration

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.viewmodels.registration.RegistrationViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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

        mAuth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { result ->
                if (result.isSuccessful()) {
                    val user = User(name, email, password)

                    FirebaseAuth.getInstance().currentUser?.let {
                        FirebaseDatabase.getInstance().getReference("Users")
                            .child(it.uid)
                            .setValue(user).addOnCompleteListener { result ->
                                if (result.isSuccessful()) {
                                    Toast.makeText(requireContext(), "User has been registered successfully!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(requireContext(), "Failed to register!", Toast.LENGTH_LONG).show()
                                }
                            }
                            .addOnFailureListener {
                                result ->
                                Toast.makeText(requireContext(), result.toString(), Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to register!", Toast.LENGTH_LONG).show()
                }
            }
    }
}

private class User {
    var name: String = ""
    var email: String = ""
    var password: String = ""

     constructor(
         name: String,
         email: String,
         password: String
     ) {
         this.name = name
         this.email = email
         this.password = password
    }

    constructor() {
    }
}