package com.semilladigital.courses.domain.model

data class Course(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val detalles: String?, // <-- AÑADIDO
    val tema: String?, // <-- AÑADIDO
    val modalidad: String,
    val fechaCurso: String,
    val direccion: String?,
    val url: String?,
    val lat: Double?,
    val longitud: Double?
)