package com.example.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.screens.PantallaActividades
import com.example.miformacionctma.ui.screens.PantallaDetalleActividad
import com.example.miformacionctma.ui.screens.PantallaFormularioActividad
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme
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
fun AppNavigation() {
    val navController = rememberNavController()
    
    // Estados temporales en memoria (No Room)
    val actividades = remember { mutableStateListOf<ActividadFormativa>() }
    var busqueda by remember { mutableStateOf("") }
    var prioridadFiltro by remember { mutableStateOf<Prioridad?>(null) }

    // Generamos el estado sellado manualmente basado en la lista en memoria
    val filtradas = actividades.filter { 
        it.titulo.contains(busqueda, ignoreCase = true) && 
        (prioridadFiltro == null || it.prioridad == prioridadFiltro)
    }
    
    val uiState = if (filtradas.isEmpty()) ListadoUiState.Vacio else ListadoUiState.Contenido(filtradas)

    NavHost(
        navController = navController,
        startDestination = "lista"
    ) {
        composable("lista") {
            PantallaActividades(
                uiState = uiState,
                busqueda = busqueda,
                prioridadSeleccionada = prioridadFiltro,
                onBusquedaChange = { busqueda = it },
                onPrioridadChange = { prioridadFiltro = it },
                onActividadClick = { actividad ->
                    navController.navigate("detalle/${actividad.id}")
                },
                onAgregarClick = {
                    navController.navigate("formulario")
                },
                onCompletarActividad = { actividad ->
                    val index = actividades.indexOfFirst { it.id == actividad.id }
                    if (index != -1) actividades[index] = actividades[index].copy(progreso = 100)
                },
                onBorrarActividad = { actividad ->
                    actividades.removeIf { it.id == actividad.id }
                }
            )
        }

        composable("formulario") {
            PantallaFormularioActividad(
                onBack = { navController.popBackStack() },
                onGuardar = { nuevaActividad ->
                    actividades.add(nuevaActividad)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "detalle/{actividadId}",
            arguments = listOf(navArgument("actividadId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("actividadId")
            val actividad = actividades.find { it.id == id }

            if (actividad != null) {
                PantallaDetalleActividad(
                    actividad = actividad,
                    onBack = { navController.popBackStack() },
                    onEditar = { navController.navigate("editar/${actividad.id}") }
                )
            }
        }
    }
}
