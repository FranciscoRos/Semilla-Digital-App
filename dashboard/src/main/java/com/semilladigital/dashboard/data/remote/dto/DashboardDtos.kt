package com.semilladigital.dashboard.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ParaTiRequest(
    @SerializedName("Usos")
    val usos: List<UsoRequest>
)

data class UsoRequest(
    @SerializedName("UsoGeneral")
    val usoGeneral: String
)

data class ParaTiResponse(
    @SerializedName("Cursos")
    val cursos: List<CursoDashboardDto>,
    @SerializedName("Apoyos")
    val apoyos: List<ApoyoDashboardDto>
)

data class CursoDashboardDto(
    val id: String,
    val Titulo: String,
    val Descripcion: String,
    val Creado: String
)

data class ApoyoDashboardDto(
    val id: String,
    val nombre_programa: String,
    val descripcion: String,
    val Creado: String
)