package com.example.practica_14

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MarcadorDao {
    @Insert
    suspend fun insertar(marcador: MarcadorEntity): Long

    @Query("SELECT * FROM marcadores ORDER BY id")
    fun observarMarcadores(): Flow<List<MarcadorEntity>>
}
