package com.semilladigital.dashboard.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.semilladigital.chatbot.presentation.ChatViewModel
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    onNavigateToCourses: (String?) -> Unit,
    onNavigateToSupports: (String?) -> Unit,
    onNavigateToChatbot: () -> Unit,
    onNavigateToGeomap: () -> Unit,
    onNavigateToForum: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onNavigateToLogin()
        }
    }

    LaunchedEffect(state.userName, state.userStatus) {
        if (state.userName != "Cargando...") {
            chatViewModel.setContext(
                "El usuario está en el Dashboard. Datos: ${state.userName}, Estatus: ${state.userStatus}."
            )
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F6F8),
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(scrollState)
        ) {
            DashboardHeader(
                userName = state.userName,
                userStatus = state.userStatus,
                onLogout = { viewModel.onLogout() },
                onProfileClick = onNavigateToProfile,
                onHistoryClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                }
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Novedades para ti",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }

                if (state.novedades.isEmpty()) {
                    Text(
                        text = "No hay novedades por ahora.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.novedades) { novedad ->
                            NewsCard(
                                item = novedad,
                                onClick = {
                                    if (novedad.tipo == TipoNovedad.CURSO) {
                                        onNavigateToCourses(novedad.id)
                                    } else {
                                        onNavigateToSupports(novedad.id)
                                    }
                                },
                                modifier = Modifier
                                    .width(320.dp)
                                    .height(115.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Servicios Principales",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ServiceCard(
                        title = "Solicitar\nApoyos",
                        icon = Icons.Default.Assignment,
                        color = Color(0xFF81C784),
                        onClick = { onNavigateToSupports(null) },
                        modifier = Modifier.weight(1f)
                    )
                    ServiceCard(
                        title = "Asistente\nVirtual",
                        icon = Icons.Default.SmartToy,
                        color = Color(0xFF64B5F6),
                        onClick = onNavigateToChatbot,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ServiceCard(
                        title = "Cursos y\nCapacitación",
                        icon = Icons.Default.School,
                        color = Color(0xFFFF8A65),
                        onClick = { onNavigateToCourses(null) },
                        modifier = Modifier.weight(1f)
                    )
                    ServiceCard(
                        title = "Geomapa de\nRecursos",
                        icon = Icons.Default.Map,
                        color = Color(0xFFBA68C8),
                        onClick = onNavigateToGeomap,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ServiceCard(
                        title = "Foro\nComunitario",
                        icon = Icons.Default.Groups,
                        color = Color(0xFFFFD54F),
                        onClick = onNavigateToForum,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                HistoryCard(
                    userStatus = state.userStatus
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DashboardHeader(
    userName: String,
    userStatus: String,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    val statusColor = if (userStatus.equals("Pendiente", ignoreCase = true)) {
        Color(0xFFFF3D00)
    } else {
        Color(0xFF69F0AE)
    }

    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 24.dp, end = 16.dp, top = 16.dp, bottom = 28.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(52.dp),
                        shadowElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = userName.take(1).uppercase(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Bienvenido, $userName",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        if (userStatus.isNotEmpty()) {
                            Surface(
                                color = Color.White.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = statusColor,
                                        shape = CircleShape,
                                        modifier = Modifier.size(8.dp)
                                    ) {}
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = userStatus,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(Color.White, CircleShape)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        IconButton(
                                            onClick = onHistoryClick,
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.History,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = onHistoryClick,
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Notifications,
                                                contentDescription = null,
                                                tint = Color(0xFFFF8A65),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = onProfileClick,
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = null,
                                                tint = Color(0xFF66BB6A),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Cerrar Sesión",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(150.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(14.dp),
                    tint = color
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                lineHeight = 20.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsCard(
    item: NovedadUiItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = if (item.tipo == TipoNovedad.CURSO) Icons.Default.School else Icons.Default.Assignment
    val colorIcon = if (item.tipo == TipoNovedad.CURSO) Color(0xFFFF8A65) else Color(0xFF81C784)

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colorIcon.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(14.dp),
                    tint = colorIcon
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp),
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = item.fecha.take(10),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HistoryCard(userStatus: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF5E35B1))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Revisiones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CITA VERIFICACIÓN", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Surface(color = Color(0xFFF5F6F8), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("No hay cita de verificación programada.", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("REVISIÓN DOCUMENTAL", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }

                Surface(color = Color(0xFFF5F6F8), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Estado Perfil:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Text("Tu documentación está en proceso de revisión.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = userStatus,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFF9800),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("APOYOS ACTIVOS", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Surface(color = Color(0xFFF5F6F8), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("No has solicitado apoyos aún.", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = Color(0xFF5E35B1))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Historial Detallado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {},
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Mis Apoyos", fontSize = 10.sp, color = Color(0xFF15803D))
                    }
                    OutlinedButton(
                        onClick = {},
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Mis Cursos", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(color = Color(0xFFF5F6F8), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(100.dp)) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Inbox, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No tienes historial de apoyos registrado.", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                }
            }
        }
    }
}