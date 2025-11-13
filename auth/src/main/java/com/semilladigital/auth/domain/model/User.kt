package com.semilladigital.auth.domain.model

// Modelo "limpio" del usuario para la app
data class User(
    val id: String,
    val nombre: String,
    val correo: String,
    val nombreRol: String,
    val estatus: String
)