package com.semilladigital.auth.data.remote

import com.semilladigital.auth.data.remote.dto.LoginRequestDto
import com.semilladigital.auth.data.remote.dto.LoginResponseDto
import com.semilladigital.auth.data.remote.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto

    @POST("registro")
    @JvmSuppressWildcards // Necesario para Map<String, Any>
    suspend fun register(@Body request: Map<String, Any>): Any
}
