package com.semilladigital.courses.domain.model

data class Course(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val modalidad: String,
    val fechaCurso: String,
    val direccion: String?, // Añadido
    val url: String?,
    val lat: Double?,
    val longitud: Double?
)