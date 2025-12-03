package com.semilladigital.forum.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.semilladigital.forum.domain.model.Categoria
import com.semilladigital.forum.domain.model.TemaDetalle

// --- HELPER PARA EL TOAST ---
@Composable
fun showWipToast(): () -> Unit {
    val context = LocalContext.current
    return {
        Toast.makeText(context, "🛠️ Estamos trabajando en esta funcionalidad, disculpa las molestias.", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ForoCategoriasScreen(
    categorias: List<CategoriaUiItem>,
    isLoading: Boolean,
    onNavigateToTemasList: () -> Unit,
    onBack: () -> Unit // <--- 1. Nuevo parámetro para regresar al home de la app
) {
    val scrollState = rememberScrollState()
    Scaffold(containerColor = Color(0xFFF5F6F8)) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(scrollState)) {
            // Pasamos onBack al header
            HeaderForoCategorias(onNavigateToTemasList, onBack)

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                // Renderizado de tarjetas (sin cambios de lógica estructural, solo visual)
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Nota: Si tienes muchas categorías, considera un FlowRow o LazyVerticalGrid,
                    // pero para el demo tal cual estaba:
                    if(categorias.isNotEmpty()) {
                        // Renderizamos solo las primeras 2 como en tu original para mantener el diseño
                        categorias.take(2).forEach { categoria ->
                            CategoriaCard(categoria, Modifier.weight(1f))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HeaderForoCategorias(onNavigateToTemasList: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(24.dp)) {

        // --- 1. Botón de Regresar Agregado ---
        IconButton(
            onClick = onBack,
            modifier = Modifier.offset(x = (-12).dp) // Un pequeño ajuste visual para alinearlo a la izquierda
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.Black)
        }

        Text("COMUNIDAD AGRÍCOLA", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
        Text("Foro Semilla Digital", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1F2937), modifier = Modifier.padding(top = 4.dp))
        Text("Un ecosistema digital para compartir sabiduría, conectar generaciones y cultivar el futuro del campo juntos.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
        Spacer(modifier = Modifier.height(24.dp))

        // Este botón SÍ funciona para poder ir a la segunda pantalla
        Button(onClick = onNavigateToTemasList, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937))) {
            Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver Discusiones Recientes", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun CategoriaCard(item: CategoriaUiItem, modifier: Modifier = Modifier) {
    // La tarjeta en sí no es clickeable, pero sus hijos sí
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
    val showToast = showWipToast()

    // --- 2. Clickeable con Toast ---
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { showToast() }, // Acción falsa
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("• ${item.titulo}", style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
        Surface(shape = RoundedCornerShape(50), color = Color(0xFFF0F4C3)) {
            Text(item.temasCount.toString(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF9CCC65), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
        }
    }
}

@Composable
fun ForoTemasScreen(
    temasRecientes: List<TemaRecienteUi>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onNavigateToDetalle: (String) -> Unit
) {
    val showToast = showWipToast()
    var searchText by remember { mutableStateOf("") }
    // Filtro visual solamente
    var selectedFilter by remember { mutableStateOf("Todos") }

    Scaffold(containerColor = Color(0xFFF5F6F8)) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            HeaderForoTemas(onBack) // El back de aquí sí funciona

            // SearchBar falsa
            SearchBarForo(
                searchText = searchText,
                onSearchTextChanged = { searchText = it },
                onFakeAction = showToast
            )

            // Filtros falsos
            FiltrosForo(selectedFilter) {
                selectedFilter = it
                showToast() // Muestra toast al cambiar filtro
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(temasRecientes) { tema ->
                        // --- 2. Clickeable con Toast en lugar de navegar ---
                        TemaRecienteCard(tema) {
                            showToast() // En vez de onNavigateToDetalle, mostramos Toast
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderForoTemas(onBack: () -> Unit) {
    val showToast = showWipToast()

    Surface(color = Color.White, shadowElevation = 4.dp) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp).statusBarsPadding()) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Atrás") }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Discusiones Recientes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Text("Explora ideas, resuelve dudas y conecta con otros miembros.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray, modifier = Modifier.padding(start = 56.dp))
            Spacer(modifier = Modifier.height(16.dp))

            // Botón Nuevo Tema -> Fake
            Button(
                onClick = { showToast() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937)),
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Nuevo Tema", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarForo(searchText: String, onSearchTextChanged: (String) -> Unit, onFakeAction: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp).background(Color.White, RoundedCornerShape(12.dp)).border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChanged,
            placeholder = { Text("Buscar...") },
            leadingIcon = { Icon(Icons.Outlined.Search, null, tint = Color.Gray) },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Botón fecha falso
                    OutlinedTextField(
                        value = "dd/mm/aaaa", onValueChange = {}, readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = onFakeAction) { // Click en calendario -> Toast
                                Icon(Icons.Default.CalendarToday, null, tint = Color.Gray)
                            }
                        },
                        modifier = Modifier.width(150.dp).height(56.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Botón refresh falso
                    IconButton(onClick = onFakeAction, modifier = Modifier.background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)).size(56.dp)) { Icon(Icons.Default.Refresh, "Buscar", tint = Color.White) }
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
                label = { Text(filter, fontWeight = FontWeight.SemiBold) }, selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) }, // Llama al toast que pasamos arriba
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = Color.White, containerColor = Color.White, labelColor = Color.DarkGray),
                border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selectedFilter == filter, borderColor = if (selectedFilter == filter) MaterialTheme.colorScheme.primary else Color.LightGray)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemaRecienteCard(item: TemaRecienteUi, onClick: (String) -> Unit) {
    // onClick aquí recibira el showToast
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

// NO NECESITAS CAMBIOS AQUI ABAJO PARA LA DEMO, PERO SE MANTIENE EL CODIGO
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemaDetalleScreen(onBack: () -> Unit, viewModel: ForoViewModel = hiltViewModel()) {
    // ... (Código original de TemaDetalleScreen)
    // Como nunca navegaremos aquí, no importa si se queda igual.
}