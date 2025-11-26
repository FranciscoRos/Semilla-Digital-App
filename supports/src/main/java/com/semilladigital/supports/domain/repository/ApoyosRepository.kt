package com.semilladigital.supports.domain.repository

import com.semilladigital.supports.domain.model.Apoyo

interface ApoyosRepository {
    suspend fun getAllApoyos(): Result<List<Apoyo>>
}