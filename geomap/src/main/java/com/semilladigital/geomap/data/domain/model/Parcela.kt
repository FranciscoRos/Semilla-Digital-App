package com.semilladigital.geomap.domain.model

data class Parcela(
    val id: String,
    val nombre: String,
    val municipio: String,
    val localidad: String,
    // Usamos una estructura de datos simple para coordenadas en dominio
    val coordenadas: List<Coordenada>,
    val actividades: List<String> // Lista plana de actividades para la UI
)

data class Coordenada(
    val lat: Double,
    val lng: Double
)