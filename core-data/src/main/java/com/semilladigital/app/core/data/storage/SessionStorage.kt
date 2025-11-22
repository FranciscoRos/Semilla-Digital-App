package com.semilladigital.app.core.data.storage

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionStorage @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NOMBRE = "user_nombre"
        private const val KEY_APELLIDOS = "user_apellidos"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_ROL = "user_rol"
        private const val KEY_ESTATUS = "user_estatus"

        private const val KEY_INTERESES = "user_intereses_keywords"
    }

    // --- FLOWS ---
    private val _authTokenFlow = MutableStateFlow(sharedPreferences.getString(KEY_TOKEN, null))
    val authTokenFlow = _authTokenFlow.asStateFlow()

    private val _userNameFlow = MutableStateFlow(sharedPreferences.getString(KEY_NOMBRE, null))
    val userNameFlow = _userNameFlow.asStateFlow()

    private val _userRolFlow = MutableStateFlow(sharedPreferences.getString(KEY_ROL, null))
    val userRolFlow = _userRolFlow.asStateFlow()

    // --- GUARDAR SESIÓN ---
    fun saveSession(
        token: String,
        id: String,
        nombre: String,
        apellidos: String,
        email: String,
        rol: String,
        estatus: String

    ) {
        sharedPreferences.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, id)
            .putString(KEY_NOMBRE, nombre)
            .putString(KEY_APELLIDOS, apellidos)
            .putString(KEY_EMAIL, email)
            .putString(KEY_ROL, rol)
            .putString(KEY_ESTATUS, estatus)
            .apply()

        // Actualizar memoria
        _authTokenFlow.value = token
        _userNameFlow.value = nombre
        _userRolFlow.value = rol
    }

    fun saveIntereses(palabrasClave: List<String>) {

        val cleanList = palabrasClave.filter { it.isNotBlank() }.distinct()
        val stringSet = cleanList.joinToString(",")

        sharedPreferences.edit()
            .putString(KEY_INTERESES, stringSet)
            .apply()
    }

    fun getIntereses(): List<String> {
        val savedString = sharedPreferences.getString(KEY_INTERESES, "") ?: ""
        if (savedString.isBlank()) return emptyList()
        return savedString.split(",").map { it.trim() }
    }

    // --- OBTENER DATOS ---
    fun getToken() = sharedPreferences.getString(KEY_TOKEN, null)
    fun getUserId() = sharedPreferences.getString(KEY_USER_ID, "")
    fun getNombre() = sharedPreferences.getString(KEY_NOMBRE, "")
    fun getApellidos() = sharedPreferences.getString(KEY_APELLIDOS, "")
    fun getNombreCompleto() = "${getNombre()} ${getApellidos()}".trim()
    fun getEmail() = sharedPreferences.getString(KEY_EMAIL, "")
    fun getRol() = sharedPreferences.getString(KEY_ROL, "")
    fun getEstatus() = sharedPreferences.getString(KEY_ESTATUS, "")

    // --- CERRAR SESIÓN ---
    fun clearSession() {
        sharedPreferences.edit().clear().apply()
        _authTokenFlow.value = null
        _userNameFlow.value = null
        _userRolFlow.value = null
    }
}