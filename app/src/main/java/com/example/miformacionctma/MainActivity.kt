package com.example.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miformacionctma.ui.screens.PantallaActividades
import com.example.miformacionctma.ui.screens.PantallaDetalleActividad
import com.example.miformacionctma.ui.screens.PantallaFormularioActividad
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme
import com.example.miformacionctma.ui.viewmodels.ActividadesViewModel
import com.example.miformacionctma.ui.viewmodels.ListadoUiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiFormacionCTMATheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(
    viewModel: ActividadesViewModel = viewModel()
) {
    val navController = rememberNavController()
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()
    val prioridadFiltro by viewModel.prioridadFiltro.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "lista"
    ) {
        // 1. Pantalla de Listado
        composable("lista") {
            PantallaActividades(
                uiState = uiState,
                busqueda = busqueda,
                prioridadSeleccionada = prioridadFiltro,
                onBusquedaChange = { viewModel.actualizarBusqueda(it) },
                onPrioridadChange = { viewModel.filtrarPorPrioridad(it) },
                onActividadClick = { actividad ->
                    navController.navigate("detalle/${actividad.id}")
                },
                onAgregarClick = {
                    navController.navigate("formulario")
                },
                onCompletarActividad = { actividad ->
                    viewModel.completarActividad(actividad)
                },
                onBorrarActividad = { actividad ->
                    viewModel.borrarActividad(actividad)
                }
            )
        }

        // 2. Pantalla de Creación (Formulario vacío)
        composable("formulario") {
            PantallaFormularioActividad(
                onBack = { navController.popBackStack() },
                onGuardar = { nuevaActividad ->
                    viewModel.agregarActividad(nuevaActividad)
                    navController.popBackStack()
                }
            )
        }

        // 3. Pantalla de Edición (Formulario con datos)
        composable(
            route = "editar/{actividadId}",
            arguments = listOf(navArgument("actividadId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("actividadId")
            val actividad = (uiState as? ListadoUiState.Contenido)?.actividades?.find { it.id == id }

            if (actividad != null) {
                PantallaFormularioActividad(
                    actividadInicial = actividad,
                    onBack = { navController.popBackStack() },
                    onGuardar = { actividadEditada ->
                        viewModel.agregarActividad(actividadEditada) // REPLACE en DAO se encarga del update
                        navController.popBackStack()
                    }
                )
            }
        }

        // 4. Pantalla de Detalle
        composable(
            route = "detalle/{actividadId}",
            arguments = listOf(navArgument("actividadId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("actividadId")
            val actividad = (uiState as? ListadoUiState.Contenido)?.actividades?.find { it.id == id }

            if (actividad != null) {
                PantallaDetalleActividad(
                    actividad = actividad,
                    onBack = { navController.popBackStack() },
                    onEditar = {
                        navController.navigate("editar/${actividad.id}")
                    }
                )
            }
        }
    }
}
