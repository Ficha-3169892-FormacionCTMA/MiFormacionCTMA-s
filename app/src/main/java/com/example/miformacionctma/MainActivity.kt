package com.example.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miformacionctma.ui.screens.PantallaActividades
import com.example.miformacionctma.ui.screens.PantallaCrearActividad
import com.example.miformacionctma.ui.screens.PantallaDetalleActividad
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MiFormacionCTMATheme {
                val navController = rememberNavController()
                val actividades = remember { mutableStateListOf(*actividadesEjemplo.toTypedArray()) }

                NavHost(navController = navController, startDestination = "lista") {
                    composable("lista") {
                        PantallaActividades(
                            actividades = actividades,
                            onActividadClick = { id ->
                                navController.navigate("detalle/$id")
                            },
                            onAddClick = {
                                navController.navigate("crear")
                            },
                            onDeleteClick = { id ->
                                actividades.removeAll { it.id == id }
                            }
                        )
                    }

                    composable("crear") {
                        PantallaCrearActividad(
                            onActividadGuardada = { nuevaActividad ->
                                actividades.add(nuevaActividad)
                                navController.popBackStack("lista", inclusive = false)
                            },
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(
                        route = "detalle/{actividadId}",
                        arguments = listOf(navArgument("actividadId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getLong("actividadId") ?: 0L
                        PantallaDetalleActividad(
                            actividadId = id,
                            actividades = actividades,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onDeleteClick = { deleteId ->
                                actividades.removeAll { it.id == deleteId }
                                navController.popBackStack("lista", inclusive = false)
                            },
                            onProgressUpdate = { updateId, newProgress ->
                                val index = actividades.indexOfFirst { it.id == updateId }
                                if (index != -1) {
                                    val current = actividades[index]
                                    actividades[index] = current.copy(progreso = newProgress)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
