package com.semilladigital.auth.data.remote

import com.semilladigital.auth.data.remote.dto.LoginRequestDto
import com.semilladigital.auth.data.remote.dto.LoginResponseDto
import com.semilladigital.auth.data.remote.dto.RegisterRequestDto // <-- Importante
import com.semilladigital.auth.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto

    @GET("me")
    suspend fun me(
        @Header("Authorization") token: String
    ): UserDto

    @POST("registro")
    suspend fun register(
        @Body request: RegisterRequestDto
    )
}