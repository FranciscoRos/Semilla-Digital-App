package com.semilladigital.courses.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.semilladigital.app.core.ui.SemillaDigitalTheme
import com.semilladigital.app.core.ui.SemillaScreen
import com.semilladigital.courses.domain.model.Course

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    viewModel: CourseViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    SemillaDigitalTheme {
        SemillaScreen(
            title = "Cursos y Capacitación",
            onNavigateBack = onNavigateBack,
            onNotificationClick = { /* TODO */ }
        ) { paddingValues ->

            CourseContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onEvent = viewModel::onEvent
            )

            val selectedCourse = state.selectedCourse
            if (selectedCourse != null) {
                CourseDetailsSheet(
                    course = selectedCourse,
                    onDismiss = { viewModel.onEvent(CourseEvent.OnHideDetails) },
                    onGoToMap = { lat, lon ->
                        val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$lon")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    }
                )
            }

            if (state.isFilterDialogVisible) {
                FilterDialog(
                    state = state,
                    onDismiss = { viewModel.onEvent(CourseEvent.OnHideFilterDialog) },
                    onTemaSelected = { viewModel.onEvent(CourseEvent.OnFilterTemaChanged(it)) },
                    onModalidadSelected = { viewModel.onEvent(CourseEvent.OnFilterModalidadChanged(it)) },
                    onDateFilterSelected = { viewModel.onEvent(CourseEvent.OnDateFilterChanged(it)) }
                )
            }
        }
    }
}

@Composable
private fun CourseContent(
    modifier: Modifier = Modifier,
    state: CourseState,
    onEvent: (CourseEvent) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (state.error != null) {
            Text(
                text = "Error: ${state.error}",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.Center)
            )
        }

        if (!state.isLoading && state.error == null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Barra de Búsqueda ---
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SearchBar(
                            modifier = Modifier.weight(1f),
                            query = state.searchQuery,
                            onQueryChange = { onEvent(CourseEvent.OnSearchQueryChanged(it)) }
                        )
                        IconButton(onClick = { onEvent(CourseEvent.OnShowFilterDialog) }) {
                            Icon(Icons.Filled.FilterList, contentDescription = "Filtros")
                        }
                    }
                }

                // --- SECCIÓN: CURSOS PARA TI ---
                if (state.recommendedCourses.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)) // Fondo sutil
                                .padding(vertical = 16.dp)
                        ) {
                            Text(
                                text = "Cursos para ti \uD83C\uDF31",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Text(
                                text = "Basado en tus actividades productivas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(Modifier.height(12.dp))

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.recommendedCourses) { course ->
                                    // USAMOS LA TARJETA GRANDE, PERO CON ANCHO FIJO
                                    CourseCard(
                                        course = course,
                                        onDetailsClick = { onEvent(CourseEvent.OnShowDetails(course)) },
                                        modifier = Modifier.width(300.dp) // <--- Ancho para carrusel
                                    )
                                }
                            }
                        }
                    }
                }

                // --- SECCIÓN: TODOS LOS CURSOS ---
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Todos los Cursos",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                if (state.courses.isEmpty() && !state.isLoading) {
                    item {
                        Text(
                            text = "No se encontraron cursos que coincidan con tu búsqueda.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(32.dp)
                        )
                    }
                }

                items(state.courses) { course ->
                    // USAMOS LA TARJETA GRANDE, LLENANDO EL ANCHO
                    CourseCard(
                        course = course,
                        onDetailsClick = { onEvent(CourseEvent.OnShowDetails(course)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

// --- TARJETA ÚNICA Y ESTANDARIZADA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseCard(
    course: Course,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick = onDetailsClick,
        shape = RoundedCornerShape(12.dp), // Bordes redondeados elegantes
        colors = CardDefaults.cardColors(
            containerColor = Color.White, // Fondo BLANCO limpio
            contentColor = Color.Black
        ),
        // BORDE GRIS OSCURO (Casi negro) como pediste
        border = BorderStroke(1.dp, Color(0xFF424242))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Título
            Text(
                text = course.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2 // Evita que títulos largos rompan todo
            )

            Spacer(Modifier.height(6.dp))

            // Descripción
            Text(
                text = course.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                color = Color.DarkGray
            )

            Spacer(Modifier.height(12.dp))

            // Chips de Información (Tema y Modalidad)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chip Modalidad
                CourseInfoChip(text = course.modalidad, color = Color(0xFFEEEEEE))

                // Chip Tema (si existe)
                if (course.tema != null) {
                    CourseInfoChip(text = course.tema, color = Color(0xFFE3F2FD)) // Azulito muy suave
                }
            }

            Spacer(Modifier.height(16.dp))

            // Botón de Acción
            Button(
                onClick = onDetailsClick,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary // Verde de tu tema
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ver Detalles")
            }
        }
    }
}

// Pequeño componente auxiliar para los Chips (Etiquetas)
@Composable
private fun CourseInfoChip(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = Color.Black,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

// --- (Resto de componentes SearchBar, FilterChips, Dialogs... Igual) ---
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar cursos...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChips(
    temas: List<String>,
    selectedTema: String,
    onTemaSelected: (String) -> Unit,

    modalidades: List<String>,
    selectedModalidad: String,
    onModalidadSelected: (String) -> Unit,

    dateFilters: List<String>,
    selectedDateFilter: String,
    onDateFilterSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Temas", style = MaterialTheme.typography.titleSmall)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(temas) { tema ->
                FilterChip(
                    selected = (tema == selectedTema),
                    onClick = { onTemaSelected(tema) },
                    label = { Text(tema) }
                )
            }
        }

        Text("Modalidad", style = MaterialTheme.typography.titleSmall)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(modalidades) { modalidad ->
                FilterChip(
                    selected = (modalidad == selectedModalidad),
                    onClick = { onModalidadSelected(modalidad) },
                    label = { Text(modalidad) }
                )
            }
        }

        Text("Fecha", style = MaterialTheme.typography.titleSmall)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(dateFilters) { filter ->
                FilterChip(
                    selected = (filter == selectedDateFilter),
                    onClick = { onDateFilterSelected(filter) },
                    label = { Text(filter) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDialog(
    state: CourseState,
    onDismiss: () -> Unit,
    onTemaSelected: (String) -> Unit,
    onModalidadSelected: (String) -> Unit,
    onDateFilterSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("OK") }
        },
        title = { Text("Filtrar Cursos") },
        text = {
            FilterChips(
                temas = state.availableTemas,
                selectedTema = state.selectedTema,
                onTemaSelected = onTemaSelected,
                modalidades = state.availableModalidades,
                selectedModalidad = state.selectedModalidad,
                onModalidadSelected = onModalidadSelected,
                dateFilters = state.availableDateFilters,
                selectedDateFilter = state.selectedDateFilter,
                onDateFilterSelected = onDateFilterSelected
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseDetailsSheet(
    course: Course,
    onDismiss: () -> Unit,
    onGoToMap: (Double, Double) -> Unit
) {
    val modalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalSheetState
    ) {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = course.titulo,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            if (!course.detalles.isNullOrBlank()) {
                item {
                    Text(
                        text = "Detalles del Curso",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = course.detalles,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            if (course.lat != null && course.longitud != null) {
                item {
                    Text(
                        text = "Ubicación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (!course.direccion.isNullOrBlank()) {
                        Text(
                            text = course.direccion,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Button(
                        onClick = { onGoToMap(course.lat, course.longitud) },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Abrir en Google Maps")
                    }
                }
            }
        }
    }
}