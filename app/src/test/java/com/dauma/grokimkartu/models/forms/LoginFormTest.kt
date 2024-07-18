package com.dauma.grokimkartu.models.forms

import com.dauma.grokimkartu.models.validators.email.EmailValidator
import com.dauma.grokimkartu.models.validators.password.PasswordValidator
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LoginFormTest {

    private val email: String = "jonas@mail.com"
    private val password: String = "=nYjSPefU+FN[tc"

    @Test
    fun `email setting`() {
        val loginForm = createForm(isEmailValid = true, isPasswordValid = true)
        loginForm.setEmail(email)
        assertThat(loginForm.getEmail()).isEqualTo(email)
    }

    @Test
    fun `password setting`() {
        val loginForm = createForm(isEmailValid = true, isPasswordValid = true)
        loginForm.setPassword(password)
        assertThat(loginForm.getPassword()).isEqualTo(password)
    }

    @Test
    fun `email valid and password valid is valid returns true`() {
        val loginForm = createForm(isEmailValid = true, isPasswordValid = true)
        assertThat(loginForm.isValid()).isTrue()
    }

    @Test
    fun `email valid and password not valid is valid returns false`() {
        val loginForm = createForm(isEmailValid = true, isPasswordValid = false)
        assertThat(loginForm.isValid()).isFalse()
    }

    @Test
    fun `email not valid and password valid is valid returns false`() {
        val loginForm = createForm(isEmailValid = false, isPasswordValid = true)
        assertThat(loginForm.isValid()).isFalse()
    }

    @Test
    fun `email not valid and password not valid is valid returns false`() {
        val loginForm = createForm(isEmailValid = false, isPasswordValid = false)
        assertThat(loginForm.isValid()).isFalse()
    }

    private fun createForm(isEmailValid: Boolean, isPasswordValid: Boolean): LoginForm {
        return LoginForm(
            emailValidator = object : EmailValidator {
                override fun isValid(email: String): Boolean = isEmailValid
            },
            passwordValidator = object : PasswordValidator {
                override fun isValid(password: String): Boolean = isPasswordValid
            }
        )
    }
}