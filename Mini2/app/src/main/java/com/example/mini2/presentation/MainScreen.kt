package com.example.mini2.presentation

import android.database.SQLException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.mini2.data.Tarea
import com.example.mini2.data.TareaDao
import com.example.mini2.domain.AppDatabase
import com.example.mini2.ui.theme.Mini2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val db = remember(context) { AppDatabase.getDatabase(context) }
    val dao = remember(db) { db.tareaDao() }
    Mini2Theme {
        PantallaTareas(dao)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PantallaTareas(dao: TareaDao) {
    val coroutineScope = rememberCoroutineScope()
    // La única fuente de datos es Room; collectAsState actualiza la interfaz.
    val tareasFlow = remember(dao) { dao.obtenerTodas() }
    val tareas by tareasFlow.collectAsState(initial = emptyList())
    var filtro by rememberSaveable { mutableStateOf(FiltroTareas.TODAS) }
    var orden by rememberSaveable { mutableStateOf(OrdenTareas.FECHA) }
    val visibles = remember(tareas, filtro, orden) { filtrarYOrdenar(tareas, filtro, orden) }
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }
    var idEnEdicion by rememberSaveable { mutableStateOf<Int?>(null) }
    var confirmarBorrado by rememberSaveable { mutableStateOf(false) }
    var procesando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val tareaEnEdicion = tareas.firstOrNull { it.id == idEnEdicion }

    // Las escrituras van a IO; los cambios de estado de Compose regresan a Main.
    fun escribir(operacion: suspend () -> Unit, alTerminar: () -> Unit = {}) {
        if (procesando) return
        procesando = true
        error = null
        coroutineScope.launch {
            try {
                withContext(Dispatchers.IO) { operacion() }
                alTerminar()
            } catch (_: SQLException) {
                error = "No se pudo guardar el cambio. Intenta de nuevo."
            } finally {
                procesando = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administrador de Tareas", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    MenuTareas(
                        filtro = filtro,
                        orden = orden,
                        puedeBorrar = !procesando && tareas.any { it.completada },
                        onFiltro = { filtro = it },
                        onOrden = { orden = it },
                        onBorrarCompletadas = { confirmarBorrado = true }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (!procesando) {
                    idEnEdicion = null
                    error = null
                    mostrarDialogo = true
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Vista: ${filtro.etiqueta}", style = MaterialTheme.typography.titleSmall)
                Text("Orden: ${orden.etiqueta}", style = MaterialTheme.typography.bodySmall)
                if (!mostrarDialogo) {
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            }
            if (visibles.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (tareas.isEmpty()) "No hay tareas registradas" else "No hay tareas para este filtro")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth().testTag("lista_tareas"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visibles, key = { it.id }) { tarea ->
                        TareaItem(
                            tarea = tarea,
                            habilitada = !procesando,
                            onEstado = { completada ->
                                escribir({ dao.actualizar(tarea.copy(completada = completada)) })
                            },
                            onEditar = {
                                idEnEdicion = tarea.id
                                error = null
                                mostrarDialogo = true
                            },
                            onEliminar = { escribir({ dao.eliminar(tarea) }) }
                        )
                    }
                }
            }
        }
    }

    // Al restaurar la pantalla esperamos a Room antes de abrir una edición existente.
    if (mostrarDialogo && (idEnEdicion == null || tareaEnEdicion != null)) {
        DialogoTarea(
            tarea = tareaEnEdicion,
            guardando = procesando,
            error = error,
            onCerrar = { mostrarDialogo = false; error = null },
            onGuardar = { titulo, descripcion ->
                if (titulo.isNotBlank()) {
                    escribir(
                        operacion = {
                            if (tareaEnEdicion == null) {
                                dao.insertar(Tarea(titulo = titulo, descripcion = descripcion))
                            } else {
                                // copy mantiene id, fecha y estado: editar no crea otra fila.
                                dao.actualizar(tareaEnEdicion.copy(titulo = titulo, descripcion = descripcion))
                            }
                        },
                        alTerminar = { mostrarDialogo = false; idEnEdicion = null }
                    )
                }
            }
        )
    }
    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = { if (!procesando) confirmarBorrado = false },
            title = { Text("¿Borrar tareas completadas?") },
            text = { Text("Se eliminarán las tareas completadas. Las pendientes se conservarán.") },
            confirmButton = {
                TextButton(
                    enabled = !procesando,
                    onClick = {
                        escribir({ dao.eliminarCompletadas() }, { confirmarBorrado = false })
                    }
                ) { Text("Borrar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBorrado = false }, enabled = !procesando) {
                    Text("Cancelar")
                }
            }
        )
    }
}
