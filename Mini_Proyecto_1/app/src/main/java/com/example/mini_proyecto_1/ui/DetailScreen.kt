package com.example.mini_proyecto_1.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    matricula: String,
    nombre: String,
    carrera: String,
    turno: String,
    estatus: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle de Registro") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Matrícula: $matricula", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Nombre: $nombre", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Carrera: $carrera", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Turno: $turno", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Estatus: $estatus", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al Inicio")
            }
        }
    }
}