package com.semilladigital.forum.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.forum.domain.model.*
import com.semilladigital.forum.domain.repository.ForoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubtemaUiItem(
    val id: String,
    val titulo: String,
    val temasCount: Int
)

data class CategoriaUiItem(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val icono: String,
    val subtemas: List<SubtemaUiItem>
)

data class TemaRecienteUi(
    val id: String,
    val titulo: String,
    val autor: String,
    val ubicacion: String,
    val fecha: String,
    val categoria: String,
    val respuestasCount: Int,
    val vistasCount: Int
)

data class TemaDetalleUi(
    val tema: TemaDetalle? = null,
    val comentarios: List<Comentario> = emptyList()
)

data class ForoState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val categorias: List<CategoriaUiItem> = emptyList(),
    val temasRecientes: List<TemaRecienteUi> = emptyList(),
    val detalle: TemaDetalleUi = TemaDetalleUi()
)

@HiltViewModel
class ForoViewModel @Inject constructor(
    private val foroRepository: ForoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ForoState())
    val state = _state.asStateFlow()

    init {
        loadCategorias()
        loadTemasRecientes()
    }

    private fun loadCategorias() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = foroRepository.getCategorias()
            result.fold(
                onSuccess = { categorias ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            categorias = categorias.map { cat ->
                                CategoriaUiItem(
                                    id = cat.id,
                                    titulo = cat.titulo,
                                    descripcion = cat.descripcion,
                                    icono = cat.icono,
                                    subtemas = cat.subtemas.map { sub -> SubtemaUiItem(sub.id, sub.titulo, sub.temasCount) }
                                )
                            }
                        )
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = "Error al cargar categorías: ${e.message}") }
                }
            )
        }
    }

    private fun loadTemasRecientes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = foroRepository.getTemasRecientes()
            result.fold(
                onSuccess = { temas ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            temasRecientes = temas.map { tema ->
                                TemaRecienteUi(
                                    id = tema.id,
                                    titulo = tema.titulo,
                                    autor = tema.autor,
                                    ubicacion = tema.ubicacion,
                                    fecha = tema.fecha,
                                    categoria = tema.categoria,
                                    respuestasCount = tema.respuestasCount,
                                    vistasCount = tema.vistasCount
                                )
                            }
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoading = false, error = "Error al cargar temas recientes.") }
                }
            )
        }
    }

    fun loadTemaDetalle(temaId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val detalleResult = foroRepository.getTemaDetalle(temaId)
            val comentariosResult = foroRepository.getComentarios(temaId)
            val temaDetalle = detalleResult.getOrNull()
            val comentarios = comentariosResult.getOrNull() ?: emptyList()
            if (temaDetalle != null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        detalle = TemaDetalleUi(temaDetalle, comentarios)
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, error = "Error al cargar el detalle del tema.") }
            }
        }
    }

    fun clearDetalle() {
        _state.update { it.copy(detalle = TemaDetalleUi()) }
    }
}