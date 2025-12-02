package com.semilladigital.forum.domain.repository

import com.semilladigital.forum.domain.model.Categoria
import com.semilladigital.forum.domain.model.Comentario
import com.semilladigital.forum.domain.model.TemaDetalle
import com.semilladigital.forum.domain.model.TemaReciente
import com.semilladigital.forum.domain.model.*

interface ForoRepository {
    suspend fun getCategorias(): Result<List<Categoria>>
    suspend fun getTemasRecientes(): Result<List<TemaReciente>>
    suspend fun getTemaDetalle(temaId: String): Result<TemaDetalle>
    suspend fun getComentarios(temaId: String): Result<List<Comentario>>
}