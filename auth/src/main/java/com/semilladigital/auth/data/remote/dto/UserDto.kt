package com.semilladigital.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("Nombre")
    val nombre: String?,

    @SerializedName("Apellido1")
    val apellido1: String?,

    @SerializedName("Apellido2")
    val apellido2: String?,

    @SerializedName("Correo")
    val correo: String?,

    @SerializedName("Rol")
    val rol: List<RolDto>?,

    @SerializedName("Estatus")
    val estatus: String?,

    @SerializedName("Actividades")
    val actividades: List<String>?,

)

data class RolDto(
    @SerializedName("idRol")
    val idRol: String?,

    @SerializedName("NombreRol")
    val nombreRol: String?
)