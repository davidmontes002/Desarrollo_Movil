package com.example.mini2

import com.example.mini2.data.Tarea
import com.example.mini2.presentation.FiltroTareas
import com.example.mini2.presentation.OrdenTareas
import com.example.mini2.presentation.filtrarYOrdenar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FiltrosTareasTest {
    private val tareas = listOf(
        Tarea(id = 1, titulo = "Antigua", fechaCreacion = 100),
        Tarea(id = 2, titulo = "Terminada", completada = true, fechaCreacion = 300),
        Tarea(id = 3, titulo = "Reciente", fechaCreacion = 200),
        Tarea(id = 4, titulo = "Empate", fechaCreacion = 200)
    )

    @Test fun fechaDescendenteConDesempateEstable() {
        assertEquals(listOf(2, 4, 3, 1), filtrarYOrdenar(tareas, FiltroTareas.TODAS, OrdenTareas.FECHA).map { it.id })
    }

    @Test fun estadoAgrupaPendientesAntesDeCompletadas() {
        assertEquals(listOf(4, 3, 1, 2), filtrarYOrdenar(tareas, FiltroTareas.TODAS, OrdenTareas.ESTADO).map { it.id })
    }

    @Test fun cadaFiltroFuncionaConAmbosOrdenes() {
        OrdenTareas.entries.forEach { orden ->
            assertEquals(setOf(1, 3, 4), filtrarYOrdenar(tareas, FiltroTareas.PENDIENTES, orden).map { it.id }.toSet())
            assertEquals(listOf(2), filtrarYOrdenar(tareas, FiltroTareas.COMPLETADAS, orden).map { it.id })
        }
        assertEquals(listOf(1, 2, 3, 4), tareas.map { it.id })
    }

    @Test fun listaVaciaAdmiteTodosLosFiltrosYOrdenes() {
        FiltroTareas.entries.forEach { filtro ->
            OrdenTareas.entries.forEach { orden ->
                assertTrue(filtrarYOrdenar(emptyList(), filtro, orden).isEmpty())
            }
        }
    }
}
