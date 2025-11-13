package com.semilladigital.app.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp // <-- Asegúrate de importar dp
//Esta es la plantilla de una página general de la app
//Consta de titulo y cuerpo
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemillaScreen(
    title: String,
    isGreenBar: Boolean = false,
    onNavigateBack: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    // 2. Decide qué colores usar
    val topBarColors = if (isGreenBar) {
        // Colores para la barra VERDE
        TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary, // Verde
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    } else {
        // Colores por defecto para la barra BLANCA
        TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface, // Blanco
            titleContentColor = MaterialTheme.colorScheme.onSurface, // Oscuro
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    Scaffold(
        topBar = {
            // --- MODIFICACIÓN AQUÍ ---
            // Envolvemos la TopBar y el Divisor en una Columna
            // para que ambos se traten como la barra superior.
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(title) },// Si onNavigateBack no es nulo, muestra la flecha
                    navigationIcon = {
                        if (onNavigateBack != null) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Volver"
                                )
                            }
                        }
                    },
                    actions = {
                        if (onNotificationClick != null) {
                            IconButton(onClick = onNotificationClick) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notificaciones"
                                )
                            }
                        }
                    },
                    colors = topBarColors
                )

                // --- ESTA ES LA "SOMBRA" / LÍNEA ---
                // Añadimos el divisor que se ve en tu imagen.
                Divider(
                    thickness = 1.dp,
                    // Un color sutil, como el borde de un outline
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
            }
        }
    ) { paddingValues ->
        // El 'paddingValues' que recibe el content ahora incluye
        // correctamente el espacio de la TopBar Y el del Divider.
        content(paddingValues)
    }
}