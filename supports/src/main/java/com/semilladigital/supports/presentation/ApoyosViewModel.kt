package com.semilladigital.supports.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.app.core.data.storage.SessionStorage
import com.semilladigital.supports.domain.model.Apoyo
import com.semilladigital.supports.domain.model.ApoyoUiItem
import com.semilladigital.supports.domain.model.EstatusApoyo
import com.semilladigital.supports.domain.model.ParcelaDetalle
import com.semilladigital.supports.domain.model.RegistroData
import com.semilladigital.supports.domain.repository.ApoyosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ApoyosState(
    val listadoApoyos: List<ApoyoUiItem> = emptyList(),
    val registroUsuario: RegistroData? = null,
    val isLoading: Boolean = false,
    val isInscribiendo: Boolean = false,
    val error: String? = null,
    val mensajeExito: String? = null,
    val showDetailsDialog: Boolean = false,
    val selectedApoyoItem: ApoyoUiItem? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class ApoyosViewModel @Inject constructor(
    private val repository: ApoyosRepository,
    private val sessionStorage: SessionStorage,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ApoyosState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    fun refreshApoyos() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val idUsuarioSesion = sessionStorage.getUserId()
            Log.d("DEBUG_SEMILLA", "ID Usuario en Sesión: '$idUsuarioSesion'")

            if (idUsuarioSesion.isNullOrBlank()) {
                _state.update { it.copy(isLoading = false, error = "No hay usuario en sesión.") }
                return@launch
            }

            try {
                val resultApoyos = repository.getAllApoyos()
                val resultRegistro = repository.getRegistroPorUsuario(idUsuarioSesion)

                if (resultApoyos.isSuccess) {
                    val apoyos = resultApoyos.getOrDefault(emptyList())
                    val registro = resultRegistro.getOrNull()

                    val itemsProcesados = if (registro != null) {
                        apoyos.map { determinarEstatus(apoyo = it, registro = registro) }
                    } else {
                        apoyos.map { ApoyoUiItem(it, EstatusApoyo.DISPONIBLE) }
                    }

                    val targetId = savedStateHandle.get<String>("id")
                    var itemToSelect: ApoyoUiItem? = null
                    var showDialog = false

                    if (!targetId.isNullOrEmpty()) {
                        itemToSelect = itemsProcesados.find { it.apoyo.id == targetId }
                        if (itemToSelect != null) {
                            showDialog = true
                        }
                    }

                    _state.update {
                        it.copy(
                            listadoApoyos = itemsProcesados,
                            registroUsuario = registro,
                            isLoading = false,
                            selectedApoyoItem = itemToSelect,
                            showDetailsDialog = showDialog
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Fallo al cargar apoyos") }
                }
            } catch (e: Exception) {
                Log.e("DEBUG_SEMILLA", "Excepción en loadData: ${e.message}")
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun inscribirseEnApoyo(apoyo: Apoyo) {
        val registro = _state.value.registroUsuario
        if (registro == null) {
            _state.update { it.copy(error = "No se encontraron datos de tu registro. Recarga la pantalla.") }
            return
        }

        val idParcela = registro.Usuario.Parcela.firstOrNull()?.idParcela
        if (idParcela == null) {
            _state.update { it.copy(error = "No tienes parcelas registradas.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isInscribiendo = true, error = null) }
            try {
                val result = repository.inscribirse(idApoyo = apoyo.id, idParcela = idParcela)
                result.fold(
                    onSuccess = {
                        _state.update { s ->
                            s.copy(isInscribiendo = false, showDetailsDialog = false, mensajeExito = it.message)
                        }
                        loadData()
                    },
                    onFailure = {
                        _state.update { s -> s.copy(isInscribiendo = false, error = it.message) }
                    }
                )
            } catch (e: Exception) {
                _state.update { s -> s.copy(isInscribiendo = false, error = e.message) }
            }
        }
    }

    fun limpiarMensajeExito() {
        _state.update { it.copy(mensajeExito = null) }
    }

    private fun determinarEstatus(apoyo: Apoyo, registro: RegistroData): ApoyoUiItem {
        val yaInscrito = registro.HistorialApoyo.any { it.idApoyo == apoyo.id }
        if (yaInscrito) {
            return ApoyoUiItem(apoyo, EstatusApoyo.YA_INSCRITO)
        }

        val parcelasUsuario = registro.Usuario.Parcela

        for (req in apoyo.Requerimientos) {
            if (req.type == "regla_parcela" && req.config != null) {
                val cumpleRegla = validarReglaParcela(req.config.actividades, req.config.hectareas, parcelasUsuario)
                if (!cumpleRegla) {
                    val motivo = if ((req.config.hectareas ?: 0.0) > 0) {
                        "Requiere ${req.config.hectareas} ha y actividades: ${req.config.actividades?.joinToString()}"
                    } else {
                        "Tu parcela no tiene la actividad requerida: ${req.config.actividades?.joinToString()}"
                    }
                    return ApoyoUiItem(apoyo, EstatusApoyo.NO_CUMPLE_REQUISITOS, motivo)
                }
            }
        }

        return ApoyoUiItem(apoyo, EstatusApoyo.DISPONIBLE)
    }

    private fun validarReglaParcela(
        actividadesRequeridas: List<String>?,
        hectareasRequeridas: Double?,
        parcelas: List<ParcelaDetalle>
    ): Boolean {
        return parcelas.any { parcela ->
            val cumpleArea = if (hectareasRequeridas != null) {
                parcela.area >= hectareasRequeridas
            } else {
                true
            }

            val cumpleActividad = if (!actividadesRequeridas.isNullOrEmpty()) {
                val actividadesParcela = parcela.usos.flatMap { it.actividadesEspecificas }
                actividadesRequeridas.any { reqAct ->
                    actividadesParcela.any { userAct ->
                        userAct.contains(reqAct, ignoreCase = true)
                    }
                }
            } else {
                true
            }

            cumpleArea && cumpleActividad
        }
    }

    fun onApoyoSelected(item: ApoyoUiItem) {
        _state.update { it.copy(selectedApoyoItem = item, showDetailsDialog = true) }
    }

    fun onCloseDetails() {
        _state.update { it.copy(showDetailsDialog = false, selectedApoyoItem = null) }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }
}