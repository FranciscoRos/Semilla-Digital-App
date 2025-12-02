package com.semilladigital.forum.domain.model

data class Categoria(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val icono: String,
    val subtemas: List<Subtema>
)

data class Subtema(
    val id: String,
    val titulo: String,
    val temasCount: Int
)