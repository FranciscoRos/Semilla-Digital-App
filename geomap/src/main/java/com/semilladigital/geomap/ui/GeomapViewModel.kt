package com.semilladigital.geomap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.geomap.domain.model.Parcela
import com.semilladigital.geomap.domain.model.Ubicacion
import com.semilladigital.geomap.domain.use_case.GetMapDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GeomapState(
    val isLoading: Boolean = false,
    val allParcelas: List<Parcela> = emptyList(),
    val allUbicaciones: List<Ubicacion> = emptyList(),
    val filteredParcelas: List<Parcela> = emptyList(),
    val filteredUbicaciones: List<Ubicacion> = emptyList(),
    val searchQuery: String = "",
    val selectedUbicacion: Ubicacion? = null
)

@HiltViewModel
class GeomapViewModel @Inject constructor(
    private val getMapDataUseCase: GetMapDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GeomapState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = getMapDataUseCase()

            result.fold(
                onSuccess = { data ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            allParcelas = data.parcelas,
                            allUbicaciones = data.ubicaciones,
                        )
                    }
                    applyFilter(_state.value.searchQuery)
                },
                onFailure = {
                    _state.update { it.copy(isLoading = false) }
                }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilter(query)
    }

    private fun applyFilter(query: String) {
        val cleanQuery = query.lowercase().trim()
        val currentState = _state.value

        val filteredParcelas = currentState.allParcelas.filter { p ->
            p.nombre.lowercase().contains(cleanQuery) ||
                    p.municipio.lowercase().contains(cleanQuery) ||
                    p.actividades.any { act -> act.lowercase().contains(cleanQuery) }
        }

        val filteredUbicaciones = currentState.allUbicaciones.filter { u ->
            u.nombre.lowercase().contains(cleanQuery) ||
                    u.municipio.lowercase().contains(cleanQuery) ||
                    u.tipo.lowercase().contains(cleanQuery)
        }

        _state.update {
            it.copy(
                filteredParcelas = filteredParcelas,
                filteredUbicaciones = filteredUbicaciones
            )
        }
    }

    fun selectUbicacion(ubicacion: Ubicacion?) {
        _state.update { it.copy(selectedUbicacion = ubicacion) }
    }
}