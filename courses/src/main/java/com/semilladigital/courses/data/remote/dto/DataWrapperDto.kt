package com.semilladigital.courses.data.remote.dto

import com.google.gson.annotations.SerializedName

// Este DTO genérico representa la respuesta {"data": [...]}
data class DataWrapperDto<T>(
    @SerializedName("data")
    val data: T
)