package com.semilladigital.auth.data.remote

import com.semilladigital.auth.data.remote.dto.LoginRequestDto
import com.semilladigital.auth.data.remote.dto.LoginResponseDto
import com.semilladigital.auth.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("registro")
    @JvmSuppressWildcards
    suspend fun register(@Body request: Map<String, Any>): Any

    @GET("me")
    suspend fun getMe(): UserDto

    @POST("logout")
    suspend fun logout(): Any

    @PUT("registro/{id}")
    @JvmSuppressWildcards
    suspend fun updateRegistro(
        @Path("id") idRegistro: String,
        @Body request: Map<String, Any>
    ): Any
}