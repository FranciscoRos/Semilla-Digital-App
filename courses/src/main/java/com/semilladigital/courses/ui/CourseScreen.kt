package com.semilladigital.courses.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Importa todos los íconos
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
import java.time.format.DateTimeFormatter // Import para formatear la fecha

// --- ANOTACIÓN PARA LAS APIS EXPERIMENTALES (DATEPICKER, SCAFFOLD, ETC.) ---
@OptIn(ExperimentalMaterial3Api::class)

// --- 1. El Composable principal de la pantalla ---
@Composable
fun CourseScreen(
    viewModel: CourseViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit // El parámetro para la flecha de "Volver"
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    SemillaDigitalTheme {
        SemillaScreen(
            title = "Cursos y Capacitación",
            onNavigateBack = onNavigateBack, // Pasa la acción de "Volver"
            onNotificationClick = { /* TODO */ }
        ) { paddingValues ->

            // Contenido principal
            CourseContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onEvent = viewModel::onEvent // Pasa los eventos al ViewModel
            )

            // --- Modal de Detalles ---
            val selectedCourse = state.selectedCourse
            if (selectedCourse != null) {
                CourseDetailsSheet(
                    course = selectedCourse,
                    onDismiss = { viewModel.onEvent(CourseEvent.OnHideDetails) },
                    onGoToMap = { lat, lon ->
                        // Lógica para abrir Google Maps
                        val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$lon")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    }
                )
            }

            // --- Diálogo de Filtros ---
            if (state.isFilterDialogVisible) {
                FilterDialog(
                    state = state,
                    onDismiss = { viewModel.onEvent(CourseEvent.OnHideFilterDialog) },
                    onTemaSelected = { viewModel.onEvent(CourseEvent.OnFilterTemaChanged(it)) },
                    onModalidadSelected = { viewModel.onEvent(CourseEvent.OnFilterModalidadChanged(it)) },
                    onDateFilterSelected = { viewModel.onEvent(CourseEvent.OnDateFilterChanged(it)) }
                )
            }

            // --- (DatePicker ya no existe) ---
        }
    }
}

// --- 2. El Composable del contenido ---
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
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Barra de Búsqueda y Filtros ---
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SearchBar(
                            modifier = Modifier.weight(1f),
                            query = state.searchQuery,
                            onQueryChange = { onEvent(CourseEvent.OnSearchQueryChanged(it)) }
                        )
                        // Botón de Filtros
                        IconButton(onClick = { onEvent(CourseEvent.OnShowFilterDialog) }) {
                            Icon(Icons.Filled.FilterList, contentDescription = "Filtros")
                        }
                    }
                }

                // --- Cursos ---
                item {
                    Text(
                        text = "Todos los Cursos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (state.courses.isEmpty() && !state.isLoading) {
                    item {
                        Text(
                            text = "No se encontraron cursos que coincidan con tu búsqueda.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
                        )
                    }
                }

                items(state.courses) { course ->
                    CourseCard(
                        course = course,
                        onDetailsClick = { onEvent(CourseEvent.OnShowDetails(course)) }
                    )
                }
            }
        }
    }
}

// --- 3. El Composable para la tarjeta de un curso ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseCard(
    course: Course,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onDetailsClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(course.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(course.descripcion, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = course.modalidad,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                if (course.tema != null) {
                    Text(
                        text = course.tema,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onDetailsClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Ver Detalles")
            }
        }
    }
}

// --- 4. El Composable para la barra de búsqueda ---
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
        singleLine = true
    )
}

// --- 5. Composable para los filtros (dentro del diálogo) ---
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


// --- 6. Composable para el Diálogo de Filtros (CORREGIDO) ---
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
            // Reutilizamos el Composable de los chips
            FilterChips(
                temas = state.availableTemas,
                selectedTema = state.selectedTema,
                onTemaSelected = onTemaSelected,
                modalidades = state.availableModalidades,
                selectedModalidad = state.selectedModalidad,
                // --- ¡ESTA ES LA LÍNEA QUE FALTABA! ---
                onModalidadSelected = onModalidadSelected,

                // Pasamos los nuevos parámetros de fecha
                dateFilters = state.availableDateFilters,
                selectedDateFilter = state.selectedDateFilter,
                onDateFilterSelected = onDateFilterSelected
            )
        }
    )
}


// --- 7. Composable para el Modal de Detalles ---
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
            // Título
            item {
                Text(
                    text = course.titulo,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Detalles
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

            // Ubicación
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