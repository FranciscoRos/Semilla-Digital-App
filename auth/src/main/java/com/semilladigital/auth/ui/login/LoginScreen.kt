package com.semilladigital.auth.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.semilladigital.app.core.ui.SemillaDigitalTheme // Importa tu tema

@OptIn(ExperimentalMaterial3Api::class) // Para Scaffold
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit, // Callback para navegar al Dashboard
    onNavigateToRegister: () -> Unit // Callback para navegar al Registro
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Observa el estado 'loginSuccess' para navegar
    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            onLoginSuccess()
        }
    }

    SemillaDigitalTheme {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)

                    // Campo de Email
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { viewModel.onEvent(LoginEvent.OnEmailChanged(it)) },
                        label = { Text("Correo Electrónico") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Campo de Contraseña
                    OutlinedTextField(
                        value = state.contrasena,
                        onValueChange = { viewModel.onEvent(LoginEvent.OnPasswordChanged(it)) },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // --- INICIO DE LA CORRECCIÓN ---
                    // 1. Crea una copia local "safe" de la variable de error
                    val error = state.error

                    // 2. Comprueba la copia local
                    if (error != null) {
                        Text(
                            // 3. Usa la copia local (ahora 100% no nula)
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    // --- FIN DE LA CORRECCIÓN ---

                    // Botón de Login
                    Button(
                        onClick = { viewModel.onEvent(LoginEvent.OnLoginClick) },
                        enabled = !state.isLoading, // Desactivado si está cargando
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Entrar")
                        }
                    }

                    // Botón de Registro
                    TextButton(onClick = onNavigateToRegister) {
                        Text("¿No tienes cuenta? Regístrate")
                    }
                }
            }
        }
    }
}