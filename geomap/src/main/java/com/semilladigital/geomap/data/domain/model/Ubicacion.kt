package com.semilladigital.geomap.domain.model

data class Ubicacion(
    val id: String,
    val nombre: String,
    val municipio: String,
    val tipo: String,
    val descripcion: String,
    val telefono: String?,
    val direccion: String?,
    val coordenada: Coordenada,
    val institucion: String?
)