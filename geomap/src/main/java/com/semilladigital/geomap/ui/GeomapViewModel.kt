package com.semilladigital.geomap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.geomap.domain.model.Parcela
import com.semilladigital.geomap.domain.model.Ubicacion
import com.semilladigital.geomap.domain.use_case.GetMapDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
                            filteredParcelas = data.parcelas,
                            filteredUbicaciones = data.ubicaciones
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoading = false) }
                }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { s ->
            val cleanQuery = query.lowercase().trim()
            s.copy(
                searchQuery = query,
                filteredParcelas = s.allParcelas.filter { p ->
                    p.nombre.lowercase().contains(cleanQuery) ||
                            p.municipio.lowercase().contains(cleanQuery) ||
                            p.actividades.any { act -> act.lowercase().contains(cleanQuery) }
                },
                filteredUbicaciones = s.allUbicaciones.filter { u ->
                    u.nombre.lowercase().contains(cleanQuery) ||
                            u.municipio.lowercase().contains(cleanQuery) ||
                            u.tipo.lowercase().contains(cleanQuery)
                }
            )
        }
    }

    fun selectUbicacion(ubicacion: Ubicacion?) {
        _state.update { it.copy(selectedUbicacion = ubicacion) }
    }
}