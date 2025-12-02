package com.semilladigital.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.auth.domain.repository.AuthRepository
import com.semilladigital.app.core.data.storage.SessionStorage
import com.semilladigital.dashboard.domain.repository.DashboardRepository
import com.semilladigital.courses.domain.repository.CourseRepository
import com.semilladigital.supports.domain.repository.ApoyosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NovedadUiItem(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val fecha: String,
    val tipo: TipoNovedad
)

data class HistorialUiItem(
    val id: String,
    val titulo: String,
    val estatus: String = "Pendiente"
)

enum class TipoNovedad {
    CURSO, APOYO
}

data class DashboardState(
    val userName: String = "Cargando...",
    val userStatus: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedOut: Boolean = false,
    val novedades: List<NovedadUiItem> = emptyList(),
    val historial: List<HistorialUiItem> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionStorage: SessionStorage,
    private val dashboardRepository: DashboardRepository,
    private val courseRepository: CourseRepository,
    private val apoyosRepository: ApoyosRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadData()
        preloadModules()
        observeHistorial()
    }

    private fun preloadModules() {
        val userId = sessionStorage.getUserId()
        viewModelScope.launch {
            launch { courseRepository.refreshCourses() }
            launch { apoyosRepository.refreshApoyos() }

            if (!userId.isNullOrEmpty()) {
                launch { apoyosRepository.refreshRegistro(userId) }
            }
        }
    }

    private fun observeHistorial() {
        viewModelScope.launch {
            apoyosRepository.registroUsuario.collect { registro ->
                if (registro != null) {
                    val items = registro.HistorialApoyo.map {
                        HistorialUiItem(
                            id = it.idApoyo,
                            titulo = it.nombre_programa,
                            estatus = "Pendiente"
                        )
                    }
                    _state.update { it.copy(historial = items) }
                }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val token = sessionStorage.getToken()

            if (token != null) {
                launch { loadUserProfile(token) }
                launch { loadNovedades() }
            } else {
                _state.update { it.copy(isLoading = false, error = "No hay sesión") }
            }
        }
    }

    private suspend fun loadUserProfile(token: String) {
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
    }

    private suspend fun loadNovedades() {
        val result = dashboardRepository.getNovedades()
        result.fold(
            onSuccess = { response ->
                val cursosItems = response.cursos.map {
                    NovedadUiItem(it.id, it.Titulo, it.Descripcion, it.Creado, TipoNovedad.CURSO)
                }
                val apoyosItems = response.apoyos.map {
                    NovedadUiItem(it.id, it.nombre_programa, it.descripcion, it.Creado, TipoNovedad.APOYO)
                }

                val combined = (cursosItems + apoyosItems).sortedByDescending { it.fecha }

                _state.update { it.copy(novedades = combined) }
            },
            onFailure = {

            }
        )
    }

    fun onLogout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val token = sessionStorage.getToken()
            if (!token.isNullOrEmpty()) {
                authRepository.logout(token)
            }
            sessionStorage.clearSession()
            _state.update { it.copy(isLoading = false, isLoggedOut = true) }
        }
    }
}