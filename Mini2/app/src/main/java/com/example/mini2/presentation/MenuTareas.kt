package com.example.mini2.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun MenuTareas(
    filtro: FiltroTareas,
    orden: OrdenTareas,
    puedeBorrar: Boolean,
    onFiltro: (FiltroTareas) -> Unit,
    onOrden: (OrdenTareas) -> Unit,
    onBorrarCompletadas: () -> Unit
) {
    // El menú siempre se cierra después de seleccionar una opción.
    var expandido by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expandido = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
        }
        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            FiltroTareas.entries.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion.etiqueta) },
                    trailingIcon = {
                        if (filtro == opcion) Icon(Icons.Default.Check, contentDescription = "Seleccionado")
                    },
                    onClick = { expandido = false; onFiltro(opcion) }
                )
            }
            HorizontalDivider()
            OrdenTareas.entries.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(if (opcion == OrdenTareas.FECHA) "Ordenar por fecha" else "Ordenar por estado") },
                    trailingIcon = {
                        if (orden == opcion) Icon(Icons.Default.Check, contentDescription = "Seleccionado")
                    },
                    onClick = { expandido = false; onOrden(opcion) }
                )
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Borrar completadas") },
                enabled = puedeBorrar,
                onClick = { expandido = false; onBorrarCompletadas() }
            )
        }
    }
}
