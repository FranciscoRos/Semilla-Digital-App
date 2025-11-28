package com.semilladigital.dashboard.ui.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modificar Información") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF15803D),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.saveChanges() },
                containerColor = Color(0xFF15803D),
                contentColor = Color.White
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Save, contentDescription = "Actualizar Información")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F6F8))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {

                SectionHeader(title = "Información Básica", icon = Icons.Default.Person)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        RegisterTextField("Nombre *", state.nombre) { viewModel.onNombreChange(it) }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                RegisterTextField("Primer Apellido *", state.apellido1) { viewModel.onApellido1Change(it) }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                RegisterTextField("Segundo Apellido", state.apellido2) { viewModel.onApellido2Change(it) }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                RegisterTextField("CURP *", state.curp) { viewModel.onCurpChange(it) }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                RegisterTextField("RFC *", state.rfc) { viewModel.onRfcChange(it) }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                RegisterTextField("Teléfono *", state.telefono, KeyboardType.Phone) { viewModel.onTelefonoChange(it) }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                RegisterTextField("Fecha Nacimiento *", state.fechaNacimiento) { viewModel.onFechaChange(it) }
                            }
                        }

                        RegisterTextField("Correo *", state.correo, KeyboardType.Email, enabled = false) { }
                        RegisterTextField("INE (Reverso) *", state.ine) { viewModel.onIneChange(it) }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionHeader(title = "Domicilio", icon = Icons.Default.Home)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        RegisterTextField("Calle *", state.calle) { viewModel.onCalleChange(it) }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                RegisterTextField("Colonia *", state.colonia) { viewModel.onColoniaChange(it) }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                RegisterTextField("Código Postal *", state.codigoPostal, KeyboardType.Number) { viewModel.onCodigoPostalChange(it) }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                RegisterTextField("Municipio *", state.municipio) { viewModel.onMunicipioChange(it) }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                RegisterTextField("Ciudad *", state.ciudad) { viewModel.onCiudadChange(it) }
                            }
                        }

                        RegisterTextField("Estado *", state.estadoDir) { viewModel.onEstadoDirChange(it) }
                        RegisterTextField("Referencias", state.referencia, singleLine = false) { viewModel.onReferenciaChange(it) }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (state.parcelas.isNotEmpty()) {
                    SectionHeader(title = "Parcelas Asociadas", icon = Icons.Default.Landscape)

                    state.parcelas.forEachIndexed { index, parcela ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Parcela ${index + 1}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color(0xFF15803D),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        color = Color(0xFFE8F5E9),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "${parcela.area} ha",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF1B5E20)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                RegisterTextField("Nombre Parcela", parcela.nombre) {
                                    viewModel.onParcelaFieldChange(index, "nombre", it)
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        RegisterTextField("Municipio", parcela.municipio) {
                                            viewModel.onParcelaFieldChange(index, "municipio", it)
                                        }
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        RegisterTextField("Localidad", parcela.localidad) {
                                            viewModel.onParcelaFieldChange(index, "localidad", it)
                                        }
                                    }
                                }

                                RegisterTextField("Dirección Adicional", parcela.direccionAdicional) {
                                    viewModel.onParcelaFieldChange(index, "direccion", it)
                                }

                                RegisterTextField("Área (Hectáreas)", parcela.area, KeyboardType.Number) {
                                    viewModel.onParcelaFieldChange(index, "area", it)
                                }

                                Divider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Usos y Actividades",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color(0xFF1565C0),
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(onClick = { viewModel.addUsoToParcela(index) }) {
                                        Icon(Icons.Default.Add, contentDescription = "Agregar Uso", tint = Color(0xFF1565C0))
                                    }
                                }

                                parcela.usos.forEachIndexed { usoIndex, uso ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                DropdownSelector(
                                                    label = "Área Productiva",
                                                    selectedValue = uso.area,
                                                    options = viewModel.areasCatalogo,
                                                    onValueChange = { viewModel.updateUsoArea(index, usoIndex, it) },
                                                    modifier = Modifier.weight(1f)
                                                )
                                                IconButton(onClick = { viewModel.removeUsoFromParcela(index, usoIndex) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar Uso", tint = Color.Red)
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            val actividadesDisponibles = viewModel.actividadesCatalogo[uso.area] ?: emptyList()

                                            DropdownSelector(
                                                label = "Agregar Actividad",
                                                selectedValue = "",
                                                options = actividadesDisponibles,
                                                onValueChange = {
                                                    if (it.isNotEmpty()) viewModel.addActividadToUso(index, usoIndex, it)
                                                },
                                                placeholder = "Seleccionar..."
                                            )

                                            if (uso.actividades.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    uso.actividades.forEach { act ->
                                                        Surface(
                                                            color = Color(0xFFE3F2FD),
                                                            shape = RoundedCornerShape(16.dp),
                                                            border = BorderStroke(1.dp, Color(0xFF90CAF9))
                                                        ) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                            ) {
                                                                Text(act, style = MaterialTheme.typography.bodySmall, color = Color(0xFF1565C0))
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Icon(
                                                                    Icons.Default.Close,
                                                                    contentDescription = "Quitar",
                                                                    modifier = Modifier.size(14.dp).clickable {
                                                                        viewModel.removeActividadFromUso(index, usoIndex, act)
                                                                    },
                                                                    tint = Color(0xFF1565C0)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                SectionHeader(title = "Información Adicional", icon = Icons.Default.Assignment)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Sistema de Riego",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFF1565C0),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "¿Cuenta con un sistema de riego? *", fontSize = 14.sp)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = state.tieneRiego.equals("si", ignoreCase = true),
                                onClick = { viewModel.onRiegoChange("Si") }
                            )
                            Text("Sí")
                            Spacer(modifier = Modifier.width(16.dp))
                            RadioButton(
                                selected = state.tieneRiego.equals("no", ignoreCase = true),
                                onClick = { viewModel.onRiegoChange("No") }
                            )
                            Text("No")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Personal",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFF1565C0),
                            fontWeight = FontWeight.Bold
                        )
                        RegisterTextField("Trabajadores a su mando *", state.trabajadores, KeyboardType.Number) { viewModel.onTrabajadoresChange(it) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Surface(
            color = Color(0xFFE3F2FD),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.size(32.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.padding(4.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF374151)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = ""
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue.ifEmpty { placeholder },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun RegisterTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedBorderColor = Color(0xFF15803D),
                disabledBorderColor = Color(0xFFE5E7EB),
                disabledTextColor = Color.DarkGray
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}