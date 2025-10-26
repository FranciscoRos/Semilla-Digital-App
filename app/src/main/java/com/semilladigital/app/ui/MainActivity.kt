package com.semilladigital.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.semilladigital.app.core.ui.SemillaDigitalTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.WindowCompat
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            // 2. ENVUELVE TODO CON TU TEMA
            SemillaDigitalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Texto de prueba por ahora
                    Text(text = "¡Hola Semilla Digital!")
                }
            }
        }
    }
}