package com.dauma.grokimkartu.viewmodels.registration

interface RegistrationViewModel {
    fun createUser(name: String, email: String, password: String)
}