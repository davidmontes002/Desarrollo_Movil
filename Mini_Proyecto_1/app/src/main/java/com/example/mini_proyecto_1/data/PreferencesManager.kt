package com.example.mini_proyecto_1.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    // Archivo interno donde Android guarda las preferencias
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_MATRICULA = "key_matricula"
    }

    // Guardado (Escritura)
    fun saveMatricula(matricula: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_MATRICULA, matricula)
        editor.apply() // Guarda de forma asíncrona[cite: 5]
    }

    // Consulta (Lectura)
    fun getMatricula(): String {
        return sharedPreferences.getString(KEY_MATRICULA, "") ?: ""
    }
}