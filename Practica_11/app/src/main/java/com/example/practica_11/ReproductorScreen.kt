package com.example.practica_11

import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReproductorScreen() {
    val context = LocalContext.current
    var audioUri by remember { mutableStateOf<Uri?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val selectorAudio = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Liberar el reproductor anterior antes de cargar otra pista.
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
            audioUri = null

            mediaPlayer = MediaPlayer.create(context, uri)
            mediaPlayer?.let { player ->
                audioUri = uri
                player.setOnCompletionListener { isPlaying = false }
                player.start()
                isPlaying = true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mini Reproductor MP3") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (audioUri == null) "Ningún archivo seleccionado"
                else "Audio cargado correctamente",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { selectorAudio.launch("audio/*") }) {
                Text("Buscar MP3 en SD/Memoria")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(
                    onClick = {
                        if (mediaPlayer != null && !isPlaying) {
                            mediaPlayer?.start()
                            isPlaying = true
                        }
                    }
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Reproducir")
                }

                FloatingActionButton(
                    onClick = {
                        if (mediaPlayer != null && isPlaying) {
                            mediaPlayer?.pause()
                            isPlaying = false
                        }
                    },
                    modifier = Modifier.semantics { contentDescription = "Pausa" }
                ) {
                    Text(" || ", style = MaterialTheme.typography.titleLarge)
                }

                FloatingActionButton(
                    onClick = {
                        mediaPlayer?.pause()
                        isPlaying = false
                        mediaPlayer?.seekTo(0)
                    }
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "Detener")
                }
            }
        }
    }
}
