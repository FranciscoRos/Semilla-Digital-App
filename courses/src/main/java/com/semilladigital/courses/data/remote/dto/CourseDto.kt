package com.semilladigital.courses.data.remote.dto

import com.google.gson.annotations.SerializedName

// Este DTO ahora coincide EXACTAMENTE con tu JSON
data class CourseDto(
    @SerializedName("id") // Corregido de "_id" a "id"
    val id: String,

    @SerializedName("Titulo")
    val titulo: String,

    @SerializedName("Descripcion")
    val descripcion: String,

    @SerializedName("Modalidad")
    val modalidad: String,

    @SerializedName("FechaCurso")
    val fechaCurso: String,

    @SerializedName("DireccionUbicacion") // Campo nuevo
    val direccion: String?,

    @SerializedName("Latitud")
    val lat: Double?,

    @SerializedName("Longitud")
    val longitud: Double?,

    @SerializedName("Url") // Corregido de "URL" a "Url"
    val url: String?

    // Los campos "Creado" y "Actualizado" no los necesitamos
    // para la UI, así que los podemos ignorar.
)