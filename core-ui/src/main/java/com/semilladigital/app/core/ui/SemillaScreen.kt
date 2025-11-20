package com.semilladigital.app.core.ui // O el paquete que estés usando

import android.app.Activity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemillaScreen(
    title: String,
    isGreenBar: Boolean = false,
    onNavigateBack: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val topBarColors = if (isGreenBar) {
        TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary, // Verde
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White, // <-- AÑADIDO
            actionIconContentColor = Color.White
        )
    } else {
        TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface, // Blanco
            titleContentColor = MaterialTheme.colorScheme.onSurface, // Oscuro
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface, // <-- AÑADIDO
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
    // Este bloque le dice al sistema qué color de íconos (claros u oscuros) usar
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // isAppearanceLightStatusBars = true  -> Íconos OSCUROS
            // isAppearanceLightStatusBars = false -> Íconos CLAROS

            // Si la barra NO es verde (es blanca), usa íconos oscuros
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isGreenBar
        }
    }
    // --- FIN DE LA CORRECCIÓN ---

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title) },
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
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}