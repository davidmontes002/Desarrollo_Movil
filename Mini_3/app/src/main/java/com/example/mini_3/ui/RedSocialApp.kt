package com.example.mini_3.ui

import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.example.mini_3.data.Publicacion
import com.example.mini_3.viewmodel.FormularioState
import com.example.mini_3.viewmodel.ListadoState
import com.example.mini_3.viewmodel.PublicacionViewModel
import java.text.DateFormat
import java.util.Date
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedSocialApp(vm: PublicacionViewModel = viewModel()) {
    val formulario by vm.formulario.collectAsStateWithLifecycle()
    val listado by vm.listado.collectAsStateWithLifecycle()
    var vista by rememberSaveable { mutableIntStateOf(0) }
    val snackbar = remember { SnackbarHostState() }
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture(), vm::finalizarCaptura)
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia(), vm::seleccionarFoto)

    LaunchedEffect(formulario.mensaje) {
        formulario.mensaje?.let {
            snackbar.showSnackbar(it)
            vm.consumirMensaje()
        }
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Mini_3 · Red social local") }) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding()) {
            PrimaryTabRow(selectedTabIndex = vista) {
                Tab(selected = vista == 0, onClick = { vista = 0 }, text = { Text("Registrar") })
                Tab(selected = vista == 1, onClick = { vista = 1 }, text = { Text("Publicaciones") })
            }
            if (vista == 0) {
                Formulario(formulario, vm::titulo, vm::descripcion,
                    tomarFoto = { vm.prepararCaptura { camara.launch(it) } },
                    elegirFoto = {
                        try {
                            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        } catch (_: ActivityNotFoundException) {
                            vm.mensaje("No hay un selector de imágenes disponible en este dispositivo.")
                        } catch (_: SecurityException) {
                            vm.mensaje("No se pudo abrir el selector de imágenes.")
                        }
                    }, guardar = vm::guardar)
            } else {
                Listado(listado)
            }
        }
    }
}

@Composable
private fun Formulario(
    estado: FormularioState,
    titulo: (String) -> Unit,
    descripcion: (String) -> Unit,
    tomarFoto: () -> Unit,
    elegirFoto: () -> Unit,
    guardar: () -> Unit
) {
    var ahora by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) { ahora = System.currentTimeMillis(); delay(30_000) }
    }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Nueva publicación", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Guarda una fotografía y lo que quieres recordar.", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = estado.titulo, onValueChange = titulo, label = { Text("Título") },
            singleLine = true, enabled = !estado.ocupado, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
        OutlinedTextField(
            value = estado.descripcion, onValueChange = descripcion, label = { Text("Descripción") },
            minLines = 3, enabled = !estado.ocupado, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
        Text("Fecha actual: ${formatearFecha(ahora)}", style = MaterialTheme.typography.bodyMedium)
        Text("La fecha y hora se registran al guardar.", style = MaterialTheme.typography.bodySmall)
        if (estado.fotoUri != null) {
            Foto(estado.fotoUri, "Vista previa de la fotografía")
        } else {
            Surface(Modifier.fillMaxWidth().height(160.dp), shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                Box(contentAlignment = Alignment.Center) { Text("Todavía no has seleccionado una foto") }
            }
        }
        OutlinedButton(onClick = tomarFoto, enabled = !estado.ocupado, modifier = Modifier.fillMaxWidth()) {
            Text("Tomar foto")
        }
        OutlinedButton(onClick = elegirFoto, enabled = !estado.ocupado, modifier = Modifier.fillMaxWidth()) {
            Text("Seleccionar de galería")
        }
        Button(onClick = guardar, enabled = !estado.ocupado, modifier = Modifier.fillMaxWidth()) {
            Text(if (estado.ocupado) "Procesando…" else "Guardar publicación")
        }
        if (estado.ocupado) LinearProgressIndicator(Modifier.fillMaxWidth())
    }
}

@Composable
private fun Listado(estado: ListadoState) {
    when {
        estado.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        estado.error != null -> MensajeLista(estado.error)
        estado.publicaciones.isEmpty() -> MensajeLista("No hay publicaciones registradas.\nCrea la primera desde Registrar.")
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(estado.publicaciones, key = { it.id }) { PublicacionCard(it) }
        }
    }
}

@Composable
private fun MensajeLista(texto: String) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { Text(texto) }
}

@Composable
private fun PublicacionCard(publicacion: Publicacion) {
    Card(Modifier.fillMaxWidth()) {
        Foto(publicacion.fotoUri, "Fotografía de ${publicacion.titulo}")
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(publicacion.titulo, style = MaterialTheme.typography.titleLarge)
            Text(publicacion.descripcion, style = MaterialTheme.typography.bodyLarge)
            Text(formatearFecha(publicacion.fecha), style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Foto(uri: String, descripcion: String) {
    SubcomposeAsyncImage(
        model = uri, contentDescription = descripcion,
        modifier = Modifier.fillMaxWidth().height(230.dp), contentScale = ContentScale.Crop,
        loading = { Box(contentAlignment = Alignment.Center) { CircularProgressIndicator() } },
        error = { Box(contentAlignment = Alignment.Center) { Text("No se pudo mostrar la fotografía") } }
    )
}

internal fun formatearFecha(fecha: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(fecha))
