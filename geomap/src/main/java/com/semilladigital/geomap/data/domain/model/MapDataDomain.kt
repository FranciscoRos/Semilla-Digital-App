package com.semilladigital.geomap.domain.model

// Un contenedor para devolver ambos conjuntos de datos
data class MapDataDomain(
    val parcelas: List<Parcela>,
    val ubicaciones: List<Ubicacion>
)