package com.example.mini_3.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.mini_3.data.FotoStorage
import com.example.mini_3.data.Publicacion
import com.example.mini_3.data.PublicacionDatabase
import com.example.mini_3.data.PublicacionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FormularioState(
    val titulo: String = "",
    val descripcion: String = "",
    val fotoUri: String? = null,
    val ocupado: Boolean = true,
    val mensaje: String? = null
)

data class ListadoState(
    val publicaciones: List<Publicacion> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null
)

class PublicacionViewModel(application: Application, private val saved: SavedStateHandle) :
    AndroidViewModel(application) {
    private val repository = PublicacionRepository(PublicacionDatabase.obtener(application).publicacionDao())
    private val fotos = FotoStorage(application)
    private val _formulario = MutableStateFlow(FormularioState(
        titulo = saved["titulo"] ?: "",
        descripcion = saved["descripcion"] ?: "",
        fotoUri = saved["fotoUri"]
    ))
    val formulario = _formulario.asStateFlow()
    private val _listado = MutableStateFlow(ListadoState())
    val listado = _listado.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                fotos.limpiarBorradores(setOfNotNull(saved["fotoUri"], saved["captura"]))
            } finally {
                _formulario.value = _formulario.value.copy(ocupado = false)
            }
        }
        viewModelScope.launch {
            repository.publicaciones.catch {
                _listado.value = ListadoState(cargando = false, error = "No se pudieron leer las publicaciones. Vuelve a abrir la aplicación.")
            }.collect { _listado.value = ListadoState(publicaciones = it, cargando = false) }
        }
    }

    fun titulo(value: String) {
        saved["titulo"] = value
        _formulario.value = _formulario.value.copy(titulo = value)
    }

    fun descripcion(value: String) {
        saved["descripcion"] = value
        _formulario.value = _formulario.value.copy(descripcion = value)
    }

    fun mensaje(value: String) { _formulario.value = _formulario.value.copy(mensaje = value) }
    fun consumirMensaje() { _formulario.value = _formulario.value.copy(mensaje = null) }

    fun prepararCaptura(abrirCamara: (Uri) -> Unit) {
        if (_formulario.value.ocupado) return
        _formulario.value = _formulario.value.copy(ocupado = true)
        viewModelScope.launch {
            try {
                val uri = withContext(Dispatchers.IO) { fotos.crearCaptura() }
                saved["captura"] = uri.toString()
                abrirCamara(uri)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                finalizarCaptura(false)
                mensaje("No se pudo abrir la cámara. Comprueba que haya una aplicación de cámara disponible.")
            }
        }
    }

    fun finalizarCaptura(correcta: Boolean) {
        val uri = saved.get<String>("captura")
        saved["captura"] = null
        viewModelScope.launch {
            try {
                if (correcta && uri != null) {
                    // Comprueba también capturas vacías devueltas por algunas cámaras.
                    val copia = fotos.importar(Uri.parse(uri))
                    cambiarFoto(copia)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                mensaje("No se pudo recuperar la fotografía. Inténtalo de nuevo.")
            } finally {
                withContext(NonCancellable + Dispatchers.IO) { fotos.eliminar(uri) }
                _formulario.value = _formulario.value.copy(ocupado = false)
            }
        }
    }

    fun seleccionarFoto(uri: Uri?) {
        if (uri == null) return
        _formulario.value = _formulario.value.copy(ocupado = true)
        viewModelScope.launch {
            try {
                cambiarFoto(fotos.importar(uri))
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                mensaje("No se pudo copiar la imagen. Selecciona otra fotografía.")
            } finally {
                _formulario.value = _formulario.value.copy(ocupado = false)
            }
        }
    }

    private suspend fun cambiarFoto(uri: String) {
        val anterior = _formulario.value.fotoUri
        saved["fotoUri"] = uri
        _formulario.value = _formulario.value.copy(fotoUri = uri)
        withContext(Dispatchers.IO) { fotos.eliminar(anterior) }
    }

    fun guardar() {
        val estado = _formulario.value
        if (estado.ocupado) return
        val error = validarPublicacion(estado.titulo, estado.descripcion, estado.fotoUri)
        if (error != null) { mensaje(error); return }
        _formulario.value = estado.copy(ocupado = true)
        viewModelScope.launch {
            // Completar la copia y la inserción aun si se cierra la pantalla durante el guardado.
            withContext(NonCancellable) {
                var permanente: String? = null
                var guardada = false
                try {
                    permanente = fotos.conservar(requireNotNull(estado.fotoUri))
                    repository.insertar(Publicacion(
                        titulo = estado.titulo.trim(), descripcion = estado.descripcion.trim(),
                        fotoUri = permanente, fecha = System.currentTimeMillis()
                    ))
                    guardada = true
                    saved["titulo"] = ""
                    saved["descripcion"] = ""
                    saved["fotoUri"] = null
                    _formulario.value = FormularioState(ocupado = false, mensaje = "Publicación guardada correctamente.")
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    _formulario.value = estado.copy(ocupado = false, mensaje = "No se pudo guardar la publicación. Inténtalo de nuevo.")
                } finally {
                    withContext(Dispatchers.IO) {
                        fotos.eliminar(if (guardada) estado.fotoUri else permanente)
                    }
                }
            }
        }
    }
}

internal fun validarPublicacion(titulo: String, descripcion: String, fotoUri: String?): String? = when {
    titulo.isBlank() -> "Escribe el título de la publicación."
    descripcion.isBlank() -> "Escribe una descripción."
    fotoUri.isNullOrBlank() -> "Toma una foto o selecciona una imagen de la galería."
    else -> null
}
