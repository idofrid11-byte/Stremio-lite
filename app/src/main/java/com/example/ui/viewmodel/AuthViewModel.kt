package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import com.example.repository.AuthRepository

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val authRepository = AuthRepository(application)

    var isLoggedIn = mutableStateOf(authRepository.isLoggedIn())
        private set

    var userEmail = mutableStateOf(authRepository.getLoggedUserEmail())
        private set

    var emailInput = mutableStateOf("")
    var passwordInput = mutableStateOf("")
    var isRegisterMode = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun authenticate() {
        val email = emailInput.value.trim()
        val pass = passwordInput.value
        errorMessage.value = null

        val result = if (isRegisterMode.value) {
            authRepository.register(email, pass)
        } else {
            authRepository.login(email, pass)
        }

        result.onSuccess {
            isLoggedIn.value = true
            userEmail.value = authRepository.getLoggedUserEmail()
            errorMessage.value = null
        }.onFailure { err ->
            errorMessage.value = err.message
        }
    }

    fun logout() {
        authRepository.logout()
        isLoggedIn.value = false
        userEmail.value = null
        emailInput.value = ""
        passwordInput.value = ""
    }
}
