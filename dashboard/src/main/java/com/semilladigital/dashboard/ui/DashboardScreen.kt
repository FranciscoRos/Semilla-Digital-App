package com.semilladigital.dashboard.ui

import android.app.Activity // <-- AÑADE ESTE IMPORT
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect // <-- AÑADE ESTE IMPORT
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView // <-- AÑADE ESTE IMPORT
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat // <-- AÑADE ESTE IMPORT
// Importa tu tema
import com.semilladigital.app.core.ui.SemillaDigitalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToCourses: () -> Unit,
    onNavigateToSupports: () -> Unit,
    onNavigateToChatbot: () -> Unit,
    onNavigateToGeomap: () -> Unit
) {

    // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
    // Como esta pantalla SIEMPRE tiene la barra verde,
    // forzamos los íconos de estado a ser CLAROS (false).
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    // --- FIN DE LA CORRECCIÓN ---

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hola, Jorge")
                        Text(
                            "Estatus: Verificado",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Servicios Principales",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Cuadrícula de botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ServiceButton(
                    text = "Solicitar Apoyos",
                    icon = Icons.Default.ListAlt,
                    onClick = onNavigateToSupports,
                    modifier = Modifier.weight(1f)
                )
                ServiceButton(
                    text = "Asistente virtual",
                    icon = Icons.Default.HelpOutline,
                    onClick = onNavigateToChatbot,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ServiceButton(
                    text = "Cursos y Capacitación",
                    icon = Icons.Default.School,
                    onClick = onNavigateToCourses,
                    modifier = Modifier.weight(1f)
                )
                ServiceButton(
                    text = "Geomapa de Recursos",
                    icon = Icons.Default.Map,
                    onClick = onNavigateToGeomap,
                    modifier = Modifier.weight(1f)
                )
            }

            // TODO: Aquí iría la lista de Alertas y Notificaciones
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // <-- AÑADIDO
@Composable
fun ServiceButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = text, textAlign = TextAlign.Center)
        }
    }
}