package com.semilladigital.dashboard.domain.repository

import com.semilladigital.app.core.data.storage.SessionStorage
import com.semilladigital.dashboard.data.remote.DashboardApiService
import com.semilladigital.dashboard.data.remote.dto.ParaTiRequest
import com.semilladigital.dashboard.data.remote.dto.ParaTiResponse
import com.semilladigital.dashboard.data.remote.dto.UsoRequest
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    private val apiService: DashboardApiService,
    private val sessionStorage: SessionStorage
) {
    suspend fun getNovedades(): Result<ParaTiResponse> {
        return try {
            val actividades = sessionStorage.getActividades()
            val usosRequest = actividades.map { UsoRequest(it) }
            val request = ParaTiRequest(usos = usosRequest)

            val response = apiService.getNovedadesParaTi(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}