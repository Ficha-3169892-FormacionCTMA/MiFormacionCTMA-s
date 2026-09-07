package com.example.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miformacionctma.data.ActividadDataStore
import com.example.miformacionctma.data.SortOrder
import com.example.miformacionctma.ui.screens.PantallaActividades
import com.example.miformacionctma.ui.screens.PantallaCrearActividad
import com.example.miformacionctma.ui.screens.PantallaDetalleActividad
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme
import com.example.miformacionctma.ui.viewmodel.ActividadViewModel
import com.example.miformacionctma.ui.state.ListadoUiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MiFormacionCTMATheme {
                val navController = rememberNavController()
                
                val context = LocalContext.current
                val dataStore = ActividadDataStore(context)
                val viewModel: ActividadViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ActividadViewModel(dataStore) as T
                        }
                    }
                )
                
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val operacionState by viewModel.operacionState.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val currentSortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()

                NavHost(navController = navController, startDestination = "lista") {
                    composable("lista") {
                        PantallaActividades(
                            uiState = uiState,
                            searchQuery = searchQuery,
                            onSearchQueryChange = viewModel::actualizarBusqueda,
                            currentSortOrder = currentSortOrder,
                            onSortOrderChange = viewModel::cambiarOrden,
                            onActividadClick = { id ->
                                navController.navigate("detalle/$id")
                            },
                            onAddClick = {
                                navController.navigate("crear")
                            },
                            onDeleteClick = { id ->
                                viewModel.eliminarActividad(id)
                            }
                        )
                    }

                    composable("crear") {
                        PantallaCrearActividad(
                            operacionState = operacionState,
                            onActividadGuardada = { nuevaActividad ->
                                viewModel.agregarActividad(nuevaActividad)
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
                        val actividades = (uiState as? ListadoUiState.Contenido)?.actividades ?: emptyList()
                        
                        PantallaDetalleActividad(
                            actividadId = id,
                            actividades = actividades,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onDeleteClick = { deleteId ->
                                viewModel.eliminarActividad(deleteId)
                                navController.popBackStack("lista", inclusive = false)
                            },
                            onProgressUpdate = { updateId, newProgress ->
                                viewModel.actualizarProgreso(updateId, newProgress)
                            }
                        )
                    }
                }
            }
        }
    }
}
