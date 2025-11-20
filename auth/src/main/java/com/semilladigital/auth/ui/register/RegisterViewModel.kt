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
    // Campos del formulario
    val fullName: String = "",
    val curp: String = "",
    val email: String = "",
    val phone: String = "",
    val municipality: String = "",
    val address: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    // Estado de la UI
    val mapDrawn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistered: Boolean = false,

    // Datos para dropdowns
    val availableMunicipalities: List<String> = listOf(
        "Chetumal", "Playa del Carmen", "Cancún", "Cozumel", "Isla Mujeres", "Bacalar", "Tulum"
    )
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    // Funciones de cambio de texto
    fun onFullNameChange(text: String) { _state.update { it.copy(fullName = text) } }
    fun onCurpChange(text: String) { _state.update { it.copy(curp = text) } }
    fun onEmailChange(text: String) { _state.update { it.copy(email = text) } }
    fun onPhoneChange(text: String) { _state.update { it.copy(phone = text) } }
    fun onMunicipalityChange(text: String) { _state.update { it.copy(municipality = text) } }
    fun onAddressChange(text: String) { _state.update { it.copy(address = text) } }
    fun onPasswordChange(text: String) { _state.update { it.copy(password = text) } }
    fun onConfirmPasswordChange(text: String) { _state.update { it.copy(confirmPassword = text) } }

    // Simular dibujo de mapa
    fun onMapDrawn() { _state.update { it.copy(mapDrawn = true) } }

    fun onRegisterClick() {
        val currentState = _state.value

        // Validaciones básicas
        if (currentState.password != currentState.confirmPassword) {
            _state.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }
        if (!currentState.mapDrawn) {
            _state.update { it.copy(error = "Debes dibujar el polígono de tu parcela") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = repository.register(
                nombreCompleto = currentState.fullName,
                correo = currentState.email,
                contrasena = currentState.password,
                curp = currentState.curp,
                localidad = "${currentState.address}, ${currentState.municipality}",
                // Por ahora hardcodeamos el cultivo o lo añadimos al form luego
                tipoCultivo = "Desconocido"
            )

            result.fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false, isRegistered = true) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Error desconocido") }
                }
            )
        }
    }
}