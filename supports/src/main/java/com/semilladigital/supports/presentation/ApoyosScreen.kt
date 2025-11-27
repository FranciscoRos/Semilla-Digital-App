package com.semilladigital.supports.presentation

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.semilladigital.supports.domain.model.Apoyo
import com.semilladigital.supports.domain.model.Requerimiento
import com.semilladigital.chatbot.presentation.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApoyosScreen(
    onBack: () -> Unit,
    viewModel: ApoyosViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    val filteredApoyos = state.todosLosApoyos.filter {
        it.nombre_programa.contains(state.searchQuery, ignoreCase = true) ||
                it.institucion_acronimo.contains(state.searchQuery, ignoreCase = true) ||
                it.descripcion.contains(state.searchQuery, ignoreCase = true)
    }

    LaunchedEffect(state.selectedApoyo, filteredApoyos) {
        val apoyo = state.selectedApoyo

        if (apoyo != null) {
            val reqs = apoyo.Requerimientos.joinToString(", ") { req ->
                req.nombre ?: req.Requisito ?: "Requisito general"
            }
            chatViewModel.setContext(
                "El usuario está viendo el detalle del Apoyo: '${apoyo.nombre_programa}'. " +
                        "Institución: ${apoyo.institucion_acronimo}. " +
                        "Objetivo: ${apoyo.objetivo}. " +
                        "Requisitos clave: $reqs."
            )
        } else {
            val listaApoyosTexto = filteredApoyos.joinToString(separator = "\n") { item ->
                "- Nombre: ${item.nombre_programa} (${item.institucion_acronimo}). Objetivo: ${item.objetivo}"
            }

            val searchContext = if (state.searchQuery.isNotBlank()) " Buscando: '${state.searchQuery}'." else ""

            chatViewModel.setContext(
                "El usuario está en la lista de Apoyos y Programas.$searchContext\n" +
                        "LISTA DE APOYOS DISPONIBLES EN PANTALLA:\n$listaApoyosTexto"
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Apoyos y Programas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF5F6F8)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    label = { Text("Buscar apoyos...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                    )
                )
                Spacer(Modifier.height(8.dp))
            }

            if (state.isLoading) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text("Cargando apoyos...", modifier = Modifier.padding(top = 8.dp))
                }
            } else if (state.error != null) {
                item {
                    Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::refreshApoyos) {
                        Text("Reintentar")
                    }
                }
            } else {
                if (state.actividadesDelUsuario.isNotEmpty() && state.apoyosParaTi.isNotEmpty() && state.searchQuery.isEmpty()) {
                    item {
                        Text("Apoyos para ti 🌱", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Basado en tus actividades productivas", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            items(state.apoyosParaTi) { apoyo ->
                                ApoyoCard(apoyo = apoyo, isSuggested = true, onClick = { viewModel.onApoyoSelected(apoyo) })
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item {
                    Text("Todos los Apoyos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.height(8.dp))
                }

                items(filteredApoyos) { apoyo ->
                    ApoyoCard(apoyo = apoyo, isSuggested = false, onClick = { viewModel.onApoyoSelected(apoyo) })
                }
                item {
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    if (state.showDetailsDialog && state.selectedApoyo != null) {
        ApoyoDetailsDialog(apoyo = state.selectedApoyo!!, onClose = viewModel::onCloseDetails)
    }
}

@Composable
fun ApoyoCard(apoyo: Apoyo, isSuggested: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .then(if (isSuggested) Modifier.width(280.dp) else Modifier.fillMaxWidth())
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = apoyo.nombre_programa,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Institución: ${apoyo.institucion_acronimo}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = apoyo.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF424242),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Ver Detalles")
            }
        }
    }
}

@Composable
fun ApoyoDetailsDialog(apoyo: Apoyo, onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(modifier = Modifier.padding(24.dp)) {
                item {
                    Text(apoyo.nombre_programa, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(16.dp))
                }

                item {
                    SectionTitle("Descripción")
                    Text(apoyo.descripcion, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                }

                item {
                    SectionTitle("Objetivo")
                    Text(apoyo.objetivo, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                }

                item {
                    SectionTitle("Información de Contacto")
                    DetailRow("Institución", "${apoyo.institucion_encargada} (${apoyo.institucion_acronimo})")
                    DetailRow("Horario", apoyo.horarios_atencion)
                    DetailRow("Teléfono", apoyo.telefono_contacto)
                    DetailRow("Correo", apoyo.correo_contacto)
                    DetailRow("Dirección", apoyo.direccion)
                    Spacer(Modifier.height(16.dp))
                }

                if (apoyo.Requerimientos.isNotEmpty()) {
                    item {
                        SectionTitle("Requerimientos")
                        apoyo.Requerimientos.forEach { req ->
                            RequerimientoItem(req)
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = onClose) {
                            Text("Cerrar")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = onClose) {
                            Text("Solicitar (WIP)")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = Color.Black)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}

@Composable
fun RequerimientoItem(req: Requerimiento) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp).padding(top = 2.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        val text = when (req.type) {
            "regla_parcela" -> "Requiere parcelas con áreas: ${req.config?.areas?.joinToString(", ") ?: "N/A"}"
            "regla_pregunta" -> "Requiere que la pregunta '${req.fieldName ?: "N/A"}' sea ${req.validation?.operator ?: ""} ${req.validation?.value ?: ""}"
            else -> req.nombre?.let { "$it: ${req.valor}" } ?: req.Requisito ?: "Requisito sin clasificar"
        }
        Text(text, style = MaterialTheme.typography.bodySmall, color = Color.Black)
    }
}