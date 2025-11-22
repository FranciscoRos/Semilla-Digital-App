package com.semilladigital.auth.domain.model


data class User(
    val id: String,
    val nombre: String,
    val apellido1: String,
    val apellido2: String,
    val correo: String,
    val estatus: String,
    // Lista de usos ya procesada en el dominio
    val usos: List<UsoItem> = emptyList()
)

data class UsoItem(
    val usoGeneral: String,
    val usosEspecificos: List<String>
)