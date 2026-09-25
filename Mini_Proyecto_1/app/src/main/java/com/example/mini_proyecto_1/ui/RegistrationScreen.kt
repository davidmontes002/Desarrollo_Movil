package com.example.mini_proyecto_1.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mini_proyecto_1.data.PreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(onNavigateToDetail: (String, String, String, String, String) -> Unit) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }

    // Estados de los campos
    var matricula by remember { mutableStateOf(preferencesManager.getMatricula()) }
    var nombre by remember { mutableStateOf("") }

    // Variables para el Spinner (DropdownMenu)
    var expanded by remember { mutableStateOf(false) }
    var carrera by remember { mutableStateOf("Ingeniería de Software") }

    // RadioButton y Switch
    var turno by remember { mutableStateOf("Matutino") }
    var isActive by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Registro de Estudiantes") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TextFields
            OutlinedTextField(
                value = matricula,
                onValueChange = { matricula = it },
                label = { Text("Matrícula") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )

            // Spinner / DropdownMenu
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = carrera,
                    onValueChange = {},
                    readOnly = true,
                label = { Text("Carrera") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                )
                DropdownMenu(
                    expanded = expanded,
                onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Ingeniería de Software") },
                        onClick = { carrera = "Ingeniería de Software"; expanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Sistemas Computacionales") },
                        onClick = { carrera = "Sistemas Computacionales"; expanded = false }
                    )
                }
            }

            // RadioButtons para Turno
            Text("Turno:")
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (turno == "Matutino"),
                onClick = { turno = "Matutino" }
                )
                Text("Matutino", modifier = Modifier.clickable { turno = "Matutino" })

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = (turno == "Vespertino"),
                onClick = { turno = "Vespertino" }
                )
                Text("Vespertino", modifier = Modifier.clickable { turno = "Vespertino" })
            }

            // Switch para Estatus
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
            ) {
            Text("Estatus Activo:")
            Switch(
                checked = isActive,
            onCheckedChange = { isActive = it }
            )
        }

            // Botón de Guardar y Navegar
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (matricula.isNotBlank() && nombre.isNotBlank()) {
                        // Persistencia Local
                        preferencesManager.saveMatricula(matricula)

                        // Navegación
                        val estatusStr = if (isActive) "Activo" else "Inactivo"
                        onNavigateToDetail(matricula, nombre, carrera, turno, estatusStr)
                    } else {
                        Toast.makeText(context, "Llena los campos requeridos", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Registrar y Ver Detalles")
            }
        }
    }
}