package com.semilladigital.dashboard.data.remote

import com.semilladigital.dashboard.data.remote.dto.ParaTiRequest
import com.semilladigital.dashboard.data.remote.dto.ParaTiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface DashboardApiService {
    @POST("paraTi")
    suspend fun getNovedadesParaTi(@Body request: ParaTiRequest): ParaTiResponse
}