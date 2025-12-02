package com.semilladigital.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("Correo")
    val correo: String,

    @SerializedName("Contrasena")
    val contrasena: String,

    @SerializedName("Tipo")
    val tipo: String = "Usuario"
)