package com.semilladigital.supports.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.app.core.data.storage.SessionStorage
import com.semilladigital.supports.domain.model.Apoyo
import com.semilladigital.supports.domain.repository.ApoyosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ApoyosState(
    val todosLosApoyos: List<Apoyo> = emptyList(),
    val apoyosParaTi: List<Apoyo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDetailsDialog: Boolean = false,
    val selectedApoyo: Apoyo? = null,
    val actividadesDelUsuario: List<String> = emptyList(),
    val searchQuery: String = "" // <--- AGREGADO PARA EL BUSCADOR
)

@HiltViewModel
class ApoyosViewModel @Inject constructor(
    private val repository: ApoyosRepository,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(ApoyosState())
    val state = _state.asStateFlow()

    init {
        loadActividadesUsuario()
        loadApoyos()
    }

    private fun loadActividadesUsuario() {
        _state.update {
            it.copy(actividadesDelUsuario = sessionStorage.getActividades())
        }
    }

    private fun loadApoyos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = repository.getAllApoyos()

            result.fold(
                onSuccess = { apoyos ->
                    val actividades = _state.value.actividadesDelUsuario
                    val apoyosFiltrados = if (actividades.isNotEmpty()) {
                        apoyos.filter { apoyo ->
                            apoyo.Requerimientos.any { req ->
                                req.type == "regla_parcela" && req.config?.actividades?.any { it in actividades } == true
                            }
                        }
                    } else {
                        emptyList()
                    }

                    _state.update {
                        it.copy(
                            todosLosApoyos = apoyos,
                            apoyosParaTi = apoyosFiltrados,
                            isLoading = false
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            error = "Error al cargar apoyos: ${e.message}",
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    fun onApoyoSelected(apoyo: Apoyo) {
        _state.update { it.copy(selectedApoyo = apoyo, showDetailsDialog = true) }
    }

    fun onCloseDetails() {
        _state.update { it.copy(showDetailsDialog = false, selectedApoyo = null) }
    }

    fun refreshApoyos() {
        loadApoyos()
    }

    // <--- NUEVA FUNCIÓN PARA EL BUSCADOR
    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }
}