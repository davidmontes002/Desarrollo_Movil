package com.example.mini2

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.mini2.data.Tarea
import com.example.mini2.domain.AppDatabase
import java.util.UUID
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TareaDatabaseTest {
    @Test fun flowNotificaCadaOperacionSinRecargar() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val dao = db.tareaDao()
        val cambios = Channel<List<Tarea>>(Channel.UNLIMITED)
        val observador = launch(start = CoroutineStart.UNDISPATCHED) {
            dao.obtenerTodas().collect { cambios.send(it) }
        }
        suspend fun siguiente() = withTimeout(5_000) { cambios.receive() }
        try {
            assertTrue(siguiente().isEmpty())
            dao.insertar(Tarea(titulo = "Mismo título", fechaCreacion = 100))
            val original = siguiente().single()
            assertTrue(original.id > 0)
            dao.actualizar(original.copy(titulo = "Editada", descripcion = "Detalles"))
            val editada = siguiente().single()
            assertEquals(original.id, editada.id)
            assertEquals(100L, editada.fechaCreacion)
            assertEquals("Detalles", editada.descripcion)
            dao.actualizar(editada.copy(completada = true))
            val completada = siguiente().single()
            assertTrue(completada.completada)
            dao.actualizar(completada.copy(completada = false))
            val pendiente = siguiente().single()
            assertFalse(pendiente.completada)
            dao.insertar(Tarea(titulo = "Editada", completada = true))
            val ambas = siguiente()
            assertEquals(2, ambas.size)
            assertNotEquals(ambas[0].id, ambas[1].id)
            dao.eliminarCompletadas()
            assertEquals(listOf(pendiente), siguiente())
            dao.eliminar(pendiente)
            assertTrue(siguiente().isEmpty())
        } finally {
            observador.cancelAndJoin()
            db.close()
        }
    }

    @Test fun cadaCambioPermaneceAlCerrarYReabrirSQLite() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val nombre = "mini2-test-${UUID.randomUUID()}"
        var db = Room.databaseBuilder(context, AppDatabase::class.java, nombre).build()
        suspend fun reabrir(): List<Tarea> {
            db.close()
            db = Room.databaseBuilder(context, AppDatabase::class.java, nombre).build()
            return withTimeout(5_000) { db.tareaDao().obtenerTodas().first() }
        }
        try {
            db.tareaDao().insertar(Tarea(titulo = "Primera", fechaCreacion = 100))
            db.tareaDao().insertar(Tarea(titulo = "Segunda", fechaCreacion = 200))
            val iniciales = reabrir()
            assertEquals(2, iniciales.size)
            val original = iniciales.single { it.titulo == "Primera" }
            val editada = original.copy(titulo = "Editada", descripcion = "Descripción persistente")
            db.tareaDao().actualizar(editada)
            assertEquals(editada, reabrir().single { it.id == original.id })
            val completada = editada.copy(completada = true)
            db.tareaDao().actualizar(completada)
            assertEquals(completada, reabrir().single { it.id == original.id })
            db.tareaDao().actualizar(completada.copy(completada = false))
            assertFalse(reabrir().single { it.id == original.id }.completada)
            db.tareaDao().actualizar(completada)
            db.tareaDao().eliminarCompletadas()
            val restante = reabrir().single()
            assertEquals("Segunda", restante.titulo)
            db.tareaDao().eliminar(restante)
            assertTrue(reabrir().isEmpty())
        } finally {
            db.close()
            context.deleteDatabase(nombre)
        }
    }
}
