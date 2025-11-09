package com.semilladigital.courses.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.semilladigital.courses.domain.model.Course
import com.semilladigital.app.core.ui.SemillaDigitalTheme // <-- 1. CORRECCIÓN DE IMPORT

// --- 1. El Composable principal de la pantalla ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    // Hilt inyecta el ViewModel automáticamente
    viewModel: CourseViewModel = hiltViewModel()
) {
    // Observamos el 'state' del ViewModel.
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Usamos el tema que definimos para toda la app
    SemillaDigitalTheme { // <-- 2. TEMA DESCOMENTADO
        Scaffold(
            topBar = {
                // La barra superior como en el PDF
                CenterAlignedTopAppBar(
                    title = { Text("Cursos y Capacitación") },
                    actions = {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary, // El color verde
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            // Contenido principal de la pantalla
            CourseContent(
                modifier = Modifier.padding(paddingValues),
                state = state
            )
        }
    }
}

// --- 2. El Composable del contenido ---

@Composable
private fun CourseContent(
    modifier: Modifier = Modifier,
    state: CourseState
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (state.isLoading) {
            // Muestra un indicador de carga en el centro
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (state.error != null) {
            // Muestra un mensaje de error
            Text(
                text = "Error: ${state.error}",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center)
            )
        }

        if (!state.isLoading && state.error == null) {
            // Muestra la lista de cursos si todo está bien
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Barra de Búsqueda ---
                item {
                    SearchBar(
                        query = state.searchQuery,
                        onQueryChange = { /* TODO: viewModel.onSearchQueryChange(it) */ }
                    )
                }

                // --- Filtros (Chips) ---
                // (Aquí iría un LazyRow para los filtros)

                // --- Cursos Destacados ---
                item {
                    Text(
                        text = "Cursos Destacados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    // Card de ejemplo (reemplazar con LazyRow)
                    CourseCard(
                        // 3. MODELO ACTUALIZADO (con 'direccion')
                        course = Course(
                            id = "1",
                            titulo = "Gestión Financiera Agrícola",
                            descripcion = "Aprende a administrar eficientemente los recursos...",
                            modalidad = "En Línea",
                            fechaCurso = "",
                            direccion = "Curso en línea", // <-- CAMPO AÑADIDO
                            url = null, lat = null, longitud = null
                        ),
                        onCourseClick = { /* TODO */ },
                        onEnrollClick = { /* TODO */ }
                    )
                }


                // --- Todos los Cursos ---
                item {
                    Text(
                        text = "Todos los Cursos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Renderiza la lista de cursos
                items(state.courses) { course ->
                    CourseCard(
                        course = course,
                        onCourseClick = { /* TODO: Navegar a detalles */ },
                        onEnrollClick = { /* TODO: viewModel.enrollInCourse(course.id) */ }
                    )
                }
            }
        }
    }
}

// --- 3. El Composable para la tarjeta de un curso ---

@OptIn(ExperimentalMaterial3Api::class) // <-- Añadido por 'Card'
@Composable
private fun CourseCard(
    course: Course,
    onCourseClick: () -> Unit,
    onEnrollClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onCourseClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = course.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = course.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
            Spacer(Modifier.height(8.dp))

            // 3. AÑADIDO: Mostrar la dirección
            if (course.direccion != null) {
                Text(
                    text = course.direccion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(8.dp))
            }

            Text(
                text = course.modalidad,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onEnrollClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Inscribirse")
            }
        }
    }
}

// --- 4. El Composable para la barra de búsqueda ---

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar cursos...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    )
}