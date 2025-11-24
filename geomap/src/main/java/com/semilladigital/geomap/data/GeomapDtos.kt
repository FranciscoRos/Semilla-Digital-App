package com.semilladigital.geomap.data

import com.google.gson.annotations.SerializedName

// --- Wrappers de Respuesta ---
data class ParcelasResponse(val data: List<ParcelaDto>)
data class UbicacionesResponse(val data: List<UbicacionEspecialDto>)

// --- Parcela DTO ---
data class ParcelaDto(
    val id: String,
    val nombre: String,
    val municipio: String,
    val localidad: String,
    @SerializedName("coordenadas") val coordenadas: List<Map<String, Double>>, // Backend envía [{lat:.., lng:..}]
    @SerializedName("usos") val usos: List<UsoParcelaDto>
)

data class UsoParcelaDto(
    val area: String,
    val actividadesEspecificas: List<String>
)

// --- Ubicación Especial DTO ---
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