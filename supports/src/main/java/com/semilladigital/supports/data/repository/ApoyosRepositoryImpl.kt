package com.semilladigital.supports.data.repository

import com.semilladigital.supports.data.remote.ApoyosApiService
import com.semilladigital.supports.domain.model.Apoyo
import com.semilladigital.supports.domain.repository.ApoyosRepository
import javax.inject.Inject

class ApoyosRepositoryImpl @Inject constructor(
    private val apiService: ApoyosApiService
) : ApoyosRepository {
    override suspend fun getAllApoyos(): Result<List<Apoyo>> {
        return try {
            val response = apiService.getApoyos()
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}