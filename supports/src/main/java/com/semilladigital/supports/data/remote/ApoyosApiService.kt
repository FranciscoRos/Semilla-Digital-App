package com.semilladigital.supports.data.remote

import com.semilladigital.supports.domain.model.ApoyosResponse
import retrofit2.http.GET

interface ApoyosApiService {
    @GET("apoyo")
    suspend fun getApoyos(): ApoyosResponse
}