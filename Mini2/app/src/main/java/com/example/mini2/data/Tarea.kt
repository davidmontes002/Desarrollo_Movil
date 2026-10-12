package com.example.mini2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Cada instancia representa una fila de la tabla tareas.
@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String = "",
    val completada: Boolean = false,
    val fechaCreacion: Long = System.currentTimeMillis()
)
