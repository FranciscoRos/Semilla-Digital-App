package com.semilladigital.forum.domain.model

data class TemaReciente(
    val id: String,
    val titulo: String,
    val autor: String,
    val ubicacion: String,
    val fecha: String,
    val categoria: String,
    val respuestasCount: Int,
    val vistasCount: Int
)

data class TemaDetalle(
    val id: String,
    val titulo: String,
    val contenido: String,
    val autor: String,
    val fecha: String,
    val rolAutor: String = "Iniciador del Hilo"
)

data class Comentario(
    val id: String,
    val autor: String,
    val fecha: String,
    val contenido: String,
    val inicialAutor: String
)