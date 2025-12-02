package com.semilladigital.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.semilladigital.auth.ui.login.LoginScreen
import com.semilladigital.app.core.data.storage.SessionStorage
import com.semilladigital.dashboard.ui.DashboardScreen
import com.semilladigital.courses.ui.CourseScreen
import com.semilladigital.auth.ui.register.RegisterScreen
import com.semilladigital.chatbot.presentation.ChatScreen
import com.semilladigital.geomap.ui.GeomapScreen
import com.semilladigital.supports.presentation.ApoyosScreen
// Importaciones del módulo foro
import com.semilladigital.forum.ui.ForoCategoriasScreen
import com.semilladigital.forum.ui.ForoTemasScreen
import com.semilladigital.forum.ui.TemaDetalleScreen
import com.semilladigital.forum.ui.ForoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val COURSES = "courses?id={id}"
    const val SUPPORTS = "supports?id={id}"
    const val CHATBOT = "chatbot"
    const val FORUM_CATEGORIES = "forum_categories"
    const val FORUM_TEMAS = "forum_temas"
    const val FORUM_DETALLE = "forum_detalle/{temaId}"
    const val GEOMAP = "geomap"
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        val token = sessionStorage.getToken()

        if (token.isNullOrEmpty()) {
            _startDestination.value = Routes.LOGIN
        } else {
            _startDestination.value = Routes.DASHBOARD
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBotButtonIn = listOf(
        Routes.DASHBOARD,
        "courses",
        "supports",
        Routes.FORUM_CATEGORIES,
        Routes.FORUM_TEMAS,
        Routes.GEOMAP
    )

    val shouldShowBot = showBotButtonIn.any {
        currentRoute?.startsWith(it.substringBefore("?")) == true
    }

    Scaffold(
        floatingActionButton = {
            if (shouldShowBot) {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.CHATBOT) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.SmartToy, contentDescription = "Chatbot")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.SPLASH
            ) {
                composable(Routes.SPLASH) {
                    SplashScreen(navController = navController)
                }

                composable(Routes.LOGIN) {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate(Routes.DASHBOARD) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        },
                        onNavigateToRegister = {
                            navController.navigate(Routes.REGISTER)
                        }
                    )
                }

                composable(Routes.REGISTER) {
                    RegisterScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.DASHBOARD) {
                    DashboardScreen(
                        onNavigateToCourses = { id ->
                            val route = if (id != null) "courses?id=$id" else "courses"
                            navController.navigate(route)
                        },
                        onNavigateToSupports = { id ->
                            val route = if (id != null) "supports?id=$id" else "supports"
                            navController.navigate(route)
                        },
                        onNavigateToChatbot = { navController.navigate(Routes.CHATBOT) },
                        onNavigateToGeomap = { navController.navigate(Routes.GEOMAP) },
                        onNavigateToForum = { navController.navigate(Routes.FORUM_CATEGORIES) },
                        onNavigateToLogin = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0)
                            }
                        }
                    )
                }

                composable(
                    route = Routes.COURSES,
                    arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true })
                ) {
                    CourseScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Routes.SUPPORTS,
                    arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true })
                ) {
                    ApoyosScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.CHATBOT) {
                    ChatScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // --- FORO: CATEGORÍAS (Ruta inicial) ---
                composable(Routes.FORUM_CATEGORIES) {
                    val viewModel = hiltViewModel<ForoViewModel>()
                    val state by viewModel.state.collectAsState()

                    ForoCategoriasScreen(
                        categorias = state.categorias,
                        isLoading = state.isLoading,
                        onNavigateToTemasList = { navController.navigate(Routes.FORUM_TEMAS) }
                    )
                }

                // --- FORO: LISTA DE TEMAS/DISCUSIONES ---
                composable(Routes.FORUM_TEMAS) {
                    val viewModel = hiltViewModel<ForoViewModel>()
                    val state by viewModel.state.collectAsState()

                    ForoTemasScreen(
                        temasRecientes = state.temasRecientes,
                        isLoading = state.isLoading,
                        onBack = { navController.popBackStack() },
                        onNavigateToDetalle = { temaId ->
                            // El ViewModel carga el detalle aquí, pero la navegación
                            // al detalle usa el ID para que la ruta sea única.
                            navController.navigate("forum_detalle/$temaId")
                        }
                    )
                }

                // --- FORO: DETALLE DEL TEMA ---
                composable(
                    route = Routes.FORUM_DETALLE,
                    arguments = listOf(navArgument("temaId") { type = NavType.StringType; nullable = false })
                ) { backStackEntry ->
                    val temaId = backStackEntry.arguments?.getString("temaId")

                    val viewModel = hiltViewModel<ForoViewModel>()
                    LaunchedEffect(temaId) {
                        if (temaId != null && viewModel.state.value.detalle.tema == null) {
                            viewModel.loadTemaDetalle(temaId)
                        }
                    }

                    TemaDetalleScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.GEOMAP) {
                    GeomapScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val destination by viewModel.startDestination.collectAsState()

    LaunchedEffect(destination) {
        destination?.let { route ->
            navController.navigate(route) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}