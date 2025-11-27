package com.semilladigital.courses.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.semilladigital.app.core.ui.SemillaDigitalTheme
import com.semilladigital.app.core.ui.SemillaScreen
import com.semilladigital.chatbot.presentation.ChatViewModel
import com.semilladigital.courses.domain.model.Course

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    viewModel: CourseViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.courses) {
        if (state.courses.isNotEmpty()) {
            val listaCursosCompleta = state.courses.joinToString(separator = "\n\n") { curso ->
                """
                [CURSO]
                ID: ${curso.id}
                Título: ${curso.titulo}
                Descripción: ${curso.descripcion}
                Detalles Completos: ${curso.detalles ?: "Sin detalles adicionales"}
                Tema: ${curso.tema ?: "General"}
                Modalidad: ${curso.modalidad}
                Fechas: ${curso.fechaCurso.joinToString(", ")}
                Dirección: ${curso.direccion ?: "No especificada"}
                Coordenadas: ${if(curso.lat != null) "${curso.lat}, ${curso.longitud}" else "N/A"}
                URL: ${curso.url ?: "N/A"}
                """.trimIndent()
            }

            chatViewModel.setContext(
                "El usuario está navegando en la lista de Cursos.\n" +
                        "A continuación se detallan TODOS los datos de los cursos disponibles en pantalla para que puedas responder cualquier duda específica sobre ellos:\n\n$listaCursosCompleta"
            )
        } else if (state.isLoading) {
            chatViewModel.setContext("El usuario está esperando a que carguen los cursos...")
        } else {
            chatViewModel.setContext("El usuario está en la pantalla de Cursos, pero la lista está vacía.")
        }
    }

    SemillaDigitalTheme {
        SemillaScreen(
            title = "Cursos y Capacitación",
            onNavigateBack = onNavigateBack,
            onNotificationClick = { }
        ) { paddingValues ->

            CourseContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onEvent = viewModel::onEvent
            )

            val selectedCourse = state.selectedCourse
            if (selectedCourse != null) {
                LaunchedEffect(selectedCourse) {
                    chatViewModel.setContext(
                        """
                        El usuario está viendo los detalles específicos del siguiente curso:
                        Título: ${selectedCourse.titulo}
                        Descripción: ${selectedCourse.descripcion}
                        Detalles: ${selectedCourse.detalles ?: "N/A"}
                        Tema: ${selectedCourse.tema}
                        Modalidad: ${selectedCourse.modalidad}
                        Fechas: ${selectedCourse.fechaCurso.joinToString(", ")}
                        Dirección: ${selectedCourse.direccion}
                        Coordenadas: ${selectedCourse.lat}, ${selectedCourse.longitud}
                        URL Web: ${selectedCourse.url}
                        """.trimIndent()
                    )
                }

                CourseDetailsSheet(
                    course = selectedCourse,
                    onDismiss = {
                        viewModel.onEvent(CourseEvent.OnHideDetails)
                        val listaCursos = state.courses.joinToString(separator = ", ") { it.titulo }
                        chatViewModel.setContext("El usuario regresó a la lista general. Cursos visibles: $listaCursos")
                    },
                    onGoToMap = { lat, lon ->
                        val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon")
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
    val titleColor = Color(0xFF07490A)

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF5F6F8))) {
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
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SearchBar(
                            modifier = Modifier.weight(1f),
                            query = state.searchQuery,
                            onQueryChange = { onEvent(CourseEvent.OnSearchQueryChanged(it)) }
                        )
                        IconButton(onClick = { onEvent(CourseEvent.OnShowFilterDialog) }) {
                            Icon(Icons.Filled.FilterList, contentDescription = "Filtros", tint = Color.Gray)
                        }
                    }
                }

                val isFiltering = state.searchQuery.isNotEmpty() ||
                        state.selectedTema != "Todos" ||
                        state.selectedModalidad != "Todas" ||
                        state.selectedDateFilter != "Todos"

                if (state.recommendedCourses.isNotEmpty() && !isFiltering) {
                    item {
                        Text(
                            text = "Cursos para ti \uD83C\uDF31",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                        Text(
                            text = "Basado en tus actividades",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.recommendedCourses) { course ->
                                CourseCard(
                                    course = course,
                                    isSuggested = true,
                                    onDetailsClick = { onEvent(CourseEvent.OnShowDetails(course)) }
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item {
                    Text(
                        text = if (isFiltering) "Resultados" else "Todos los Cursos",
                        style = MaterialTheme.typography.titleLarge,
                        color = titleColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (state.courses.isEmpty() && !state.isLoading) {
                    item {
                        Text(
                            text = "No se encontraron cursos.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            color = Color.Gray
                        )
                    }
                }

                items(state.courses) { course ->
                    CourseCard(
                        course = course,
                        isSuggested = false,
                        onDetailsClick = { onEvent(CourseEvent.OnShowDetails(course)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseCard(
    course: Course,
    isSuggested: Boolean,
    onDetailsClick: () -> Unit
) {

    val modifier = if (isSuggested) {
        Modifier
            .width(280.dp)
            .height(200.dp)
    } else {
        Modifier.fillMaxWidth()
    }

    Card(
        modifier = modifier.clickable(onClick = onDetailsClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = course.titulo,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                val subTitle = if (course.tema != null) "${course.modalidad} • ${course.tema}" else course.modalidad
                Text(
                    text = subTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = course.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF424242),
                    maxLines = if (isSuggested) 3 else 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // El botón se queda abajo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Button(
                    onClick = onDetailsClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = "Ver Detalles",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Buscar cursos...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
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
            TextButton(onClick = onDismiss) { Text("Aplicar") }
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
        sheetState = modalSheetState,
        containerColor = Color.White
    ) {
        LazyColumn(
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 32.dp),
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
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0288D1)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = course.detalles,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF424242)
                    )
                }
            }
            item {
                Text(
                    text = "Información Adicional",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0288D1)
                )
                Spacer(Modifier.height(4.dp))
                Text("Modalidad: ${course.modalidad}", style = MaterialTheme.typography.bodyMedium)
                if(course.tema != null) {
                    Text("Tema: ${course.tema}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (course.lat != null && course.longitud != null) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { onGoToMap(course.lat, course.longitud) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Ver Ubicación en Mapa")
                    }
                }
            }
        }
    }
}