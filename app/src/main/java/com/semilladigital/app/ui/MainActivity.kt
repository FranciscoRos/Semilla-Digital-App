package com.semilladigital.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.semilladigital.app.core.ui.SemillaDigitalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CORRECCIÓN: Cambiamos a 'false' para permitir diseño Edge-to-Edge
        // Esto permite que tu TopBar verde se dibuje detrás de la barra de estado (hora/batería)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SemillaDigitalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}