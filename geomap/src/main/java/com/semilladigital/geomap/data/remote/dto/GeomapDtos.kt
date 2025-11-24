package com.semilladigital.geomap.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.semilladigital.geomap.domain.model.Coordenada
import com.semilladigital.geomap.domain.model.Parcela
import com.semilladigital.geomap.domain.model.Ubicacion

// --- DTOs (Reflejan el JSON) ---

data class ParcelasResponseDto(val data: List<ParcelaDto>)
data class UbicacionesResponseDto(val data: List<UbicacionEspecialDto>)

data class ParcelaDto(
    val id: String,
    val nombre: String,
    val municipio: String,
    val localidad: String,
    @SerializedName("coordenadas") val coordenadas: List<Map<String, Double>>,
    @SerializedName("usos") val usos: List<UsoParcelaDto>
)

data class UsoParcelaDto(
    val area: String,
    val actividadesEspecificas: List<String>
)

data class UbicacionEspecialDto(
    val id: String,
    val nombre: String,
    val municipio: String,
    val tipo: String,
    val descripcion: String,
    val telefono: String?,
    val direccion: String?,
    @SerializedName("coordenadas") val coordenadas: CoordenadaSimpleDto,
    val institucion: String?
)

data class CoordenadaSimpleDto(
    @SerializedName("latitud") val lat: Double,
    @SerializedName("longitud") val lng: Double
)

// --- MAPPERS (Extension Functions para convertir DTO -> Domain) ---

fun ParcelaDto.toDomain(): Parcela {
    // Aplanar las actividades de todos los usos en una sola lista
    val allActivities = usos.flatMap { it.actividadesEspecificas }

    // Convertir coordenadas
    val domainCoords = coordenadas.mapNotNull {
        val lat = it["lat"] ?: it["latitud"]
        val lng = it["lng"] ?: it["longitud"]
        if (lat != null && lng != null) Coordenada(lat, lng) else null
    }

    return Parcela(
        id = id,
        nombre = nombre,
        municipio = municipio,
        localidad = localidad,
        coordenadas = domainCoords,
        actividades = allActivities
    )
}

fun UbicacionEspecialDto.toDomain(): Ubicacion {
    return Ubicacion(
        id = id,
        nombre = nombre,
        municipio = municipio,
        tipo = tipo,
        descripcion = descripcion,
        telefono = telefono,
        direccion = direccion,
        coordenada = Coordenada(coordenadas.lat, coordenadas.lng),
        institucion = institucion
    )
}