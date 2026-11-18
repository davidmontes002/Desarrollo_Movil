package com.example.mini_3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Publicacion::class], version = 1, exportSchema = true)
abstract class PublicacionDatabase : RoomDatabase() {
    abstract fun publicacionDao(): PublicacionDao

    companion object {
        @Volatile private var instancia: PublicacionDatabase? = null

        fun obtener(context: Context): PublicacionDatabase = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(
                context.applicationContext,
                PublicacionDatabase::class.java,
                "publicaciones.db"
            ).build().also { instancia = it }
        }
    }
}
