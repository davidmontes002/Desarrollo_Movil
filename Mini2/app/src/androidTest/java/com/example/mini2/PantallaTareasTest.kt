package com.example.mini2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.mini2.data.Tarea
import com.example.mini2.domain.AppDatabase
import com.example.mini2.presentation.PantallaTareas
import com.example.mini2.ui.theme.Mini2Theme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PantallaTareasTest {
    @get:Rule val compose: ComposeContentTestRule = createComposeRule()
    private lateinit var db: AppDatabase
    private lateinit var restauracion: StateRestorationTester
    private val dao get() = db.tareaDao()

    @Before fun preparar() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        ).build()
        restauracion = StateRestorationTester(compose)
        restauracion.setContent { Mini2Theme { PantallaTareas(dao) } }
    }

    @After fun cerrar() { db.close() }

    private fun tareas() = runBlocking { dao.obtenerTodas().first() }
    private fun esperar(condicion: () -> Boolean) { compose.waitUntil(5_000, condicion) }
    private fun visible(texto: String) = compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
    private fun menu(opcion: String) {
        compose.onNodeWithContentDescription("Más opciones").performClick()
        compose.onNodeWithText(opcion).performClick()
    }
    private fun crear(titulo: String, descripcion: String = "") {
        compose.onNodeWithContentDescription("Agregar tarea").performClick()
        compose.onNodeWithText("Título").performTextReplacement(titulo)
        compose.onNodeWithText("Descripción (opcional)").performTextReplacement(descripcion)
        compose.onNodeWithText("Guardar").performClick()
        esperar { visible(titulo) && !visible("Nueva tarea") }
    }

    @Test fun formularioValidaEditaCompletaDesmarcaYElimina() {
        compose.onNodeWithText("Administrador de Tareas").assertIsDisplayed()
        compose.onNodeWithText("No hay tareas registradas").assertIsDisplayed()
        compose.onNodeWithContentDescription("Agregar tarea").performClick()
        compose.onNodeWithText("Guardar").assertIsNotEnabled()
        compose.onNodeWithText("Título").performTextReplacement("   ")
        compose.onNodeWithText("Guardar").assertIsNotEnabled()
        compose.onNodeWithText("Cancelar").performClick()
        assertTrue(tareas().isEmpty())
        crear("Estudiar", "Repasar Room")
        val original = tareas().single()
        compose.onNodeWithContentDescription("Cambiar estado de Estudiar").performClick()
        esperar { visible("Completada") }
        compose.onNodeWithContentDescription("Editar Estudiar").performClick()
        compose.onNode(hasSetTextAction() and hasText("Repasar Room")).assertIsDisplayed()
        compose.onNodeWithText("Título").performTextReplacement("Estudiar Kotlin")
        compose.onNodeWithText("Descripción (opcional)").performTextReplacement("Listas y corrutinas")
        compose.onNodeWithText("Guardar").performClick()
        esperar { visible("Estudiar Kotlin") && !visible("Editar tarea") }
        val editada = tareas().single()
        assertEquals(original.id, editada.id)
        assertEquals(original.fechaCreacion, editada.fechaCreacion)
        assertEquals("Listas y corrutinas", editada.descripcion)
        assertTrue(editada.completada)
        compose.onNodeWithContentDescription("Cambiar estado de Estudiar Kotlin").performClick()
        esperar { visible("Pendiente") }
        assertFalse(tareas().single().completada)
        compose.onNodeWithContentDescription("Eliminar Estudiar Kotlin").performClick()
        esperar { visible("No hay tareas registradas") }
        assertTrue(tareas().isEmpty())
    }

    @Test fun menuFiltraOrdenaYBorraSoloCompletadas() {
        runBlocking {
            dao.insertar(Tarea(titulo = "Antigua", fechaCreacion = 100))
            dao.insertar(Tarea(titulo = "Terminada", completada = true, fechaCreacion = 300))
            dao.insertar(Tarea(titulo = "Reciente", fechaCreacion = 200))
        }
        esperar { visible("Antigua") && visible("Terminada") && visible("Reciente") }
        fun comprobarOrden(titulos: List<String>) {
            val posiciones = titulos.map { compose.onNodeWithText(it).fetchSemanticsNode().boundsInRoot.top }
            assertTrue(posiciones.zipWithNext().all { (a, b) -> a < b })
        }
        comprobarOrden(listOf("Terminada", "Reciente", "Antigua"))
        menu("Ordenar por estado")
        comprobarOrden(listOf("Reciente", "Antigua", "Terminada"))
        menu("Pendientes")
        compose.onNodeWithText("Terminada").assertDoesNotExist()
        compose.onNodeWithText("Reciente").assertIsDisplayed()
        menu("Completadas")
        compose.onNodeWithText("Reciente").assertDoesNotExist()
        compose.onNodeWithText("Terminada").assertIsDisplayed()
        menu("Todas")
        menu("Ordenar por fecha")
        comprobarOrden(listOf("Terminada", "Reciente", "Antigua"))
        menu("Borrar completadas")
        compose.onNodeWithText("Cancelar").performClick()
        assertEquals(3, tareas().size)
        menu("Borrar completadas")
        compose.onNodeWithText("Borrar").performClick()
        esperar { !visible("Terminada") }
        assertEquals(setOf("Antigua", "Reciente"), tareas().map { it.titulo }.toSet())
        menu("Completadas")
        compose.onNodeWithText("No hay tareas para este filtro").assertIsDisplayed()
        compose.onNodeWithContentDescription("Más opciones").performClick()
        compose.onNodeWithText("Borrar completadas").assertIsNotEnabled()
    }

    @Test fun restaurarConservaBorradorYFiltrosYListaSeDesplaza() {
        crear("Original")
        menu("Pendientes")
        menu("Ordenar por estado")
        compose.onNodeWithContentDescription("Editar Original").performClick()
        compose.onNodeWithText("Título").performTextReplacement("Borrador")
        restauracion.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("Borrador").assertIsDisplayed()
        compose.onNodeWithText("Guardar").performClick()
        esperar { visible("Borrador") && !visible("Editar tarea") }
        compose.onNodeWithText("Vista: Pendientes").assertIsDisplayed()
        compose.onNodeWithText("Orden: Pendientes primero").assertIsDisplayed()
        runBlocking {
            repeat(20) { dao.insertar(Tarea(titulo = "Tarea $it", fechaCreacion = it.toLong())) }
        }
        esperar { tareas().size == 21 }
        val ultima = tareas().single { it.titulo == "Tarea 0" }
        compose.onNodeWithTag("lista_tareas").performScrollToNode(
            androidx.compose.ui.test.hasTestTag("tarea_${ultima.id}")
        )
        compose.onNodeWithText("Tarea 0").assertIsDisplayed()
    }
}
