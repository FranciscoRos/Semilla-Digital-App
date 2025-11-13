package com.semilladigital.auth.data.remote

import com.semilladigital.auth.data.remote.dto.LoginRequestDto
import com.semilladigital.auth.data.remote.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto

    // Aquí añadiremos el @POST("usuario") para el registro después
}