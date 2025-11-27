package com.semilladigital.supports.domain.model

import com.google.gson.annotations.SerializedName

data class HistorialApoyoRequest(
    @SerializedName("parcelaId")
    val parcelaId: String
)

data class HistorialApoyoResponse(
    val success: Boolean,
    val message: String
)