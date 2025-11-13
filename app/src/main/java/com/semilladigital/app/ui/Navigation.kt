package com.semilladigital.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.semilladigital.auth.ui.login.LoginScreen
import com.semilladigital.app.core.data.storage.SessionStorage
import com.semilladigital.dashboard.ui.DashboardScreen
import com.semilladigital.courses.ui.CourseScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// Define los nombres de las rutas
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val COURSES = "courses"
}

// ViewModel para la lógica de arranque (comprobar sesión)
@HiltViewModel
class MainViewModel @Inject constructor(
    val sessionStorage: SessionStorage
) : ViewModel()

// --- ESTA ES LA PANTALLA DE NAVEGACIÓN PRINCIPAL ---
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH // El inicio SIEMPRE es el Splash
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
            // RegisterScreen(...)
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onNavigateToCourses = { navController.navigate(Routes.COURSES) },
                onNavigateToSupports = { /* TODO */ },
                onNavigateToChatbot = { /* TODO */ },
                onNavigateToGeomap = { /* TODO */ }
                // TODO: Añadir botón de Logout
            )
        }

        composable(Routes.COURSES) {
            CourseScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

// --- PANTALLA LÓGICA DE SPLASH (CORREGIDA) ---
@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    // --- ¡CAMBIO AQUÍ! ---
    // 1. Usamos un valor inicial "centinela" que sea ÚNICO.
    val authToken by viewModel.sessionStorage.authTokenFlow.collectAsState(initial = "LOADING")

    LaunchedEffect(authToken) {
        // 2. Esperamos a que el valor deje de ser "LOADING"
        if (authToken != "LOADING") {

            // 3. Ahora la lógica es segura.
            // Si el token es null (no existe) o está vacío, vamos a LOGIN
            val route = if (authToken.isNullOrEmpty()) {
                Routes.LOGIN
            } else {
                Routes.DASHBOARD // Sí hay token, vamos al Dashboard
            }

            navController.navigate(route) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        }
    }

    // 4. Mientras tanto, muestra un indicador de carga
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}