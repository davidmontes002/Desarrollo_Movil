package com.example.mini_3.data

class PublicacionRepository(private val dao: PublicacionDao) {
    val publicaciones = dao.observarTodas()
    suspend fun insertar(publicacion: Publicacion) = dao.insertar(publicacion)
}
