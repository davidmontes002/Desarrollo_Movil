package com.example.practica_14

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marcadores")
data class MarcadorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitud: Double,
    val longitud: Double,
    val titulo: String
)
