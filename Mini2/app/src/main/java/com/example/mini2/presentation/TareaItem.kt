package com.example.mini2.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.mini2.data.Tarea
import java.text.DateFormat
import java.util.Date

@Composable
fun TareaItem(
    tarea: Tarea,
    habilitada: Boolean,
    onEstado: (Boolean) -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val fecha = remember(tarea.fechaCreacion) {
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
            .format(Date(tarea.fechaCreacion))
    }
    Card(modifier = Modifier.fillMaxWidth().testTag("tarea_${tarea.id}")) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = onEstado,
                enabled = habilitada,
                modifier = Modifier.semantics {
                    contentDescription = "Cambiar estado de ${tarea.titulo}"
                }
            )
            Column(
                modifier = Modifier.weight(1f).padding(top = 12.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else null
                )
                if (tarea.descripcion.isNotBlank()) {
                    Text(tarea.descripcion, style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    text = if (tarea.completada) "Completada" else "Pendiente",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(fecha, style = MaterialTheme.typography.bodySmall)
            }
            Column {
                IconButton(onClick = onEditar, enabled = habilitada) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar ${tarea.titulo}")
                }
                IconButton(onClick = onEliminar, enabled = habilitada) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar ${tarea.titulo}")
                }
            }
        }
    }
}
