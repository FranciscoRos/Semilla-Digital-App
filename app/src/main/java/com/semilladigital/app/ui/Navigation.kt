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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.semilladigital.auth.ui.login.LoginScreen
import com.semilladigital.app.core.data.storage.SessionStorage
import com.semilladigital.dashboard.ui.DashboardScreen
import com.semilladigital.courses.ui.CourseScreen
import com.semilladigital.auth.ui.register.RegisterScreen
import com.semilladigital.chatbot.presentation.ChatScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val COURSES = "courses"
    const val CHATBOT = "chatbot"
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
        Routes.COURSES
    )

    Scaffold(
        floatingActionButton = {
            if (currentRoute in showBotButtonIn) {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.CHATBOT) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.SmartToy, contentDescription = "Chatbot")
                }
            }
        }
    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.fillMaxSize()
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
                    onNavigateToCourses = { navController.navigate(Routes.COURSES) },
                    onNavigateToSupports = { },
                    onNavigateToChatbot = { navController.navigate(Routes.CHATBOT) },
                    onNavigateToGeomap = { }
                )
            }

            composable(Routes.COURSES) {
                CourseScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Routes.CHATBOT) {
                ChatScreen(
                    onBackClick = { navController.popBackStack() }
                )
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