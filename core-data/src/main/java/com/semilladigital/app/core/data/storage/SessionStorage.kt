package com.semilladigital.app.core.data.storage

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionStorage @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val AUTH_TOKEN_KEY = "auth_token"
    }

    // Guardar Token
    fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString(AUTH_TOKEN_KEY, token)
            .apply()
    }

    // --- ESTE ES EL MÉTODO QUE FALTABA ---
    fun getAuthToken(): String? {
        return sharedPreferences.getString(AUTH_TOKEN_KEY, null)
    }

    // Cerrar Sesión
    fun clearToken() {
        sharedPreferences.edit()
            .remove(AUTH_TOKEN_KEY)
            .apply()
    }
}