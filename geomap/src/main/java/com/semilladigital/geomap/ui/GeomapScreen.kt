package com.semilladigital.geomap.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.semilladigital.chatbot.presentation.ChatViewModel
import com.semilladigital.geomap.utils.MapUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeomapScreen(
    onBack: () -> Unit,
    viewModel: GeomapViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // --- LÓGICA DE CONTEXTO PARA EL CHATBOT ---
    LaunchedEffect(state.selectedUbicacion, state.searchQuery) {
        val ubi = state.selectedUbicacion
        val query = state.searchQuery

        if (ubi != null) {
            // Caso 1: Viendo detalle de una ubicación
            chatViewModel.setContext(
                "El usuario ha seleccionado un marcador en el Geomapa: '${ubi.nombre}'. " +
                        "Tipo: ${ubi.tipo}. Municipio: ${ubi.municipio}. " +
                        "Descripción: ${ubi.descripcion}. " +
                        "Teléfono: ${ubi.telefono ?: "No disponible"}. " +
                        "El usuario tiene un botón para abrir esta ubicación en Google Maps."
            )
        } else {
            // Caso 2: Explorando el mapa general o buscando
            val searchContext = if (query.isNotBlank()) " Actualmente ha filtrado el mapa buscando: '$query'." else ""

            chatViewModel.setContext(
                "El usuario está explorando el Geomapa de Recursos. " +
                        "Este mapa interactivo muestra parcelas productivas (polígonos verdes) y marcadores de interés (oficinas, proveedores, centros de acopio).$searchContext"
            )
        }
    }

    // 1. Gestión de Permisos de Ubicación
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasLocationPermission = isGranted }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // 2. Configuración del Mapa
    val cameraPositionState = rememberCameraPositionState {
        // Centrado en Chetumal/Bacalar como punto de partida
        position = CameraPosition.fromLatLngZoom(LatLng(18.500, -88.300), 10f)
    }

    Scaffold(
        topBar = {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onBack = onBack
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = hasLocationPermission
                ),
                properties = MapProperties(
                    mapType = MapType.NORMAL,
                    isMyLocationEnabled = hasLocationPermission
                )
            ) {
                // --- DIBUJAR PARCELAS (Polígonos) ---
                state.filteredParcelas.forEach { parcela ->
                    val coords = parcela.coordenadas.map { LatLng(it.lat, it.lng) }

                    if (coords.isNotEmpty()) {
                        Polygon(
                            points = coords,
                            fillColor = Color(0x554CAF50),
                            strokeColor = Color(0xFF2E7D32),
                            strokeWidth = 3f,
                            tag = parcela.nombre
                        )

                        val centro = MapUtils.calculateCentroid(coords)
                        val emoji = MapUtils.getEmojiForActivity(parcela.actividades)

                        Marker(
                            state = MarkerState(position = centro),
                            icon = MapUtils.textToBitmapDescriptor(emoji, context),
                            title = parcela.nombre,
                            snippet = parcela.actividades.joinToString(", ")
                        )
                    }
                }

                // --- DIBUJAR UBICACIONES ESPECIALES (Marcadores) ---
                state.filteredUbicaciones.forEach { ubicacion ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(ubicacion.coordenada.lat, ubicacion.coordenada.lng)
                        ),
                        title = ubicacion.nombre,
                        onInfoWindowClick = { viewModel.selectUbicacion(ubicacion) },
                        onClick = {
                            viewModel.selectUbicacion(ubicacion)
                            false
                        }
                    )
                }
            }

            if(state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    // 3. Bottom Sheet de Detalles
    if (state.selectedUbicacion != null) {
        val ubi = state.selectedUbicacion!!
        ModalBottomSheet(onDismissRequest = { viewModel.selectUbicacion(null) }) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = ubi.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = ubi.tipo.replace("_", " ").uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))

                Text(text = ubi.descripcion, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))

                if (!ubi.telefono.isNullOrBlank()) {
                    Text(
                        text = "📞 ${ubi.telefono}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        val gmmIntentUri = Uri.parse(
                            "geo:0,0?q=${ubi.coordenada.lat},${ubi.coordenada.lng}(${Uri.encode(ubi.nombre)})"
                        )
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Map, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Ver en Google Maps")
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, onBack: () -> Unit) {
    Surface(
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            // >>> APLICACIÓN DEL INSET DE LA BARRA DE ESTADO (SOLUCIÓN) <<<
            // Esto empuja el Surface hacia abajo para respetar la barra de estado.
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
            }
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Buscar cultivo, municipio...", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                trailingIcon = if(query.isNotEmpty()) {
                    { IconButton(onClick = { onQueryChange("") }) { Icon(Icons.Default.Close, null) } }
                } else null,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f).height(50.dp)
            )
        }
    }
}