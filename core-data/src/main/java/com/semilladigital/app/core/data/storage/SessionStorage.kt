package com.semilladigital.app.core.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Le decimos a Hilt que solo cree una instancia
class SessionStorage @Inject constructor(
    // Hilt nos inyectará el DataStore que definiremos en el siguiente paso
    private val dataStore: DataStore<Preferences>
) {

    // 1. Define las "llaves" para guardar los datos
    companion object {
        val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        // Aquí podríamos guardar también el ID del usuario, nombre, etc.
    }

    // 2. Función para GUARDAR el token
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    // 3. Función para LEER el token (como un Flujo)
    // Usamos un Flow para que la app pueda "escuchar"
    // los cambios en la sesión en tiempo real.
    val authTokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN_KEY]
    }

    // 4. Función para BORRAR el token (Logout)
    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
        }
    }
}