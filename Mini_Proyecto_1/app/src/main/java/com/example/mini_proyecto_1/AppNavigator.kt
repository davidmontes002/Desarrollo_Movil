package com.example.mini_proyecto_1


import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mini_proyecto_1.ui.DetailScreen
import com.example.mini_proyecto_1.ui.RegistrationScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
    startDestination = "registration_screen"
    ) {
        // Pantalla de Registro
        composable("registration_screen") {
            RegistrationScreen(
                onNavigateToDetail = { mat, nom, car, tur, est ->
                    navController.navigate("detail_screen/$mat/$nom/$car/$tur/$est")
                }
            )
        }

        // Pantalla de Detalle recibiendo argumentos
        composable(
            route = "detail_screen/{mat}/{nom}/{car}/{tur}/{est}",
            arguments = listOf(
                navArgument("mat") { type = NavType.StringType },
        navArgument("nom") { type = NavType.StringType },
        navArgument("car") { type = NavType.StringType },
        navArgument("tur") { type = NavType.StringType },
        navArgument("est") { type = NavType.StringType }
        )
        ) { backStackEntry ->
        // Recuperamos los valores pasados
        val mat = backStackEntry.arguments?.getString("mat") ?: ""
        val nom = backStackEntry.arguments?.getString("nom") ?: ""
        val car = backStackEntry.arguments?.getString("car") ?: ""
        val tur = backStackEntry.arguments?.getString("tur") ?: ""
        val est = backStackEntry.arguments?.getString("est") ?: ""

            DetailScreen(
                matricula = mat,
                nombre = nom,
                carrera = car,
                turno = tur,
                estatus = est,
                onBackClick = { navController.popBackStack() }
            )
    }
    }
}