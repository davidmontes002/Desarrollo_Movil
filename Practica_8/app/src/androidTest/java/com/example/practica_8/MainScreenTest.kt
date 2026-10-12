package com.example.practica_8

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.example.practica_8.domain.AppDatabase
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test

class MainScreenTest {
    @get:Rule
    val compose: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity> =
        createAndroidComposeRule<MainActivity>()
    private val nombre = "Prueba ${UUID.randomUUID()}"
    private val dao get() = AppDatabase.getDatabase(
        InstrumentationRegistry.getInstrumentation().targetContext
    ).usuarioDao()

    @After
    fun limpiarSoloElRegistroDePrueba() = runBlocking {
        dao.obtenerTodos().first().filter { it.nombre == nombre }.forEach { dao.eliminar(it) }
    }

    @Test
    fun validaGuardaRestauraYEliminaSinRecargar() {
        val campo = compose.onNode(hasSetTextAction())
        val guardar = compose.onNodeWithText("Guardar Usuario")
        guardar.assertIsNotEnabled()
        campo.performTextInput("   ")
        guardar.assertIsNotEnabled()
        campo.performTextClearance()
        campo.performTextInput(nombre)
        guardar.performClick()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("| Nombre: $nombre", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        campo.assertTextEquals("Nombre del usuario", "")
        val usuario = runBlocking { dao.obtenerTodos().first().single { it.nombre == nombre } }
        val texto = "ID: ${usuario.id} | Nombre: $nombre"
        compose.onNodeWithText(texto).performScrollTo().assertIsDisplayed()
        compose.activityRule.scenario.recreate()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription("Eliminar usuario ${usuario.id}")
            .performScrollTo().performClick()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText(texto).fetchSemanticsNodes().isEmpty()
        }
    }
}
