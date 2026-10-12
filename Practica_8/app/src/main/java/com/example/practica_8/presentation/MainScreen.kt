package com.example.practica_8.presentation

import android.database.SQLException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.practica_8.data.Usuario
import com.example.practica_8.domain.AppDatabase
import com.example.practica_8.ui.theme.Practica_8Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val db = remember(context) { AppDatabase.getDatabase(context) }
    val dao = remember(db) { db.usuarioDao() }
    val coroutineScope = rememberCoroutineScope()
    // Conservamos el Flow para no reiniciar la consulta en cada recomposición.
    val usuariosFlow = remember(dao) { dao.obtenerTodos() }
    val usuarios by usuariosFlow.collectAsState(initial = emptyList())
    var nuevoNombre by rememberSaveable { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Practica_8Theme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .imePadding()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = nuevoNombre,
                    onValueChange = { nuevoNombre = it },
                    label = { Text("Nombre del usuario") },
                    singleLine = true,
                    enabled = !guardando,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (nuevoNombre.isNotBlank() && !guardando) {
                            val nombreAGuardar = nuevoNombre
                            guardando = true
                            coroutineScope.launch {
                                try {
                                    withContext(Dispatchers.IO) {
                                        dao.insertar(Usuario(nombre = nombreAGuardar))
                                    }
                                    // La ejecución vuelve al hilo principal después de withContext.
                                    nuevoNombre = ""
                                    error = null
                                } catch (_: SQLException) {
                                    error = "No se pudo guardar el usuario. Intenta de nuevo."
                                } finally {
                                    guardando = false
                                }
                            }
                        }
                    },
                    enabled = nuevoNombre.isNotBlank() && !guardando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Usuario")
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Lista de Usuarios Registrados:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(usuarios, key = { it.id }) { usuario ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "ID: ${usuario.id} | Nombre: ${usuario.nombre}",
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            try {
                                                withContext(Dispatchers.IO) {
                                                    dao.eliminar(usuario)
                                                }
                                                error = null
                                            } catch (_: SQLException) {
                                                error = "No se pudo eliminar el usuario. Intenta de nuevo."
                                            }
                                        }
                                    },
                                    modifier = Modifier.semantics {
                                        contentDescription = "Eliminar usuario ${usuario.id}"
                                    }
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
