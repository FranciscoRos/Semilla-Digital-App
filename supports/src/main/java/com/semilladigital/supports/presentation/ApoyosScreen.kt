package com.semilladigital.supports.presentation

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.semilladigital.supports.domain.model.Apoyo
import com.semilladigital.supports.domain.model.ApoyoUiItem
import com.semilladigital.supports.domain.model.EstatusApoyo
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
    val context = LocalContext.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    // Manejo de mensajes de éxito (Toast)
    LaunchedEffect(state.mensajeExito) {
        state.mensajeExito?.let { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
            viewModel.limpiarMensajeExito()
        }
    }

    val filteredItems = state.listadoApoyos.filter { item ->
        val apoyo = item.apoyo
        apoyo.nombre_programa.contains(state.searchQuery, ignoreCase = true) ||
                apoyo.institucion_acronimo.contains(state.searchQuery, ignoreCase = true) ||
                apoyo.descripcion.contains(state.searchQuery, ignoreCase = true)
    }

    LaunchedEffect(state.selectedApoyoItem, filteredItems) {
        val selected = state.selectedApoyoItem
        if (selected != null) {
            val apoyo = selected.apoyo
            chatViewModel.setContext(
                "Viendo detalle: '${apoyo.nombre_programa}'. " +
                        "Estatus para usuario: ${selected.estatus}. " +
                        "Motivo (si aplica): ${selected.motivoNoEligible ?: "N/A"}"
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
                    containerColor = Color.White
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
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            if (state.isLoading) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text("Analizando elegibilidad...", modifier = Modifier.padding(top = 8.dp))
                }
            } else if (state.error != null) {
                item {
                    Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::refreshApoyos) { Text("Reintentar") }
                }
            } else {
                if (filteredItems.isEmpty()) {
                    item {
                        Text("No se encontraron apoyos.", modifier = Modifier.padding(16.dp))
                    }
                }

                val disponibles = filteredItems.filter { it.estatus == EstatusApoyo.DISPONIBLE }
                if (disponibles.isNotEmpty()) {
                    item {
                        Text(
                            "Disponibles para ti ✅",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                    items(disponibles) { item ->
                        ApoyoCard(item = item, onClick = { viewModel.onApoyoSelected(item) })
                    }
                }

                val otros = filteredItems.filter { it.estatus != EstatusApoyo.DISPONIBLE }
                if (otros.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Otros (Inscritos o No Disponibles)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                    items(otros) { item ->
                        ApoyoCard(item = item, onClick = { viewModel.onApoyoSelected(item) })
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }

    if (state.showDetailsDialog && state.selectedApoyoItem != null) {
        ApoyoDetailsDialog(
            item = state.selectedApoyoItem!!,
            onClose = viewModel::onCloseDetails,
            onInscribir = { viewModel.inscribirseEnApoyo(state.selectedApoyoItem!!.apoyo) },
            isInscribiendo = state.isInscribiendo
        )
    }
}

@Composable
fun ApoyoCard(item: ApoyoUiItem, onClick: () -> Unit) {
    val apoyo = item.apoyo
    val cardColor = if (item.estatus == EstatusApoyo.DISPONIBLE) Color.White else Color(0xFFEEEEEE)
    val borderColor = if (item.estatus == EstatusApoyo.DISPONIBLE) Color(0xFFE0E0E0) else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(if (item.estatus == EstatusApoyo.DISPONIBLE) 3.dp else 0.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = apoyo.nombre_programa,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (item.estatus == EstatusApoyo.DISPONIBLE) Color.Black else Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = apoyo.institucion_acronimo,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                StatusBadge(item.estatus)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = apoyo.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF424242),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            if (item.estatus == EstatusApoyo.NO_CUMPLE_REQUISITOS) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "⚠️ ${item.motivoNoEligible ?: "Requisitos no cumplidos"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFD32F2F)
                )
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (item.estatus == EstatusApoyo.DISPONIBLE) Color(0xFF4CAF50) else Color.Gray
                ),
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Ver Detalles")
            }
        }
    }
}

@Composable
fun StatusBadge(estatus: EstatusApoyo) {
    val (color, text, icon) = when (estatus) {
        EstatusApoyo.DISPONIBLE -> Triple(Color(0xFFE8F5E9), "Disponible", Icons.Default.CheckCircle)
        EstatusApoyo.YA_INSCRITO -> Triple(Color(0xFFE3F2FD), "Inscrito", Icons.Default.CheckCircle)
        EstatusApoyo.NO_CUMPLE_REQUISITOS -> Triple(Color(0xFFFFEBEE), "No elegible", Icons.Default.Lock)
    }

    Surface(
        color = color,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Black.copy(alpha = 0.6f))
            Spacer(Modifier.width(4.dp))
            Text(text, style = MaterialTheme.typography.labelSmall, color = Color.Black.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun ApoyoDetailsDialog(item: ApoyoUiItem, onClose: () -> Unit, onInscribir: () -> Unit, isInscribiendo: Boolean) {
    val apoyo = item.apoyo

    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(modifier = Modifier.padding(24.dp)) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            apoyo.nombre_programa,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    StatusBadge(item.estatus)
                    Spacer(Modifier.height(16.dp))

                    if (item.estatus == EstatusApoyo.NO_CUMPLE_REQUISITOS) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Razón: ${item.motivoNoEligible}",
                                modifier = Modifier.padding(12.dp),
                                color = Color(0xFFC62828),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }
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

                item {
                    SectionTitle("Requerimientos")
                    if (apoyo.Requerimientos.isEmpty()) {
                        Text("No se especifican requisitos adicionales.", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        apoyo.Requerimientos.forEach { req ->
                            RequerimientoItem(req)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = onClose) { Text("Cerrar") }
                        Spacer(Modifier.width(8.dp))

                        val btnText = when(item.estatus) {
                            EstatusApoyo.DISPONIBLE -> "Inscribirme"
                            EstatusApoyo.YA_INSCRITO -> "Ya Inscrito"
                            EstatusApoyo.NO_CUMPLE_REQUISITOS -> "No disponible"
                        }

                        Button(
                            onClick = onInscribir,
                            enabled = item.estatus == EstatusApoyo.DISPONIBLE && !isInscribiendo
                        ) {
                            if (isInscribiendo) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(btnText)
                            }
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
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

@Composable
fun RequerimientoItem(req: Requerimiento) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp).padding(top = 2.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        val text = when (req.type) {
            "regla_parcela" -> "Parcela: ${req.config?.actividades?.joinToString() ?: "Cualquiera"} (${req.config?.hectareas ?: 0} ha min)"
            else -> req.nombre ?: req.Requisito ?: "Requisito general"
        }
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}