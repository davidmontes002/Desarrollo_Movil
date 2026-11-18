package com.example.practica_14

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale

@Composable
fun MapaOSMScreen() {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val dao = remember { AppDatabase.getInstance(context).marcadorDao() }
    val marcadores by remember { dao.observarMarcadores() }.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    fun tienePermiso() = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    ).any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

    var permiso by remember { mutableStateOf(tienePermiso()) }
    var posicion by remember { mutableStateOf<GeoPoint?>(null) }
    val solicitar = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permiso = tienePermiso() }
    val permisos = remember { arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    ) }
    val mapa = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.OpenTopo)
            setMultiTouchControls(true)
            controller.setZoom(17.0)
            controller.setCenter(GeoPoint(25.7905, -108.9875))
        }
    }
    val gps = remember(mapa) {
        val proveedor = GpsMyLocationProvider(context).apply {
            addLocationSource(LocationManager.NETWORK_PROVIDER)
        }
        object : MyLocationNewOverlay(proveedor, mapa) {
            override fun onResume() {
                if (tienePermiso()) super.onResume()
            }
        }.apply {
            // OSMdroid necesita Bitmap: convertir el Vector Asset nativo rojo.
            val icono = requireNotNull(ContextCompat.getDrawable(context, R.drawable.ic_gps_rojo))
                .toBitmap()
            setPersonIcon(icono)
            setDirectionArrow(icono, icono)
            setPersonAnchor(0.5f, 0.5f)
            enableFollowLocation()
            runOnFirstFix {
                mapa.post {
                    myLocation?.let {
                        mapa.controller.setZoom(17.0)
                        mapa.controller.setCenter(it)
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) { if (!permiso) solicitar.launch(permisos) }
    LaunchedEffect(permiso) {
        if (permiso && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            gps.enableMyLocation()
        } else gps.disableMyLocation()
        while (permiso) {
            posicion = gps.myLocation
            delay(1000)
        }
    }
    DisposableEffect(mapa, lifecycle) {
        val eventos = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?) = false
            override fun longPressHelper(p: GeoPoint?): Boolean {
                p ?: return false
                scope.launch {
                    try {
                        dao.insertar(MarcadorEntity(
                            latitud = p.latitude, longitud = p.longitude,
                            titulo = String.format(Locale.US, "Punto de interés (%.5f, %.5f)",
                                p.latitude, p.longitude)
                        ))
                        snackbar.showSnackbar("Marcador guardado en Room")
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        snackbar.showSnackbar("No se pudo guardar. Intenta de nuevo.")
                    }
                }
                return true
            }
        })
        mapa.overlays.add(eventos)
        mapa.overlays.add(gps)
        val observador = LifecycleEventObserver { _, evento ->
            when (evento) {
                Lifecycle.Event.ON_RESUME -> {
                    mapa.onResume()
                    permiso = tienePermiso()
                    if (permiso) gps.enableMyLocation()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    gps.disableMyLocation()
                    mapa.onPause()
                }
                else -> Unit
            }
        }
        lifecycle.addObserver(observador)
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) mapa.onResume()
        onDispose {
            lifecycle.removeObserver(observador)
            gps.disableMyLocation()
            mapa.onPause()
            mapa.onDetach()
        }
    }

    LaunchedEffect(marcadores, mapa) {
        // Flow actualiza exclusivamente los marcadores persistidos.
        mapa.overlays.filterIsInstance<Marker>().forEach {
            it.closeInfoWindow()
            mapa.overlays.remove(it)
        }
        marcadores.forEach { dato ->
            mapa.overlays.add(Marker(mapa).apply {
                position = GeoPoint(dato.latitud, dato.longitud)
                title = dato.titulo
                snippet = "Guardado permanentemente · #${dato.id}"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            })
        }
        mapa.invalidate()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Surface(tonalElevation = 3.dp) {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Text("Práctica 14 · GPS y mapas", style = MaterialTheme.typography.titleMedium)
                    Text("Mantén pulsada una calle para guardar un punto.",
                        style = MaterialTheme.typography.bodySmall)
                    Text("Marcadores guardados: ${marcadores.size}",
                        style = MaterialTheme.typography.bodySmall)
                    Text(
                        if (!permiso) "Sin permiso de ubicación"
                        else posicion?.let {
                            String.format(Locale.US, "GPS: %.6f, %.6f", it.latitude, it.longitude)
                        } ?: "Buscando ubicación… Activa el GPS del celular.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Box(Modifier.weight(1f).clipToBounds()) {
                AndroidView(
                    factory = { mapa },
                    modifier = Modifier.fillMaxSize().clipToBounds(),
                )
                Button(
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                    onClick = {
                        if (!permiso) solicitar.launch(permisos)
                        else {
                            gps.enableFollowLocation()
                            gps.myLocation?.let { mapa.controller.animateTo(it) }
                            if (gps.myLocation == null) {
                                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            }
                        }
                    }
                ) { Text(if (permiso) "Mi ubicación" else "Permitir GPS") }
            }
            Text("© OpenStreetMap contributors · SRTM | © OpenTopoMap (CC-BY-SA)",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall)
        }
    }
}
