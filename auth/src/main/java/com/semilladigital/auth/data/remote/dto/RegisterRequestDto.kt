package com.semilladigital.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

// Estructura exacta para PerfilRegistroController
data class RegisterRequestDto(
    @SerializedName("Usuario")
    val usuario: UsuarioRegistroDataDto
)

data class UsuarioRegistroDataDto(
    @SerializedName("Nombre")
    val nombre: String,

    @SerializedName("Apellido1")
    val apellido1: String = "", // La UI pide nombre completo, aquí dividiremos o enviaremos vacío

    @SerializedName("Apellido2")
    val apellido2: String = "",

    @SerializedName("Correo")
    val correo: String,

    @SerializedName("Contrasena")
    val contrasena: String,

    @SerializedName("Curp")
    val curp: String,

    @SerializedName("Telefono")
    val telefono: String = "",

    @SerializedName("Domicilio")
    val domicilio: String, // Usaremos el campo 'Localidad' de la UI aquí

    // Campos requeridos por el backend (enviamos defaults)
    @SerializedName("FechaNacimiento")
    val fechaNacimiento: String = "2000-01-01",

    @SerializedName("Ine")
    val ine: String = "",

    @SerializedName("Rfc")
    val rfc: String = "",

    @SerializedName("Parcela")
    val parcela: List<ParcelaRegistroDto> = emptyList()
)

data class ParcelaRegistroDto(
    @SerializedName("nombre")
    val nombre: String = "Parcela Principal",

    // Coordenadas: Lista de [lat, lng]
    // El controlador espera: 'Coordenadas' => $parcela['coordenadas']
    @SerializedName("coordenadas")
    val coordenadas: List<List<Double>>,

    @SerializedName("ciudad")
    val ciudad: String = "",

    @SerializedName("municipio")
    val municipio: String = "",

    @SerializedName("localidad")
    val localidad: String = "",

    @SerializedName("direccionAdicional")
    val direccionAdicional: String = "",

    @SerializedName("area")
    val area: String = "0",

    @SerializedName("usos")
    val usos: String // Aquí enviaremos el "Tipo de Cultivo"
)