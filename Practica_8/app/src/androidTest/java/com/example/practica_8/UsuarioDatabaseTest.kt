package com.example.practica_8

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.practica_8.data.Usuario
import com.example.practica_8.domain.AppDatabase
import java.util.UUID
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsuarioDatabaseTest {
    @Test
    fun flowEmiteInsercionesYEliminacionesPorId() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val dao = db.usuarioDao()
        val cambios = Channel<List<Usuario>>(Channel.UNLIMITED)
        val observer = launch(start = CoroutineStart.UNDISPATCHED) {
            dao.obtenerTodos().collect { cambios.send(it) }
        }
        suspend fun siguiente() = withTimeout(5_000) { cambios.receive() }
        try {
            assertTrue(siguiente().isEmpty())
            dao.insertar(Usuario(nombre = "María"))
            val primero = siguiente().single()
            assertTrue(primero.id > 0)
            assertEquals("María", primero.nombre)
            dao.insertar(Usuario(nombre = "María"))
            val dos = siguiente()
            assertEquals(2, dos.size)
            assertNotEquals(dos[0].id, dos[1].id)
            dao.eliminar(primero)
            val restante = siguiente().single()
            assertNotEquals(primero.id, restante.id)
            dao.eliminar(restante)
            assertTrue(siguiente().isEmpty())
        } finally {
            observer.cancel()
            db.close()
        }
    }

    @Test
    fun persisteAlCerrarYReabrirLaBase() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val nombre = "prueba-room-${UUID.randomUUID()}"
        var db = Room.databaseBuilder(context, AppDatabase::class.java, nombre).build()
        try {
            db.usuarioDao().insertar(Usuario(nombre = "José persistente"))
            val guardado = db.usuarioDao().obtenerTodos().first().single()
            db.close()
            db = Room.databaseBuilder(context, AppDatabase::class.java, nombre).build()
            assertEquals(guardado, db.usuarioDao().obtenerTodos().first().single())
        } finally {
            db.close()
            context.deleteDatabase(nombre)
        }
    }
}
