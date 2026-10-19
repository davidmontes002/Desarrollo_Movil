
package com.example.practica_9_10

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaleriaScreen() {

    val context = LocalContext.current

    // Imágenes de Internet
    val imagenes = listOf(
        "https://picsum.photos/id/1018/400/400",
        "https://picsum.photos/id/1015/400/400",
        "https://picsum.photos/id/1019/400/400",
        "https://picsum.photos/id/1020/400/400",
        "https://picsum.photos/id/1021/400/400",
        "https://picsum.photos/id/1022/400/400",
        "https://picsum.photos/id/1023/400/400",
        "https://picsum.photos/id/1024/400/400"
    )

    // Fotografías capturadas temporalmente
    val fotosCapturadas = remember {
        mutableStateListOf<Bitmap>()
    }

    // Estados para ampliar imágenes
    var imagenSeleccionada by remember {
        mutableStateOf<String?>(null)
    }

    var fotoSeleccionada by remember {
        mutableStateOf<Bitmap?>(null)
    }

    // Abre la cámara y recibe la fotografía
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->

        if (bitmap != null) {
            fotosCapturadas.add(0, bitmap)
        }
    }

    // Solicita permiso de cámara
    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->

        if (concedido) {
            camaraLauncher.launch(null)
        } else {
            Toast.makeText(
                context,
                "Se necesita permiso para usar la cámara",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Cámara y Galería")
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                    val permisoConcedido =
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                    if (permisoConcedido) {
                        camaraLauncher.launch(null)
                    } else {
                        permisoLauncher.launch(
                            Manifest.permission.CAMERA
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tomar fotografía"
                )
            }
        }

    ) { paddingValues ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Fotografías tomadas con la cámara
            items(fotosCapturadas) { bitmap ->
                ImagenCapturadaItem(
                    bitmap = bitmap,
                    onClick = {
                        fotoSeleccionada = bitmap
                        imagenSeleccionada = null
                    }
                )
            }

            // Fotografías provenientes de Internet
            items(imagenes) { url ->
                ImagenItem(
                    url = url,
                    onClick = {
                        imagenSeleccionada = url
                        fotoSeleccionada = null
                    }
                )
            }
        }
    }

    // Vista ampliada de las fotografías
    if (imagenSeleccionada != null || fotoSeleccionada != null) {

        Dialog(
            onDismissRequest = {
                imagenSeleccionada = null
                fotoSeleccionada = null
            }
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f),
                shape = RoundedCornerShape(12.dp)
            ) {

                if (fotoSeleccionada != null) {

                    Image(
                        bitmap = fotoSeleccionada!!.asImageBitmap(),
                        contentDescription = "Foto capturada ampliada",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )

                } else {

                    AsyncImage(
                        model = imagenSeleccionada,
                        contentDescription = "Imagen ampliada",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

// Imágenes descargadas desde Internet
@Composable
fun ImagenItem(
    url: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {

        AsyncImage(
            model = url,
            contentDescription = "Imagen de la galería",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Fotografías tomadas con la cámara
@Composable
fun ImagenCapturadaItem(
    bitmap: Bitmap,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {

        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Foto capturada",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
