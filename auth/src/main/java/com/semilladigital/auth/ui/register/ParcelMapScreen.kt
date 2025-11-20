package com.semilladigital.auth.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun ParcelMapScreen(
    onPolygonCompleted: (List<List<Double>>) -> Unit,
    onBack: () -> Unit
) {
    // Coordenadas iniciales (Chetumal)
    val chetumal = LatLng(18.500, -88.300)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(chetumal, 13f)
    }

    // Estado de los puntos: Usamos una lista inmutable para forzar la recomposición al modificarla
    var polygonPoints by remember { mutableStateOf(listOf<LatLng>()) }

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.HYBRID,
                isMyLocationEnabled = false
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            onMapClick = { latLng ->
                polygonPoints = polygonPoints + latLng
            }
        ) {
            // Marcadores
            polygonPoints.forEach { point ->
                Marker(
                    state = MarkerState(position = point),
                    title = "Punto"
                )
            }

            // Polígono (3 o más puntos)
            if (polygonPoints.size >= 3) {
                Polygon(
                    points = polygonPoints,
                    fillColor = Color(0x554CAF50),
                    strokeColor = Color(0xFF4CAF50),
                    strokeWidth = 5f
                )
            } else if (polygonPoints.size == 2) {
                // Línea guía (2 puntos)
                Polyline(
                    points = polygonPoints,
                    color = Color(0xFF4CAF50)
                )
            }
        }

        // Controles Superiores
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
                .background(Color.White.copy(alpha = 0.9f), MaterialTheme.shapes.medium)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Toca el mapa para marcar los puntos.", style = MaterialTheme.typography.bodySmall)
                Text("Mínimo 3 puntos para cerrar.", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Row {
                IconButton(onClick = {
                    if (polygonPoints.isNotEmpty()) {
                        polygonPoints = polygonPoints.dropLast(1)
                    }
                }) {
                    Icon(Icons.Default.Undo, contentDescription = "Deshacer", tint = Color.Black)
                }
                IconButton(onClick = { polygonPoints = emptyList() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar todo", tint = Color.Red)
                }
            }
        }

        // Botón Guardar
        Button(
            onClick = {
                val coordenadas = polygonPoints.map { listOf(it.latitude, it.longitude) }
                onPolygonCompleted(coordenadas)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            enabled = polygonPoints.size >= 3,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Guardar Parcela")
        }
    }
}