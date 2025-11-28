package com.semilladigital.dashboard.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.app.core.data.storage.SessionStorage
import com.semilladigital.auth.domain.repository.AuthRepository
import com.semilladigital.supports.domain.repository.ApoyosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UsoUiState(
    val area: String = "",
    val actividades: List<String> = emptyList()
)

data class ParcelaUiState(
    val idParcela: String = "",
    val nombre: String = "",
    val municipio: String = "",
    val localidad: String = "",
    val direccionAdicional: String = "",
    val area: String = "",
    val coordenadas: List<List<Double>> = emptyList(),
    val usos: List<UsoUiState> = emptyList()
)

data class ProfileState(
    val idRegistro: String = "",
    val nombre: String = "",
    val apellido1: String = "",
    val apellido2: String = "",
    val correo: String = "",
    val telefono: String = "",
    val curp: String = "",
    val rfc: String = "",
    val ine: String = "",
    val fechaNacimiento: String = "",

    val calle: String = "",
    val colonia: String = "",
    val municipio: String = "",
    val ciudad: String = "",
    val estadoDir: String = "",
    val codigoPostal: String = "",
    val referencia: String = "",

    val tieneRiego: String = "no",
    val trabajadores: String = "",

    val rawExtras: Map<String, Any> = emptyMap(),
    val parcelas: List<ParcelaUiState> = emptyList(),

    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val apoyosRepository: ApoyosRepository,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    val areasCatalogo = listOf("Agrícola", "Ganadera", "Pesca", "Forestal", "Otro")

    val actividadesCatalogo = mapOf(
        "Agrícola" to listOf("Maíz", "Frijol", "Hortalizas", "Frutales", "Caña de azúcar", "Otro"),
        "Ganadera" to listOf("Bovinos (Vacas)", "Porcinos (Cerdos)", "Ovinos (Borregos)", "Caprinos (Cabras)", "Aves de corral", "Apicultura", "Otro"),
        "Pesca" to listOf("Pesca ribereña", "Pesca de altura", "Acuacultura", "Otro"),
        "Forestal" to listOf("Maderable", "No maderable", "Otro"),
        "Otro" to listOf("Otro")
    )

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = sessionStorage.getUserId()

            if (!userId.isNullOrEmpty()) {
                val result = apoyosRepository.getRegistroPorUsuario(userId, forceRefresh = true)

                result.fold(
                    onSuccess = { registro ->
                        val usuario = registro.Usuario
                        val domicilio = usuario.Domicilio
                        val extras = registro.CamposExtra ?: emptyMap()

                        val mappedParcelas = usuario.Parcela.map { p ->
                            val mappedUsos = p.usos.map { uso ->
                                UsoUiState(area = uso.area, actividades = uso.actividadesEspecificas)
                            }
                            // Convertir Coordenada(lat, lng) -> [lat, lng]
                            val coordsList = p.coordenadas?.map { listOf(it.lat, it.lng) } ?: emptyList()

                            ParcelaUiState(
                                idParcela = p.idParcela ?: "",
                                nombre = p.nombre ?: "",
                                municipio = p.municipio ?: "",
                                localidad = p.localidad ?: "",
                                direccionAdicional = p.direccionAdicional ?: "",
                                area = p.area?.toString() ?: "0.0",
                                coordenadas = coordsList,
                                usos = mappedUsos
                            )
                        }

                        _state.update {
                            it.copy(
                                isLoading = false,
                                idRegistro = registro.id,
                                nombre = usuario.Nombre,
                                apellido1 = usuario.Apellido1 ?: "",
                                apellido2 = usuario.Apellido2 ?: "",
                                telefono = usuario.Telefono ?: "",
                                correo = usuario.Correo ?: sessionStorage.getEmail() ?: "",
                                curp = usuario.Curp ?: "",
                                rfc = usuario.Rfc ?: "",
                                ine = usuario.Ine ?: "",
                                fechaNacimiento = usuario.FechaNacimiento ?: "",

                                calle = domicilio?.Calle ?: "",
                                colonia = domicilio?.Colonia ?: "",
                                municipio = domicilio?.Municipio ?: "",
                                ciudad = domicilio?.Ciudad ?: "",
                                estadoDir = domicilio?.Estado ?: "",
                                codigoPostal = domicilio?.CodigoPostal ?: "",
                                referencia = domicilio?.Referencia ?: "",

                                // Usar toString() para leer seguros
                                tieneRiego = extras["sistema_riego"]?.toString()
                                    ?: extras["tieneRiego"]?.toString()
                                    ?: "no",

                                trabajadores = extras["personal_mando"]?.toString()
                                    ?: extras["trabajadores"]?.toString()
                                    ?: "",

                                rawExtras = extras,
                                parcelas = mappedParcelas
                            )
                        }
                    },
                    onFailure = {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                nombre = sessionStorage.getNombre() ?: "",
                                correo = sessionStorage.getEmail() ?: ""
                            )
                        }
                    }
                )
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onNombreChange(v: String) { _state.update { it.copy(nombre = v) } }
    fun onApellido1Change(v: String) { _state.update { it.copy(apellido1 = v) } }
    fun onApellido2Change(v: String) { _state.update { it.copy(apellido2 = v) } }
    fun onTelefonoChange(v: String) { _state.update { it.copy(telefono = v) } }
    fun onCurpChange(v: String) { _state.update { it.copy(curp = v) } }
    fun onRfcChange(v: String) { _state.update { it.copy(rfc = v) } }
    fun onIneChange(v: String) { _state.update { it.copy(ine = v) } }
    fun onFechaChange(v: String) { _state.update { it.copy(fechaNacimiento = v) } }

    fun onCalleChange(v: String) { _state.update { it.copy(calle = v) } }
    fun onColoniaChange(v: String) { _state.update { it.copy(colonia = v) } }
    fun onMunicipioChange(v: String) { _state.update { it.copy(municipio = v) } }
    fun onCiudadChange(v: String) { _state.update { it.copy(ciudad = v) } }
    fun onEstadoDirChange(v: String) { _state.update { it.copy(estadoDir = v) } }
    fun onCodigoPostalChange(v: String) { _state.update { it.copy(codigoPostal = v) } }
    fun onReferenciaChange(v: String) { _state.update { it.copy(referencia = v) } }

    fun onRiegoChange(v: String) { _state.update { it.copy(tieneRiego = v) } }
    fun onTrabajadoresChange(v: String) { _state.update { it.copy(trabajadores = v) } }

    fun onParcelaFieldChange(index: Int, field: String, value: String) {
        val currentParcelas = _state.value.parcelas.toMutableList()
        val p = currentParcelas[index]
        currentParcelas[index] = when(field) {
            "nombre" -> p.copy(nombre = value)
            "municipio" -> p.copy(municipio = value)
            "localidad" -> p.copy(localidad = value)
            "direccion" -> p.copy(direccionAdicional = value)
            "area" -> p.copy(area = value)
            else -> p
        }
        _state.update { it.copy(parcelas = currentParcelas) }
    }

    fun addUsoToParcela(parcelaIndex: Int) {
        val currentParcelas = _state.value.parcelas.toMutableList()
        val p = currentParcelas[parcelaIndex]
        val currentUsos = p.usos.toMutableList()
        currentUsos.add(UsoUiState(area = "Agrícola", actividades = emptyList()))
        currentParcelas[parcelaIndex] = p.copy(usos = currentUsos)
        _state.update { it.copy(parcelas = currentParcelas) }
    }

    fun removeUsoFromParcela(parcelaIndex: Int, usoIndex: Int) {
        val currentParcelas = _state.value.parcelas.toMutableList()
        val p = currentParcelas[parcelaIndex]
        val currentUsos = p.usos.toMutableList()
        if (usoIndex in currentUsos.indices) {
            currentUsos.removeAt(usoIndex)
            currentParcelas[parcelaIndex] = p.copy(usos = currentUsos)
            _state.update { it.copy(parcelas = currentParcelas) }
        }
    }

    fun updateUsoArea(parcelaIndex: Int, usoIndex: Int, newArea: String) {
        val currentParcelas = _state.value.parcelas.toMutableList()
        val p = currentParcelas[parcelaIndex]
        val currentUsos = p.usos.toMutableList()
        if (usoIndex in currentUsos.indices) {
            currentUsos[usoIndex] = currentUsos[usoIndex].copy(area = newArea, actividades = emptyList())
            currentParcelas[parcelaIndex] = p.copy(usos = currentUsos)
            _state.update { it.copy(parcelas = currentParcelas) }
        }
    }

    fun addActividadToUso(parcelaIndex: Int, usoIndex: Int, actividad: String) {
        val currentParcelas = _state.value.parcelas.toMutableList()
        val p = currentParcelas[parcelaIndex]
        val currentUsos = p.usos.toMutableList()
        if (usoIndex in currentUsos.indices) {
            val uso = currentUsos[usoIndex]
            if (!uso.actividades.contains(actividad)) {
                val newActs = uso.actividades + actividad
                currentUsos[usoIndex] = uso.copy(actividades = newActs)
                currentParcelas[parcelaIndex] = p.copy(usos = currentUsos)
                _state.update { it.copy(parcelas = currentParcelas) }
            }
        }
    }

    fun removeActividadFromUso(parcelaIndex: Int, usoIndex: Int, actividad: String) {
        val currentParcelas = _state.value.parcelas.toMutableList()
        val p = currentParcelas[parcelaIndex]
        val currentUsos = p.usos.toMutableList()
        if (usoIndex in currentUsos.indices) {
            val uso = currentUsos[usoIndex]
            val newActs = uso.actividades.filter { it != actividad }
            currentUsos[usoIndex] = uso.copy(actividades = newActs)
            currentParcelas[parcelaIndex] = p.copy(usos = currentUsos)
            _state.update { it.copy(parcelas = currentParcelas) }
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null, successMessage = null) }

            val idRegistro = _state.value.idRegistro.ifEmpty { sessionStorage.getIdRegistro() }
            val idUsuario = sessionStorage.getUserId()

            if (idRegistro.isNullOrEmpty()) {
                _state.update { it.copy(isSaving = false, error = "Error: ID de registro no encontrado.") }
                return@launch
            }

            val parcelasDto = _state.value.parcelas.map { p ->
                val coordsMap = p.coordenadas.map {
                    mapOf("lat" to (it.getOrNull(0) ?: 0.0), "lng" to (it.getOrNull(1) ?: 0.0))
                }

                val usosDto = p.usos.map { u ->
                    mapOf("area" to u.area, "actividadesEspecificas" to u.actividades)
                }

                mapOf(
                    "idParcela" to p.idParcela,
                    "nombre" to p.nombre,
                    "municipio" to p.municipio,
                    "localidad" to p.localidad,
                    "direccionAdicional" to p.direccionAdicional,
                    "area" to (p.area.toDoubleOrNull() ?: 0.0),
                    "coordenadas" to coordsMap,
                    "usos" to usosDto
                )
            }

            val extrasEditados = mapOf(
                "sistema_riego" to _state.value.tieneRiego,
                "personal_mando" to _state.value.trabajadores
            )
            val finalExtras = _state.value.rawExtras + extrasEditados

            val payload = mapOf(
                "Usuario" to mapOf(
                    "idUsuario" to (idUsuario ?: ""),
                    "Nombre" to _state.value.nombre,
                    "Apellido1" to _state.value.apellido1,
                    "Apellido2" to _state.value.apellido2,
                    "Telefono" to _state.value.telefono,
                    "Curp" to _state.value.curp,
                    "Rfc" to _state.value.rfc,
                    "Ine" to _state.value.ine,
                    "FechaNacimiento" to _state.value.fechaNacimiento,
                    "Domicilio" to mapOf(
                        "Calle" to _state.value.calle,
                        "Colonia" to _state.value.colonia,
                        "Municipio" to _state.value.municipio,
                        "Ciudad" to _state.value.ciudad,
                        "Estado" to _state.value.estadoDir,
                        "CodigoPostal" to _state.value.codigoPostal,
                        "Referencia" to _state.value.referencia
                    ),
                    "Parcela" to parcelasDto
                ),
                "CamposExtra" to finalExtras
            )

            val result = authRepository.updateProfile(idRegistro!!, payload)

            result.fold(
                onSuccess = {
                    _state.update { it.copy(isSaving = false, successMessage = "Información actualizada correctamente") }
                },
                onFailure = { e ->
                    _state.update { it.copy(isSaving = false, error = e.message) }
                }
            )
        }
    }

    fun clearMessages() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}