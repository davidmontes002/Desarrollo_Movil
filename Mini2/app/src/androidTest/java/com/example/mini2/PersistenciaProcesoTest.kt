package com.example.mini2

import androidx.test.platform.app.InstrumentationRegistry
import com.example.mini2.data.Tarea
import com.example.mini2.domain.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// También permite ejecutar cada fase en procesos separados usando ADB.
class PersistenciaProcesoTest {
    @Test fun cambiosSobrevivenAlReinicio() = runBlocking {
        val dao = AppDatabase.getDatabase(
            InstrumentationRegistry.getInstrumentation().targetContext
        ).tareaDao()
        val prefijo = "__mini2_prueba_proceso_20261011__"
        suspend fun propias() = dao.obtenerTodas().first().filter { it.titulo.startsWith(prefijo) }
        suspend fun ejecutar(fase: String) {
            when (fase) {
                "crear" -> {
                    propias().forEach { dao.eliminar(it) }
                    dao.insertar(Tarea(titulo = "${prefijo}A", fechaCreacion = 100))
                    dao.insertar(Tarea(titulo = "${prefijo}B", fechaCreacion = 200))
                    assertEquals(2, propias().size)
                }
                "editar" -> {
                    assertEquals(2, propias().size)
                    val tarea = propias().single { it.fechaCreacion == 100L }
                    assertEquals("${prefijo}A", tarea.titulo)
                    dao.actualizar(tarea.copy(titulo = "${prefijo}A editada", descripcion = "Persistente"))
                }
                "completar" -> {
                    val tarea = propias().single { it.fechaCreacion == 100L }
                    assertEquals("${prefijo}A editada", tarea.titulo)
                    assertEquals("Persistente", tarea.descripcion)
                    assertFalse(tarea.completada)
                    dao.actualizar(tarea.copy(completada = true))
                }
                "desmarcar" -> {
                    val tarea = propias().single { it.fechaCreacion == 100L }
                    assertTrue(tarea.completada)
                    dao.actualizar(tarea.copy(completada = false))
                }
                "eliminar" -> {
                    val tarea = propias().single { it.fechaCreacion == 100L }
                    assertFalse(tarea.completada)
                    dao.eliminar(tarea)
                }
                "limpiar" -> {
                    val restante = propias().single()
                    assertEquals("${prefijo}B", restante.titulo)
                    dao.eliminar(restante)
                    assertTrue(propias().isEmpty())
                }
                else -> error("Fase de prueba desconocida: $fase")
            }
        }
        val fase = InstrumentationRegistry.getArguments().getString("faseMini2")
        if (fase == null) {
            listOf("crear", "editar", "completar", "desmarcar", "eliminar", "limpiar").forEach { ejecutar(it) }
        } else {
            ejecutar(fase)
        }
    }
}
