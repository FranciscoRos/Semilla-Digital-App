package com.semilladigital.supports.presentation

import android.util.Log
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
import java.util.concurrent.CancellationException
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
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(ApoyosState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    fun refreshApoyos() {
        loadData()
    }

// ... imports ...

// DENTRO DE ApoyosViewModel

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val idUsuarioSesion = sessionStorage.getUserId()
            Log.d("DEBUG_SEMILLA", "1. ID Usuario en Sesión: '$idUsuarioSesion'")

            if (idUsuarioSesion.isNullOrBlank()) {
                _state.update { it.copy(isLoading = false, error = "No hay usuario en sesión.") }
                return@launch
            }

            try {
                // 1. Cargar Apoyos
                val resultApoyos = repository.getAllApoyos()

                // 2. Cargar Registros
                // Nota: Usamos getTodosLosRegistros directamente en el repo o la lógica de filtrado
                // Como tu repo usa 'getRegistroPorUsuario', vamos a ver qué hace.
                val resultRegistro = repository.getRegistroPorUsuario(idUsuarioSesion)

                if (resultApoyos.isSuccess) {
                    val apoyos = resultApoyos.getOrDefault(emptyList())
                    val registro = resultRegistro.getOrNull()

                    Log.d("DEBUG_SEMILLA", "2. Apoyos cargados: ${apoyos.size}")

                    if (registro != null) {
                        Log.d("DEBUG_SEMILLA", "3. ¡Registro encontrado! ID Registro: ${registro.id}")
                        Log.d("DEBUG_SEMILLA", "4. Parcelas en registro: ${registro.Usuario.Parcela.size}")
                        if (registro.Usuario.Parcela.isNotEmpty()) {
                            Log.d("DEBUG_SEMILLA", "   -> Primera Parcela ID: ${registro.Usuario.Parcela[0].idParcela}")
                        }
                    } else {
                        Log.e("DEBUG_SEMILLA", "3. ERROR: No se encontró registro para el usuario '$idUsuarioSesion'")
                        // Aquí está el problema potencial: Si falla, el botón no tendrá ID de parcela
                    }

                    val itemsProcesados = if (registro != null) {
                        apoyos.map { determinarEstatus(apoyo = it, registro = registro) }
                    } else {
                        apoyos.map { ApoyoUiItem(it, EstatusApoyo.DISPONIBLE) }
                    }

                    _state.update {
                        it.copy(
                            listadoApoyos = itemsProcesados,
                            registroUsuario = registro,
                            isLoading = false
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
        Log.d("DEBUG_SEMILLA", ">>> Click en Inscribirse a: ${apoyo.nombre_programa}")

        val registro = _state.value.registroUsuario

        if (registro == null) {
            Log.e("DEBUG_SEMILLA", "ERROR BLOQUEANTE: registroUsuario es NULL. No se puede obtener parcela.")
            _state.update { it.copy(error = "No se encontraron datos de tu registro. Recarga la pantalla.") }
            return
        }

        val idParcela = registro.Usuario.Parcela.firstOrNull()?.idParcela
        Log.d("DEBUG_SEMILLA", "ID Parcela detectado: $idParcela")

        if (idParcela == null) {
            Log.e("DEBUG_SEMILLA", "ERROR BLOQUEANTE: El usuario tiene registro pero NO tiene parcelas.")
            _state.update { it.copy(error = "No tienes parcelas registradas.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isInscribiendo = true, error = null) }
            try {
                Log.d("DEBUG_SEMILLA", "Enviando POST... Apoyo: ${apoyo.id}, Parcela: $idParcela")
                val result = repository.inscribirse(idApoyo = apoyo.id, idParcela = idParcela)

                result.fold(
                    onSuccess = {
                        Log.d("DEBUG_SEMILLA", "¡Éxito! ${it.message}")
                        _state.update { s ->
                            s.copy(isInscribiendo = false, showDetailsDialog = false, mensajeExito = it.message)
                        }
                        loadData()
                    },
                    onFailure = {
                        Log.e("DEBUG_SEMILLA", "Fallo POST: ${it.message}")
                        _state.update { s -> s.copy(isInscribiendo = false, error = it.message) }
                    }
                )
            } catch (e: Exception) {
                Log.e("DEBUG_SEMILLA", "Excepción POST: ${e.message}")
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