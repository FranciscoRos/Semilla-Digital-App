package com.semilladigital.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.auth.domain.repository.AuthRepository
import com.semilladigital.app.core.data.storage.SessionStorage // <-- 1. IMPORTA EL STORAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionStorage: SessionStorage // <-- 2. INYECTA EL STORAGE
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChanged -> {
                _state.update { it.copy(email = event.email) }
            }
            is LoginEvent.OnPasswordChanged -> {
                _state.update { it.copy(contrasena = event.contrasena) }
            }
            is LoginEvent.OnLoginClick -> {
                login()
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = authRepository.login(
                correo = _state.value.email,
                contrasena = _state.value.contrasena
            )

            result.fold(
                onSuccess = { authResult ->
                    // --- 3. ¡CAMBIO AQUÍ! ---
                    // Guardamos el token en DataStore
                    sessionStorage.saveToken(authResult.token)

                    // (Opcional: guardar también datos del usuario si quieres)

                    _state.update {
                        it.copy(
                            isLoading = false,
                            loginSuccess = true // Avisamos a la UI que navegue
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error desconocido"
                        )
                    }
                }
            )
        }
    }
}