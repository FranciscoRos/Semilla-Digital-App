package com.semilladigital.geomap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.geomap.data.GeomapRepository
import com.semilladigital.geomap.data.ParcelaDto
import com.semilladigital.geomap.data.UbicacionEspecialDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GeomapState(
    val isLoading: Boolean = false,
    val allParcelas: List<ParcelaDto> = emptyList(),
    val allUbicaciones: List<UbicacionEspecialDto> = emptyList(),
    val filteredParcelas: List<ParcelaDto> = emptyList(),
    val filteredUbicaciones: List<UbicacionEspecialDto> = emptyList(),
    val searchQuery: String = "",
    val selectedUbicacion: UbicacionEspecialDto? = null
)

@HiltViewModel
class GeomapViewModel @Inject constructor(
    private val repository: GeomapRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GeomapState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = repository.getMapData()

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
                    // Manejar error (Toast o Snackbar en UI)
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
                            p.usos.any { u -> u.actividadesEspecificas.any { act -> act.lowercase().contains(cleanQuery) } }
                },
                filteredUbicaciones = s.allUbicaciones.filter { u ->
                    u.nombre.lowercase().contains(cleanQuery) ||
                            u.municipio.lowercase().contains(cleanQuery) ||
                            u.tipo.lowercase().contains(cleanQuery)
                }
            )
        }
    }

    fun selectUbicacion(ubicacion: UbicacionEspecialDto?) {
        _state.update { it.copy(selectedUbicacion = ubicacion) }
    }
}