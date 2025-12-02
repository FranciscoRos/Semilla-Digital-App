package com.semilladigital.forum.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.semilladigital.forum.domain.model.Comentario
import com.semilladigital.forum.domain.model.TemaDetalle

@Composable
fun ForoCategoriasScreen(
    categorias: List<CategoriaUiItem>,
    isLoading: Boolean,
    onNavigateToTemasList: () -> Unit
) {
    val scrollState = rememberScrollState()
    Scaffold(containerColor = Color(0xFFF5F6F8)) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(scrollState)) {
            HeaderForoCategorias(onNavigateToTemasList)
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    categorias.take(2).forEach { categoria -> CategoriaCard(categoria, Modifier.weight(1f)) }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HeaderForoCategorias(onNavigateToTemasList: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(24.dp)) {
        Text("COMUNIDAD AGRÍCOLA", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
        Text("Foro Semilla Digital", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1F2937), modifier = Modifier.padding(top = 4.dp))
        Text("Un ecosistema digital para compartir sabiduría, conectar generaciones y cultivar el futuro del campo juntos.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onNavigateToTemasList, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937))) {
            Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver Discusiones Recientes", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun CategoriaCard(item: CategoriaUiItem, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ContentPaste, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(item.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Text(item.descripcion, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
            item.subtemas.forEach { SubtemaItem(it) }
        }
    }
}

@Composable
fun SubtemaItem(item: SubtemaUiItem) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable {}, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text("• ${item.titulo}", style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
        Surface(shape = RoundedCornerShape(50), color = Color(0xFFF0F4C3)) {
            Text(item.temasCount.toString(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF9CCC65), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
        }
    }
}

@Composable
fun ForoTemasScreen(temasRecientes: List<TemaRecienteUi>, isLoading: Boolean, onBack: () -> Unit, onNavigateToDetalle: (String) -> Unit) {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }
    Scaffold(containerColor = Color(0xFFF5F6F8)) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            HeaderForoTemas(onBack)
            SearchBarForo(searchText, { searchText = it })
            FiltrosForo(selectedFilter) { selectedFilter = it }
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(temasRecientes) { tema -> TemaRecienteCard(tema, onNavigateToDetalle) }
                }
            }
        }
    }
}

@Composable
fun HeaderForoTemas(onBack: () -> Unit) {
    Surface(color = Color.White, shadowElevation = 4.dp) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp).statusBarsPadding()) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Atrás") }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Discusiones Recientes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Text("Explora ideas, resuelve dudas y conecta con otros miembros.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray, modifier = Modifier.padding(start = 56.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { }, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937)), modifier = Modifier.align(Alignment.End)) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Nuevo Tema", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarForo(searchText: String, onSearchTextChanged: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp).background(Color.White, RoundedCornerShape(12.dp)).border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = searchText, onValueChange = onSearchTextChanged, placeholder = { Text("Buscar...") }, leadingIcon = { Icon(Icons.Outlined.Search, null, tint = Color.Gray) },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = "dd/mm/aaaa", onValueChange = {}, readOnly = true, trailingIcon = { Icon(Icons.Default.CalendarToday, null, tint = Color.Gray) },
                        modifier = Modifier.width(150.dp).height(56.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {}, modifier = Modifier.background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)).size(56.dp)) { Icon(Icons.Default.Refresh, "Buscar", tint = Color.White) }
                }
            },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
        )
    }
}

@Composable
fun FiltrosForo(selectedFilter: String, onFilterSelected: (String) -> Unit) {
    val filters = listOf("Todos", "Populares", "Sin Respuesta", "Mis Temas")
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filters) { filter ->
            FilterChip(
                label = { Text(filter, fontWeight = FontWeight.SemiBold) }, selected = selectedFilter == filter, onClick = { onFilterSelected(filter) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = Color.White, containerColor = Color.White, labelColor = Color.DarkGray),
                border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selectedFilter == filter, borderColor = if (selectedFilter == filter) MaterialTheme.colorScheme.primary else Color.LightGray)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemaRecienteCard(item: TemaRecienteUi, onClick: (String) -> Unit) {
    Card(onClick = { onClick(item.id) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { ChipTag(item.categoria) }
                Spacer(modifier = Modifier.height(8.dp))
                Text(item.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("Haga clic para ver...", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(top = 4.dp), overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.autor, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("• ${item.ubicacion}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(item.respuestasCount.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("RESPUESTAS", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ChipTag(text: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE0F7FA)) {
        Text(text, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF00ACC1), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemaDetalleScreen(onBack: () -> Unit, viewModel: ForoViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = { TopAppBar(title = { Text(state.detalle.tema?.titulo ?: "Detalle", fontWeight = FontWeight.SemiBold) }, navigationIcon = { IconButton(onClick = { onBack(); viewModel.clearDetalle() }) { Icon(Icons.Default.ArrowBack, "Atrás") } }) },
        containerColor = Color(0xFFF5F6F8)
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (state.detalle.tema != null) {
            LazyColumn(contentPadding = paddingValues, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { TemaOriginalCard(state.detalle.tema!!) }
                item { Text("${state.detalle.comentarios.size} Respuestas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
                items(state.detalle.comentarios) { ComentarioCard(it) }
                item { EscribirRespuestaCard(); Spacer(modifier = Modifier.height(16.dp)) }
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) { Text(state.error.orEmpty(), color = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
fun TemaOriginalCard(tema: TemaDetalle) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(tema.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(tema.contenido, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray, modifier = Modifier.padding(vertical = 12.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
            Row(modifier = Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text(tema.autor.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold) }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(tema.autor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Text(tema.rolAutor, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ComentarioCard(comentario: Comentario) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
            Surface(shape = CircleShape, color = Color(0xFFE3F2FD), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) { Text(comentario.inicialAutor, color = Color(0xFF1E88E5), fontWeight = FontWeight.Bold) }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(comentario.autor, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Text(comentario.fecha, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(comentario.contenido, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscribirRespuestaCard() {
    var respuesta by remember { mutableStateOf("") }
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).background(Color.White, RoundedCornerShape(16.dp)).border(1.dp, Color.LightGray, RoundedCornerShape(16.dp)).padding(end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = respuesta, onValueChange = { respuesta = it }, placeholder = { Text("Escribe tu respuesta...") }, modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
        )
        IconButton(onClick = { }, enabled = respuesta.isNotBlank(), colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White), modifier = Modifier.size(48.dp)) {
            Icon(Icons.Default.Send, "Enviar")
        }
    }
}