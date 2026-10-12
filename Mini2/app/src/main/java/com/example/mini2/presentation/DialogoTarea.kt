package com.example.mini2.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mini2.data.Tarea

@Composable
fun DialogoTarea(
    tarea: Tarea?,
    guardando: Boolean,
    error: String?,
    onCerrar: () -> Unit,
    onGuardar: (String, String) -> Unit
) {
    // El mismo formulario sirve para crear y editar; conserva el borrador al girar.
    var titulo by rememberSaveable(tarea?.id) { mutableStateOf(tarea?.titulo.orEmpty()) }
    var descripcion by rememberSaveable(tarea?.id) {
        mutableStateOf(tarea?.descripcion.orEmpty())
    }
    AlertDialog(
        onDismissRequest = { if (!guardando) onCerrar() },
        title = { Text(if (tarea == null) "Nueva tarea" else "Editar tarea") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    supportingText = { Text("Obligatorio") },
                    singleLine = true,
                    enabled = !guardando,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    minLines = 2,
                    maxLines = 4,
                    enabled = !guardando,
                    modifier = Modifier.fillMaxWidth()
                )
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (titulo.isNotBlank()) onGuardar(titulo.trim(), descripcion.trim()) },
                enabled = titulo.isNotBlank() && !guardando
            ) {
                Text(if (guardando) "Guardando…" else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar, enabled = !guardando) { Text("Cancelar") }
        }
    )
}
