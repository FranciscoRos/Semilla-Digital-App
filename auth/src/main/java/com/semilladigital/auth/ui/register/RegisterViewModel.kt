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

data class RegisterState(
    val nombreCompleto: String = "",
    val curp: String = "",
    val correo: String = "",
    val telefono: String = "",

    val municipio: String = "",
    val direccion: String = "",

    val contrasena: String = "",
    val confirmarContrasena: String = "",

    val coordenadasParcela: List<List<Double>> = emptyList(),

    val dynamicAnswers: Map<String, Any> = emptyMap(),

    val activeSection: String = "Información Personal",
    val mapDrawn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistered: Boolean = false,
    val isShowingMap: Boolean = false,

    val availableMunicipalities: List<String> = listOf(
        "Chetumal", "Playa del Carmen", "Cancún", "Cozumel",
        "Isla Mujeres", "Bacalar", "Tulum", "Puerto Morelos"
    )
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onNombreCompletoChange(v: String) { _state.update { it.copy(nombreCompleto = v) } }
    fun onCurpChange(v: String) { _state.update { it.copy(curp = v) } }
    fun onCorreoChange(v: String) { _state.update { it.copy(correo = v) } }
    fun onTelefonoChange(v: String) { _state.update { it.copy(telefono = v) } }

    fun onMunicipioChange(v: String) { _state.update { it.copy(municipio = v) } }
    fun onDireccionChange(v: String) { _state.update { it.copy(direccion = v) } }

    fun onContrasenaChange(v: String) { _state.update { it.copy(contrasena = v) } }
    fun onConfirmarContrasenaChange(v: String) { _state.update { it.copy(confirmarContrasena = v) } }

    fun onDynamicAnswerChange(fieldName: String, value: Any) {
        _state.update { currentState ->
            val newAnswers = currentState.dynamicAnswers.toMutableMap()
            newAnswers[fieldName] = value

            // Limpieza de campos dependientes si cambia el tipo de producción
            if (fieldName == "tipoProduccion") {
                newAnswers.remove("actividadesAgricola")
                newAnswers.remove("actividadesGanadera")
                newAnswers.remove("actividadesPesca")
                newAnswers.remove("actividadesApicultura")
            }

            currentState.copy(dynamicAnswers = newAnswers)
        }
    }

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
                nombreCompleto = "Jose8",
                curp = "JOSE04932MNC845GDF",
                correo = "Jose8${System.currentTimeMillis()}@gmail.com", // Random para no repetir
                telefono = "9830000000",
                municipio = "Othón P. Blanco",
                direccion = "Av. Independencia, Centro",
                contrasena = "ContrasenaNueva8",
                confirmarContrasena = "ContrasenaNueva8",
                mapDrawn = true,
                coordenadasParcela = listOf(
                    listOf(18.503, -88.303),
                    listOf(18.504, -88.304)
                ),
                dynamicAnswers = mapOf(
                    "tipoProduccion" to "agricola",
                    "actividadesAgricola" to listOf("tomate", "chile"), // values internos
                    "anosExperiencia" to 5,
                    "tieneRiego" to "si",
                    "fuenteAgua" to "Pozo",
                    "tipoMaquinaria" to listOf("tractor"),
                    "trabajadores" to 3,
                    "ventaProductos" to "local",
                    "apoyosGubernamentales" to "no"
                )
            )
        }
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            val s = _state.value
            _state.update { it.copy(isLoading = true, error = null) }

            if (s.contrasena != s.confirmarContrasena) {
                _state.update { it.copy(isLoading = false, error = "Las contraseñas no coinciden") }
                return@launch
            }

            try {
                // 1. Obtener etiquetas legibles (Labels) en lugar de values internos
                // Buscamos en el SCHEMA la opción que coincida con el valor seleccionado

                val tipoUsoValue = s.dynamicAnswers["tipoProduccion"] as? String ?: ""

                // Buscar el Label del Tipo de Producción (ej: "agricola" -> "Agrícola")
                val tipoUsoLabel = QUESTION_SCHEMA
                    .find { it.fieldName == "tipoProduccion" }
                    ?.options
                    ?.find { it.value == tipoUsoValue }
                    ?.label ?: "Desconocido"

                // Determinar qué lista de actividades usar y buscar sus Labels
                val (campoActividades, listaValoresActividades) = when (tipoUsoValue) {
                    "agricola" -> "actividadesAgricola" to (s.dynamicAnswers["actividadesAgricola"] as? List<*> ?: emptyList<Any>())
                    "ganadera" -> "actividadesGanadera" to (s.dynamicAnswers["actividadesGanadera"] as? List<*> ?: emptyList<Any>())
                    "pesca" -> "actividadesPesca" to (s.dynamicAnswers["actividadesPesca"] as? List<*> ?: emptyList<Any>())
                    "apicultura" -> "actividadesApicultura" to (s.dynamicAnswers["actividadesApicultura"] as? List<*> ?: emptyList<Any>())
                    else -> "" to emptyList()
                }

                // Convertir valores de actividades a Labels (ej: "maiz" -> "Siembra de Maíz")
                val questionActividades = QUESTION_SCHEMA.find { it.fieldName == campoActividades }
                val actividadesLabels = listaValoresActividades.map { valor ->
                    questionActividades?.options?.find { it.value == valor.toString() }?.label ?: valor.toString()
                }

                // 2. Construir el Payload igual al Postman
                val payload = mutableMapOf<String, Any>()

                // Coordenadas
                val coordenadasFormateadas = s.coordenadasParcela.map { punto: List<Double> ->
                    mapOf(
                        "lat" to (punto.getOrNull(0) ?: 0.0),
                        "lng" to (punto.getOrNull(1) ?: 0.0)
                    )
                }

                // Estructura de usos interna de Parcela (Usando LABELS)
                val usosFormateados = listOf(
                    mapOf(
                        "area" to tipoUsoLabel, // Envía "Agrícola" en vez de "agricola"
                        "actividadesEspecificas" to actividadesLabels // Envía ["Cultivo de tomate"]
                    )
                )

                payload["Usuario"] = mapOf(
                    "Nombre" to s.nombreCompleto,
                    "Apellido1" to "Poot8", // Hardcodeado según tu ejemplo Postman para probar
                    "Apellido2" to "Fiona8", // Hardcodeado según tu ejemplo Postman
                    "Curp" to s.curp,
                    "Correo" to s.correo,
                    "Contrasena" to s.contrasena,
                    "Telefono" to s.telefono,
                    "FechaNacimiento" to "2000-01-01",
                    "Ine" to "Informcaion de ine",
                    "Rfc" to "JO029384FJVN",

                    "Domicilio" to mapOf(
                        "Calle" to s.direccion,
                        "Colonia" to "Centro",
                        "Municipio" to s.municipio,
                        "Ciudad" to "Chetumal",
                        "Estado" to "Quintana Roo",
                        "CodigoPostal" to "11111",
                        "Referencia" to "Casa color blanco"
                    ),

                    "Parcela" to listOf(
                        mapOf(
                            "ciudad" to "Chetumal",
                            "municipio" to s.municipio,
                            "localidad" to "Calderitas",
                            "direccionAdicional" to "Camino de terracería",
                            "coordenadas" to coordenadasFormateadas,
                            "area" to 1500.75,
                            "nombre" to "Parcela Principal",
                            "usos" to usosFormateados
                        )
                    )
                )

                // Agregar campos dinámicos a la raíz del JSON
                // NOTA: Aquí enviamos los values crudos a la raíz como "CamposExtra" si el backend los pide así,
                // pero ya inyectamos los bonitos dentro de Usuario.Parcela.usos
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