package com.semilladigital.auth.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.semilladigital.app.core.ui.SemillaDigitalTheme
import com.semilladigital.app.core.ui.SemillaScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isRegistered) {
        if (state.isRegistered) {
            onBack()
        }
    }

    SemillaDigitalTheme {
        // Usamos un Scaffold vacío porque controlaremos el diseño manualmente
        // para que se parezca más a tu web (fondo gris, cards blancas)
        Scaffold(
            containerColor = Color(0xFFF9FAFB) // bg-gray-50
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // --- Header con botón volver ---
                TextButton(
                    onClick = onBack,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Volver a Login", fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "Crear Cuenta de Productor",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Text(
                    "Completa el siguiente formulario para registrarte",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4B5563),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // --- SECCIÓN 1: Información Personal ---
                FormSection(title = "Información Personal") {
                    RegisterInput(
                        label = "Nombre Completo *",
                        value = state.fullName,
                        onValueChange = viewModel::onFullNameChange
                    )
                    RegisterInput(
                        label = "CURP *",
                        value = state.curp,
                        onValueChange = viewModel::onCurpChange,
                        placeholder = "CURP de 18 caracteres"
                    )
                    RegisterInput(
                        label = "Correo Electrónico *",
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        keyboardType = KeyboardType.Email
                    )
                    RegisterInput(
                        label = "Teléfono",
                        value = state.phone,
                        onValueChange = viewModel::onPhoneChange,
                        keyboardType = KeyboardType.Phone
                    )
                }

                Spacer(Modifier.height(24.dp))

                // --- SECCIÓN 2: Domicilio ---
                FormSection(title = "Domicilio") {
                    Text("Municipio *", style = MaterialTheme.typography.labelMedium, color = Color(0xFF374151))
                    Spacer(Modifier.height(4.dp))
                    SimpleDropdown(
                        options = state.availableMunicipalities,
                        selectedOption = state.municipality,
                        onOptionSelected = viewModel::onMunicipalityChange,
                        placeholder = "Seleccionar municipio"
                    )

                    Spacer(Modifier.height(16.dp))

                    RegisterInput(
                        label = "Dirección *",
                        value = state.address,
                        onValueChange = viewModel::onAddressChange
                    )
                }

                Spacer(Modifier.height(24.dp))

                // --- SECCIÓN 3: Mapa de Parcela ---
                FormSection(title = "Ubicación de tu Parcela") {
                    // Placeholder del Mapa
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color(0xFFDCFCE7), RoundedCornerShape(8.dp)) // green-100
                            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp)), // Dashed border sim
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Layers,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text("Mapa interactivo para dibujar tu parcela", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4B5563))
                            Text("Este campo es obligatorio", style = MaterialTheme.typography.labelSmall, color = Color(0xFF6B7280))
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Botón de Dibujar
                    val buttonColor = if (state.mapDrawn) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.primary
                    val contentColor = if (state.mapDrawn) MaterialTheme.colorScheme.primary else Color.White

                    Button(
                        onClick = viewModel::onMapDrawn,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor, contentColor = contentColor)
                    ) {
                        if (state.mapDrawn) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Parcela dibujada")
                        } else {
                            Text("Dibujar polígono de parcela")
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --- SECCIÓN 4: Contraseña ---
                FormSection(title = "Crear Contraseña") {
                    RegisterInput(
                        label = "Contraseña *",
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        isPassword = true
                    )
                    RegisterInput(
                        label = "Confirmar Contraseña *",
                        value = state.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        isPassword = true
                    )
                }

                Spacer(Modifier.height(24.dp))

                if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // --- Botones Finales ---
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = viewModel::onRegisterClick,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Crear Cuenta")
                        }
                    }

                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF111827))
                    ) {
                        Text("Cancelar")
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// --- COMPONENTES REUTILIZABLES ---

@Composable
fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun RegisterInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color(0xFF374151))
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = if (placeholder != null) { { Text(placeholder, color = Color.Gray) } } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFD1D5DB),
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDropdown(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    placeholder: String
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = if (selectedOption.isEmpty()) placeholder else selectedOption
    val textColor = if (selectedOption.isEmpty()) Color.Gray else Color.Black

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            readOnly = true,
            value = displayText,
            onValueChange = {},
            textStyle = LocalTextStyle.current.copy(color = textColor),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFD1D5DB),
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}