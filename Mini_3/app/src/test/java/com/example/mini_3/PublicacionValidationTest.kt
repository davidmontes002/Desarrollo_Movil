package com.example.mini_3

import com.example.mini_3.viewmodel.validarPublicacion
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PublicacionValidationTest {
    @Test fun rechazaTituloEnBlanco() {
        assertNotNull(validarPublicacion(" \n ", "Descripción", "file:///foto.jpg"))
    }
    @Test fun rechazaDescripcionEnBlanco() {
        assertNotNull(validarPublicacion("Título", " \t", "file:///foto.jpg"))
    }
    @Test fun requiereFotografia() {
        assertNotNull(validarPublicacion("Título", "Descripción", null))
        assertNotNull(validarPublicacion("Título", "Descripción", ""))
    }
    @Test fun aceptaFormularioCompleto() {
        assertNull(validarPublicacion("Título", "Descripción", "file:///foto.jpg"))
    }
}
