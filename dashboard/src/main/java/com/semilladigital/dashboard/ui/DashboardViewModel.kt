package com.semilladigital.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.auth.domain.repository.AuthRepository
import com.semilladigital.app.core.data.storage.SessionStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val userName: String = "Cargando...",
    val userStatus: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val token = sessionStorage.getToken()

            if (token != null) {
                val result = authRepository.getUserProfile(token)

                result.fold(
                    onSuccess = { user ->
                        // 1. LÓGICA DE APLANADO (FLATTEN)
                        // Extraemos todas las palabras clave del usuario
                        val palabrasClave = mutableListOf<String>()

                        user.usos.forEach { uso ->
                            // Guardamos el general (ej: "Agrícola")
                            if (uso.usoGeneral.isNotBlank()) {
                                palabrasClave.add(uso.usoGeneral)
                            }
                            // Guardamos los específicos (ej: "Cultivo de tomate")
                            uso.usosEspecificos.forEach { especifico ->
                                if (especifico.isNotBlank()) {
                                    palabrasClave.add(especifico)
                                }
                            }
                        }

                        // 2. GUARDAMOS EN SESSION STORAGE (Para que Cursos lo use después)
                        sessionStorage.saveIntereses(palabrasClave)

                        // 3. ACTUALIZAMOS UI
                        _state.update {
                            it.copy(
                                isLoading = false,
                                userName = user.nombre,
                                userStatus = user.estatus
                            )
                        }
                    },
                    onFailure = { e ->
                        _state.update { it.copy(isLoading = false, userName = "Usuario", error = e.message) }
                    }
                )
            } else {
                _state.update { it.copy(isLoading = false, error = "No hay sesión") }
            }
        }
    }

    fun onLogout() {
        sessionStorage.clearSession()
    }
}