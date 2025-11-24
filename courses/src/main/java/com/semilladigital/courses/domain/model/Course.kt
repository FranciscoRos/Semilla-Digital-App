package com.semilladigital.courses.domain.model

data class Course(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val detalles: String?,
    val tema: String?,
    val modalidad: String,
    val fechaCurso: List<String>,
    val direccion: String?,
    val lat: Double?,
    val longitud: Double?,
    val url: String?
)