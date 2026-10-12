package com.example.mini2.presentation

import com.example.mini2.data.Tarea

enum class FiltroTareas(val etiqueta: String) {
    TODAS("Todas"), PENDIENTES("Pendientes"), COMPLETADAS("Completadas")
}

enum class OrdenTareas(val etiqueta: String) {
    FECHA("Más recientes primero"), ESTADO("Pendientes primero")
}

// Es una vista de los datos de Room; no almacena una segunda lista independiente.
fun filtrarYOrdenar(
    tareas: List<Tarea>,
    filtro: FiltroTareas,
    orden: OrdenTareas
): List<Tarea> {
    val filtradas = tareas.filter {
        when (filtro) {
            FiltroTareas.TODAS -> true
            FiltroTareas.PENDIENTES -> !it.completada
            FiltroTareas.COMPLETADAS -> it.completada
        }
    }
    return when (orden) {
        OrdenTareas.FECHA -> filtradas.sortedWith(
            compareByDescending<Tarea> { it.fechaCreacion }.thenByDescending { it.id }
        )
        OrdenTareas.ESTADO -> filtradas.sortedWith(
            compareBy<Tarea> { it.completada }
                .thenByDescending { it.fechaCreacion }.thenByDescending { it.id }
        )
    }
}
