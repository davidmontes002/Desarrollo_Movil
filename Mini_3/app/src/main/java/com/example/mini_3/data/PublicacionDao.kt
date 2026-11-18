package com.example.mini_3.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PublicacionDao {
    @Insert
    suspend fun insertar(publicacion: Publicacion)

    @Query("SELECT * FROM publicaciones ORDER BY fecha DESC, id DESC")
    fun observarTodas(): Flow<List<Publicacion>>
}
