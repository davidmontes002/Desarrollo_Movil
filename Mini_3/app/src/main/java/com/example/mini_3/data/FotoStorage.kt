package com.example.mini_3.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Los borradores sobreviven a la recreación; sólo las fotos guardadas pasan a fotos/. */
class FotoStorage(private val context: Context) {
    private val borradores get() = File(context.filesDir, "borradores").apply { mkdirs() }
    private val fotos get() = File(context.filesDir, "fotos").apply { mkdirs() }

    fun crearCaptura(): Uri {
        val archivo = File.createTempFile("captura_", ".jpg", borradores)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
    }

    suspend fun importar(uri: Uri): String = withContext(Dispatchers.IO) {
        val destino = File(borradores, "${UUID.randomUUID()}.img")
        try {
            context.contentResolver.openInputStream(uri).use { entrada ->
                requireNotNull(entrada) { "No se pudo abrir la fotografía" }
                destino.outputStream().use { entrada.copyTo(it) }
            }
            require(destino.length() > 0) { "La fotografía está vacía" }
            Uri.fromFile(destino).toString()
        } catch (e: Exception) {
            destino.delete()
            throw e
        }
    }

    suspend fun conservar(uri: String): String = withContext(Dispatchers.IO) {
        val destino = File(fotos, "${UUID.randomUUID()}.img")
        try {
            context.contentResolver.openInputStream(Uri.parse(uri)).use { entrada ->
                requireNotNull(entrada)
                destino.outputStream().use { entrada.copyTo(it) }
            }
            require(destino.length() > 0)
            Uri.fromFile(destino).toString()
        } catch (e: Exception) {
            destino.delete()
            throw e
        }
    }

    fun eliminar(uri: String?) {
        if (uri == null) return
        val parsed = Uri.parse(uri)
        if (parsed.scheme == "content" && parsed.authority == "${context.packageName}.fileprovider") {
            context.contentResolver.delete(parsed, null, null)
        } else if (parsed.scheme == "file") {
            val file = File(parsed.path ?: return)
            if (file.parentFile == borradores || file.parentFile == fotos) file.delete()
        }
    }

    suspend fun limpiarBorradores(conservar: Set<String>) = withContext(Dispatchers.IO) {
        val nombres = conservar.mapNotNull { Uri.parse(it).lastPathSegment }.toSet()
        borradores.listFiles()?.filter { it.name !in nombres }?.forEach { it.delete() }
    }
}
