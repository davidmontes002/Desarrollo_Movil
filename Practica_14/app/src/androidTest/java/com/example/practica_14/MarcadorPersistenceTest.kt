package com.example.practica_14

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MarcadorPersistenceTest {
    @Test
    fun insertarEmiteFlowYConservaCoordenadasAlReabrir() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val nombre = "prueba-persistencia.db"
        context.deleteDatabase(nombre)
        var db = Room.databaseBuilder(context, AppDatabase::class.java, nombre).build()
        try {
            val punto = MarcadorEntity(
                latitud = 25.7905, longitud = -108.9875, titulo = "Punto de prueba"
            )
            val id = db.marcadorDao().insertar(punto)
            assertTrue(id > 0)
            val emitidos = withTimeout(5000) {
                db.marcadorDao().observarMarcadores().first { it.isNotEmpty() }
            }
            assertEquals(punto.copy(id = id), emitidos.single())
            db.close()
            db = Room.databaseBuilder(context, AppDatabase::class.java, nombre).build()
            val recuperados = withTimeout(5000) { db.marcadorDao().observarMarcadores().first() }
            assertEquals(emitidos, recuperados)
        } finally {
            db.close()
            context.deleteDatabase(nombre)
        }
    }
}
