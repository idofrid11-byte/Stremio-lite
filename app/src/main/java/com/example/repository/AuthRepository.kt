package com.example.repository

import android.content.Context

class AuthRepository(context: Context) {
    private val prefs = context.getSharedPreferences("stremio_auth_prefs", Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun getLoggedUserEmail(): String? {
        return prefs.getString("user_email", null)
    }

    fun register(email: String, pass: String): Result<Unit> {
        if (!email.contains("@") || pass.length < 6) {
            return Result.failure(IllegalArgumentException("Invalid email or password must be at least 6 characters."))
        }
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_email", email)
            .putString("user_pass", pass)
            .apply()
        return Result.success(Unit)
    }

    fun login(email: String, pass: String): Result<Unit> {
        val savedEmail = prefs.getString("user_email", null)
        val savedPass = prefs.getString("user_pass", null)

        // For demo/offline persistence, or if registering first:
        if (savedEmail == null) {
            return register(email, pass)
        }

        if (savedEmail == email && savedPass == pass) {
            prefs.edit().putBoolean("is_logged_in", true).apply()
            return Result.success(Unit)
        } else {
            return Result.failure(IllegalArgumentException("Incorrect email or password."))
        }
    }

    fun logout() {
        prefs.edit().putBoolean("is_logged_in", false).apply()
    }
}
