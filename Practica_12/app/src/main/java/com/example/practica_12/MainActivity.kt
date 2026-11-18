package com.example.practica_12

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.practica_12.presentation.*

class MainActivity : ComponentActivity() {
    private val viewModel: PokeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "tipos",
                modifier = Modifier.safeDrawingPadding()) {
                composable("tipos") {
                    TiposScreen(viewModel) { id -> navController.navigate("pokemons/$id") }
                }
                composable("pokemons/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: ""
                    PokemonsByTypeScreen(viewModel, id) { pokeId ->
                        navController.navigate("detalle/$pokeId")
                    }
                }
                composable("detalle/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: ""
                    PokemonDetailScreen(viewModel, id)
                }
            }
        }
    }
}
