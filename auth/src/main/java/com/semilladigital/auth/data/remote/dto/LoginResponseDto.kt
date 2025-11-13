package com.semilladigital.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginResponseDto(
    @SerializedName("usuario")
    val usuario: UserDto,

    @SerializedName("token")
    val token: String
)