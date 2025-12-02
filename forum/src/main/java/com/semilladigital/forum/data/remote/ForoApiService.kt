package com.semilladigital.forum.data.remote

import com.semilladigital.forum.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Query

interface ForoApiService {
    @GET("categorias")
    suspend fun getCategorias(): BaseResponse<List<CategoriaDto>>

    @GET("temas")
    suspend fun getTemasRecientes(): BaseResponse<List<TemaRecienteDto>>

    // Si el detalle viene directo (sin "data"), se queda así:
    @GET("temas")
    suspend fun getTemaDetalle(@Query("id") temaId: String): TemaDetalleDto

    // Asumiendo que los comentarios vienen en lista envuelta
    @GET("comentarios")
    suspend fun getComentarios(@Query("temaId") temaId: String): BaseResponse<List<ComentarioDto>>
}