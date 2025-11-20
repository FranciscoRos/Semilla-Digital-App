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

            val token = sessionStorage.getAuthToken()

            if (token != null) {
                val result = authRepository.getUserProfile(token)

                result.fold(
                    onSuccess = { user ->
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
        sessionStorage.clearToken()
        // La navegación global reaccionará al cambio en SessionStorage
    }
}