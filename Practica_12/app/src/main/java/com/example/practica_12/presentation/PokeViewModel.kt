package com.example.practica_12.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica_12.data.NamedResource
import com.example.practica_12.data.PokemonDetailResponse
import com.example.practica_12.data.PokeRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PokeViewModel : ViewModel() {
    private val repository = PokeRepository()
    private var listaJob: Job? = null
    private var detalleJob: Job? = null

    val tipos = mutableStateOf<List<NamedResource>>(emptyList())
    val pokemonsPorTipo = mutableStateOf<List<NamedResource>>(emptyList())
    val pokemonDetalle = mutableStateOf<PokemonDetailResponse?>(null)

    init {
        viewModelScope.launch {
            try {
                tipos.value = repository.getTypes()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("PokeViewModel", "No se pudieron cargar los tipos", e)
            }
        }
    }

    fun cargarPokemonsPorTipo(id: String) {
        listaJob?.cancel()
        pokemonsPorTipo.value = emptyList()
        listaJob = viewModelScope.launch {
            try {
                pokemonsPorTipo.value = repository.getPokemonsByType(id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("PokeViewModel", "No se pudieron cargar los Pokémon", e)
            }
        }
    }

    fun cargarDetallePokemon(id: String) {
        detalleJob?.cancel()
        pokemonDetalle.value = null
        detalleJob = viewModelScope.launch {
            try {
                pokemonDetalle.value = repository.getPokemonDetail(id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("PokeViewModel", "No se pudo cargar el detalle", e)
            }
        }
    }
}
