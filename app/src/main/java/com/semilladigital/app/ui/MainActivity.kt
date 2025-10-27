package com.semilladigital.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.semilladigital.app.core.ui.SemillaDigitalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- 1. ACTIVAR MODO EDGE-TO-EDGE ---
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SemillaDigitalTheme {
                Surface(
                    // --- 2. AÑADIR PADDING DE LA BARRA DE ESTADO ---
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.statusBars.asPaddingValues()),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Texto de prueba (con una Columna para ver los ejemplos)
                    Column {
                        // 1. Texto por defecto (ya usa TextPrimary)
                        Text(text = "¡Hola SemillaDigital! (Default)")

                        // 2. Texto usando el color primario
                        Text(
                            text = "Texto con color Primario",
                            color = MaterialTheme.colorScheme.primary
                        )

                        // 3. Texto usando el color secundario
                        Text(
                            text = "Texto con color Secundario",
                            color = MaterialTheme.colorScheme.secondary
                        )

                        // 4. Texto usando un estilo de tipografía
                        Text(
                            text = "Soy un Título",
                            style = MaterialTheme.typography.headlineLarge
                        )

                        // 5. Combinando todo
                        Text(
                            text = "Título Verde",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}