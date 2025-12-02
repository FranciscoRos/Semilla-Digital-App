package com.semilladigital.forum.data.remote.dto

import com.google.gson.annotations.SerializedName

// 1. La clase para envolver la respuesta "data"


// 2. Los DTOs corregidos según tu JSON
data class CategoriaDto(
    val id: String,
    @SerializedName("Nombre") val titulo: String, // Mapeamos "Nombre" del JSON a "titulo"
    @SerializedName("Descripcion") val descripcion: String,
    @SerializedName("Icono") val icono: String,
    @SerializedName("SubCategorias") val subtemas: List<SubtemaDto>
)

data class SubtemaDto(
    @SerializedName("idSub") val id: String,
    @SerializedName("Nombre") val titulo: String,
    @SerializedName("CantidadTemas") val temasCount: Int
)

data class TemaRecienteDto(
    val id: String,
    @SerializedName("Titulo") val titulo: String,
    @SerializedName("Autor") val autor: AutorDto?, // Puede ser nulo
    @SerializedName("Ubicacion") val ubicacion: String?,
    @SerializedName("Creado") val fecha: String?,
    val categoria: String?,
    @SerializedName("Comentarios") val respuestasCount: Int,
    @SerializedName("Vistas") val vistasCount: Int
)

data class AutorDto(
    val idUsuario: String,
    @SerializedName("Nombre") val nombre: String?,
    @SerializedName("Apellido1") val apellido1: String?,
    @SerializedName("Apellido2") val apellido2: String?
)

data class TemaDetalleDto(
    val id: String,
    @SerializedName("Titulo") val titulo: String,
    @SerializedName("Detalles") val contenido: String, // O "Descripcion" segun JSON
    @SerializedName("Autor") val autor: AutorDto?,
    @SerializedName("Creado") val fecha: String?
)

data class ComentarioDto(
    val id: String,
    @SerializedName("Autor") val autor: AutorDto?, // Si el comentario tiene objeto autor
    @SerializedName("Contenido") val contenido: String,
    @SerializedName("Fecha") val fecha: String?
)