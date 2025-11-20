package com.semilladigital.auth.ui.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.semilladigital.app.core.ui.SemillaDigitalTheme

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isRegistered) { if (state.isRegistered) onBack() }

    // --- LÓGICA DEL MAPA ---
    if (state.isShowingMap) {
        ParcelMapScreen(
            onPolygonCompleted = { puntos -> viewModel.onPolygonSaved(puntos) },
            onBack = { viewModel.onCloseMap() }
        )
        return
    }

    // --- PANTALLA DEL FORMULARIO ---
    SemillaDigitalTheme {
        Scaffold(containerColor = Color(0xFFF3F4F6)) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                item {
                    TextButton(
                        onClick = onBack,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.offset(x = (-12).dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Volver al Login", fontWeight = FontWeight.Medium)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Registro de usuario y Parcelas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Complete todos los campos", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }

                // Botón Debug
                item {
                    Button(
                        onClick = { viewModel.fillWithDummyData() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F2F1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("⚡ Llenar Datos de Prueba ⚡", color = Color(0xFF00695C))
                    }
                }

                // Sección 1: Información Básica
                item {
                    ExpandableSection(
                        title = "Información Básica",
                        icon = Icons.Default.Person,
                        isExpanded = state.activeSection == "Información Básica",
                        onToggle = { viewModel.toggleSection("Información Básica") }
                    ) {
                        RegisterInput("Nombre *", state.nombre) { viewModel.onNombreChange(it) }
                        RegisterInput("Primer Apellido *", state.apellido1) { viewModel.onApellido1Change(it) }
                        RegisterInput("Segundo Apellido", state.apellido2) { viewModel.onApellido2Change(it) }
                        RegisterInput("CURP *", state.curp) { viewModel.onCurpChange(it) }
                        RegisterInput("Correo Electrónico *", state.correo, KeyboardType.Email) { viewModel.onCorreoChange(it) }
                        RegisterInput("Contraseña *", state.contrasena, KeyboardType.Text, true) { viewModel.onContrasenaChange(it) }
                        RegisterInput("Teléfono *", state.telefono, KeyboardType.Phone) { viewModel.onTelefonoChange(it) }
                        RegisterInput("Fecha de Nacimiento * (aaaa-mm-dd)", state.fechaNacimiento) { viewModel.onFechaNacimientoChange(it) }
                        RegisterInput("Número de INE (reverso) *", state.ine) { viewModel.onIneChange(it) }
                        RegisterInput("RFC *", state.rfc) { viewModel.onRfcChange(it) }
                    }
                }

                // Sección 2: Domicilio
                item {
                    ExpandableSection(
                        title = "Domicilio",
                        icon = Icons.Default.Home,
                        isExpanded = state.activeSection == "Domicilio",
                        onToggle = { viewModel.toggleSection("Domicilio") }
                    ) {
                        RegisterInput("Calle *", state.calle) { viewModel.onCalleChange(it) }
                        RegisterInput("Colonia *", state.colonia) { viewModel.onColoniaChange(it) }

                        Text("Municipio *", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        SimpleDropdown(
                            options = state.availableMunicipalities,
                            selectedOption = state.municipio,
                            onOptionSelected = { viewModel.onMunicipioChange(it) },
                            placeholder = "Seleccionar..."
                        )
                        Spacer(Modifier.height(8.dp))

                        RegisterInput("Ciudad *", state.ciudad) { viewModel.onCiudadChange(it) }
                        RegisterInput("Estado *", state.estado) { viewModel.onEstadoChange(it) }
                        RegisterInput("Código Postal *", state.cp, KeyboardType.Number) { viewModel.onCpChange(it) }
                        RegisterInput("Referencias", state.referencia) { viewModel.onReferenciaChange(it) }
                    }
                }

                // Sección 3: Parcelas
                item {
                    ExpandableSection(
                        title = "Parcelas",
                        icon = Icons.Default.Map,
                        isExpanded = state.activeSection == "Parcelas",
                        onToggle = { viewModel.toggleSection("Parcelas") }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(
                                    if (state.mapDrawn) Color(0xFFDCFCE7) else Color(0xFFF3F4F6),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    if (state.mapDrawn) Icons.Default.CheckCircle else Icons.Default.Layers,
                                    null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(
                                    if (state.mapDrawn) "Parcela delimitada correctamente" else "Mapa Satelital",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.onOpenMap() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.mapDrawn) Color(0xFF059669) else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(if (state.mapDrawn) Icons.Default.Edit else Icons.Default.Create, null)
                            Spacer(Modifier.width(8.dp))
                            Text(if(state.mapDrawn) "Editar Parcela" else "Dibujar Parcela")
                        }
                    }
                }

                // Secciones Dinámicas
                val groupedQuestions = QUESTION_SCHEMA.groupBy { it.section }

                groupedQuestions.forEach { (sectionName, questions) ->
                    item {
                        ExpandableSection(
                            title = sectionName,
                            icon = Icons.Default.ListAlt,
                            isExpanded = state.activeSection == sectionName,
                            onToggle = { viewModel.toggleSection(sectionName) }
                        ) {
                            questions.forEach { question ->
                                val showQuestion = question.conditionalField == null ||
                                        state.dynamicAnswers[question.conditionalField] == question.conditionalValue

                                if (showQuestion) {
                                    DynamicQuestionInput(
                                        question = question,
                                        answer = state.dynamicAnswers[question.fieldName],
                                        onAnswerChange = { viewModel.onDynamicAnswerChange(question.fieldName, it) }
                                    )
                                    Spacer(Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }

                // Botón Registrarse
                item {
                    Spacer(Modifier.height(16.dp))

                    if (state.error != null) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = { viewModel.onRegisterClick() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Save, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Registrarse", fontSize = 18.sp)
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

// UI Components

@Composable
fun ExpandableSection(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isExpanded) Color(0xFF2563EB) else Color.White
                    )
                    .clickable { onToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = if(isExpanded) Color.White else Color.Black)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if(isExpanded) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    null,
                    tint = if(isExpanded) Color.White else Color.Black
                )
            }
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(16.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun RegisterInput(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if(isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            shape = RoundedCornerShape(8.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicQuestionInput(
    question: Question,
    answer: Any?,
    onAnswerChange: (Any) -> Unit
) {
    Column {
        Text(
            text = "${question.questionText}${if(question.required) " *" else ""}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))

        when (question.type) {
            QuestionType.Text -> {
                OutlinedTextField(
                    value = answer as? String ?: "",
                    onValueChange = { onAnswerChange(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            QuestionType.Number -> {
                OutlinedTextField(
                    value = answer.toString().takeIf { it != "null" } ?: "",
                    onValueChange = { onAnswerChange(it.toIntOrNull() ?: 0) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            QuestionType.Select -> {
                SimpleDropdown(
                    options = question.options.map { it.label },
                    selectedOption = question.options.find { it.value == answer }?.label ?: "",
                    onOptionSelected = { label ->
                        val value = question.options.find { it.label == label }?.value ?: label
                        onAnswerChange(value)
                    },
                    placeholder = "Seleccionar..."
                )
            }
            QuestionType.Radio -> {
                question.options.forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onAnswerChange(option.value) }) {
                        RadioButton(
                            selected = (answer as? String) == option.value,
                            onClick = { onAnswerChange(option.value) }
                        )
                        Text(option.label)
                    }
                }
            }
            QuestionType.Checkbox -> {
                val selected = (answer as? List<String>) ?: emptyList()
                question.options.forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selected.contains(option.value),
                            onCheckedChange = { isChecked ->
                                if (isChecked) onAnswerChange(selected + option.value)
                                else onAnswerChange(selected - option.value)
                            }
                        )
                        Text(option.label)
                    }
                }
            }
            else -> {}
        }
    }
}