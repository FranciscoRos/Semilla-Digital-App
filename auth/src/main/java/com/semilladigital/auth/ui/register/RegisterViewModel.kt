package com.semilladigital.auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- AQUÍ SE DEFINE EL ESTADO (SOLO AQUÍ) ---
data class RegisterState(
    // Información Básica
    val nombre: String = "",
    val apellido1: String = "",
    val apellido2: String = "",
    val curp: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val telefono: String = "",
    val fechaNacimiento: String = "",
    val ine: String = "",
    val rfc: String = "",

    // Domicilio
    val calle: String = "",
    val colonia: String = "",
    val municipio: String = "",
    val ciudad: String = "",
    val estado: String = "Quintana Roo",
    val cp: String = "",
    val referencia: String = "",

    // Datos de Parcela
    val coordenadasParcela: List<List<Double>> = emptyList(),

    // Datos Dinámicos
    val dynamicAnswers: Map<String, Any> = emptyMap(),

    // UI State
    val activeSection: String = "Información Básica",
    val mapDrawn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistered: Boolean = false,

    val isShowingMap: Boolean = false,

    // Listas para Dropdowns
    val availableMunicipalities: List<String> = listOf(
        "Othón P. Blanco", "Bacalar", "Benito Juárez", "Cozumel",
        "Isla Mujeres", "Solidaridad", "Tulum", "Lázaro Cárdenas",
        "José María Morelos", "Felipe Carrillo Puerto", "Puerto Morelos"
    )
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    // Setters Información Básica
    fun onNombreChange(v: String) { _state.update { it.copy(nombre = v) } }
    fun onApellido1Change(v: String) { _state.update { it.copy(apellido1 = v) } }
    fun onApellido2Change(v: String) { _state.update { it.copy(apellido2 = v) } }
    fun onCurpChange(v: String) { _state.update { it.copy(curp = v) } }
    fun onCorreoChange(v: String) { _state.update { it.copy(correo = v) } }
    fun onContrasenaChange(v: String) { _state.update { it.copy(contrasena = v) } }
    fun onTelefonoChange(v: String) { _state.update { it.copy(telefono = v) } }
    fun onFechaNacimientoChange(v: String) { _state.update { it.copy(fechaNacimiento = v) } }
    fun onIneChange(v: String) { _state.update { it.copy(ine = v) } }
    fun onRfcChange(v: String) { _state.update { it.copy(rfc = v) } }

    // Setters Domicilio
    fun onCalleChange(v: String) { _state.update { it.copy(calle = v) } }
    fun onColoniaChange(v: String) { _state.update { it.copy(colonia = v) } }
    fun onMunicipioChange(v: String) { _state.update { it.copy(municipio = v) } }
    fun onCiudadChange(v: String) { _state.update { it.copy(ciudad = v) } }
    fun onEstadoChange(v: String) { _state.update { it.copy(estado = v) } }
    fun onCpChange(v: String) { _state.update { it.copy(cp = v) } }
    fun onReferenciaChange(v: String) { _state.update { it.copy(referencia = v) } }

    // Setters Dinámicos
    fun onDynamicAnswerChange(fieldName: String, value: Any) {
        _state.update {
            val newAnswers = it.dynamicAnswers.toMutableMap()
            newAnswers[fieldName] = value
            it.copy(dynamicAnswers = newAnswers)
        }
    }

    // UI Actions
    fun toggleSection(section: String) {
        _state.update {
            it.copy(activeSection = if (it.activeSection == section) "" else section)
        }
    }

    fun onOpenMap() { _state.update { it.copy(isShowingMap = true) } }
    fun onCloseMap() { _state.update { it.copy(isShowingMap = false) } }

    fun onPolygonSaved(puntos: List<List<Double>>) {
        _state.update {
            it.copy(
                coordenadasParcela = puntos,
                mapDrawn = true,
                isShowingMap = false
            )
        }
    }

    fun onMapDrawn() {
        _state.update { it.copy(mapDrawn = true) }
    }

    fun fillWithDummyData() {
        _state.update {
            it.copy(
                nombre = "Prueba",
                apellido1 = "De",
                apellido2 = "Concepto",
                curp = "ABCD800101HDFRXX05",
                correo = "prueba${System.currentTimeMillis()}@test.com",
                contrasena = "password123",
                telefono = "9981234567",
                fechaNacimiento = "1990-01-01",
                ine = "1234567890123",
                rfc = "ABCD800101XXX",
                calle = "Av. Insurgentes 123",
                colonia = "Centro",
                municipio = "Chetumal",
                ciudad = "Chetumal",
                estado = "Quintana Roo",
                cp = "77000",
                referencia = "Casa verde",
                mapDrawn = true,
                coordenadasParcela = listOf(
                    listOf(18.500, -88.300),
                    listOf(18.501, -88.301),
                    listOf(18.500, -88.301),
                    listOf(18.500, -88.300)
                ),
                dynamicAnswers = mapOf(
                    "tipoProduccion" to "agricola",
                    "anosExperiencia" to 5,
                    "cultivos" to listOf("maiz", "frijol"),
                    "tieneRiego" to "si",
                    "fuenteAgua" to "Pozo",
                    "tipoMaquinaria" to listOf("tractor"),
                    "usaPesticidas" to "no",
                    "certificacionOrganica" to "en_proceso",
                    "trabajadores" to 3,
                    "ventaProductos" to "local",
                    "apoyosGubernamentales" to "no"
                )
            )
        }
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val s = _state.value
                val payload = mutableMapOf<String, Any>()

                val coordenadasFormateadas = s.coordenadasParcela.map { punto: List<Double> ->
                    mapOf(
                        "lat" to (punto.getOrNull(0) ?: 0.0),
                        "lng" to (punto.getOrNull(1) ?: 0.0)
                    )
                }

                val listaCultivos = s.dynamicAnswers["cultivos"] as? List<*>
                val actividades = listaCultivos?.map { it.toString() } ?: emptyList()
                val tipoArea = s.dynamicAnswers["tipoProduccion"] as? String ?: "Desconocido"

                val usosFormateados = listOf(
                    mapOf(
                        "area" to tipoArea,
                        "actividadesEspecificas" to actividades
                    )
                )

                payload["Usuario"] = mapOf(
                    "Nombre" to s.nombre,
                    "Apellido1" to s.apellido1,
                    "Apellido2" to s.apellido2,
                    "Curp" to s.curp,
                    "Correo" to s.correo,
                    "Contrasena" to s.contrasena,
                    "Telefono" to s.telefono,
                    "FechaNacimiento" to s.fechaNacimiento.ifBlank { "2000-01-01" },
                    "Ine" to s.ine,
                    "Rfc" to s.rfc,

                    "Domicilio" to mapOf(
                        "Calle" to s.calle,
                        "Colonia" to s.colonia,
                        "Municipio" to s.municipio,
                        "Ciudad" to s.ciudad,
                        "Estado" to s.estado,
                        "CodigoPostal" to s.cp,
                        "Referencia" to s.referencia
                    ),

                    "Parcela" to listOf(
                        mapOf(
                            "coordenadas" to coordenadasFormateadas,
                            "ciudad" to s.ciudad,
                            "municipio" to s.municipio,
                            "localidad" to s.colonia,
                            "direccionAdicional" to s.calle,
                            "area" to 0.0,
                            "nombre" to "Parcela Principal",
                            "usos" to usosFormateados
                        )
                    )
                )

                payload.putAll(s.dynamicAnswers)

                val result = repository.register(payload)

                result.fold(
                    onSuccess = { _state.update { it.copy(isLoading = false, isRegistered = true) } },
                    onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
                )
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}