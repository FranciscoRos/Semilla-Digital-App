package com.semilladigital.forum.data.repository

import com.semilladigital.forum.data.remote.ForoApiService
import com.semilladigital.forum.data.remote.dto.*
import com.semilladigital.forum.domain.model.*
import com.semilladigital.forum.domain.repository.ForoRepository
import javax.inject.Inject

class ForoRepositoryImpl @Inject constructor(
    private val apiService: ForoApiService
) : ForoRepository {

    override suspend fun getCategorias(): Result<List<Categoria>> {
        return try {
            val response = apiService.getCategorias()
            // Accedemos a .data
            Result.success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getTemasRecientes(): Result<List<TemaReciente>> {
        return try {
            val response = apiService.getTemasRecientes()
            Result.success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getTemaDetalle(temaId: String): Result<TemaDetalle> {
        return try {
            val dto = apiService.getTemaDetalle(temaId)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getComentarios(temaId: String): Result<List<Comentario>> {
        return try {
            val response = apiService.getComentarios(temaId)
            Result.success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}

// --- MAPPERS CORREGIDOS ---

fun CategoriaDto.toDomain(): Categoria {
    return Categoria(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        icono = icono,
        subtemas = subtemas.map { it.toDomain() }
    )
}

fun SubtemaDto.toDomain(): Subtema {
    return Subtema(
        id = id,
        titulo = titulo,
        temasCount = temasCount
    )
}

fun TemaRecienteDto.toDomain(): TemaReciente {
    // Aquí concatenamos los nombres del AutorDto
    val nombreCompleto = "${autor?.nombre ?: "Usuario"} ${autor?.apellido1 ?: ""}".trim()

    return TemaReciente(
        id = id,
        titulo = titulo,
        autor = if (nombreCompleto.isBlank()) "Anónimo" else nombreCompleto,
        ubicacion = ubicacion ?: "Sin ubicación",
        fecha = fecha?.take(10) ?: "",
        categoria = categoria ?: "General",
        respuestasCount = respuestasCount,
        vistasCount = vistasCount
    )
}

fun TemaDetalleDto.toDomain(): TemaDetalle {
    val nombreAutor = autor?.nombre ?: "Usuario"
    return TemaDetalle(
        id = id,
        titulo = titulo,
        contenido = contenido,
        autor = nombreAutor,
        rolAutor = "Miembro",
        fecha = fecha?.take(10) ?: ""
    )
}

fun ComentarioDto.toDomain(): Comentario {
    val nombreAutor = autor?.nombre ?: "Usuario"
    return Comentario(
        id = id,
        autor = nombreAutor,
        fecha = fecha?.take(10) ?: "",
        contenido = contenido,
        inicialAutor = nombreAutor.take(1).uppercase()
    )
}