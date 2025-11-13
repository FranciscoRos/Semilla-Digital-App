package com.semilladigital.courses.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CourseDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("Titulo")
    val titulo: String,

    @SerializedName("Descripcion")
    val descripcion: String,

    @SerializedName("Detalles")
    val detalles: String?,

    @SerializedName("Tema")
    val tema: String?, // General, Agricultura, etc.

    @SerializedName("Modalidad")
    val modalidad: String,

    @SerializedName("FechaCurso")
    val fechaCurso: String,

    @SerializedName("DireccionUbicacion")
    val direccion: String?,

    @SerializedName("Latitud")
    val lat: Double?,

    @SerializedName("Longitud")
    val longitud: Double?,

    @SerializedName("Url")
    val url: String?
)