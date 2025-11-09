package com.semilladigital.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.semilladigital.dashboard.ui.DashboardScreen // Importa el Dashboard
import com.semilladigital.courses.ui.CourseScreen       // Importa los Cursos

// Define los nombres únicos (rutas) para cada pantalla
object Routes {
    const val DASHBOARD = "dashboard"
    const val COURSES = "courses"
    // const val SUPPORTS = "supports"
    // const val CHATBOT = "chatbot"
    // const val GEOMAP = "geomap"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD // Empezamos en el Dashboard
    ) {

        // Pantalla 1: Dashboard
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                // Cuando se presiona "Cursos", navegamos a la ruta de cursos
                onNavigateToCourses = { navController.navigate(Routes.COURSES) },
                onNavigateToSupports = { /* TODO */ },
                onNavigateToChatbot = { /* TODO */ },
                onNavigateToGeomap = { /* TODO */ }
            )
        }

        // Pantalla 2: Cursos
        composable(Routes.COURSES) {
            CourseScreen() // Mostramos la pantalla de cursos que ya hicimos
        }

        // ... Aquí añadiremos las otras rutas (Apoyos, Chatbot, etc.)
    }
}