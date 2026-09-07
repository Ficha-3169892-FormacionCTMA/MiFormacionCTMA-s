package com.example.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.actividadesUrgentes
import com.example.miformacionctma.domain.promedioProgreso
import com.example.miformacionctma.ui.screens.PantallaActividades
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miformacionctma.data.ActividadDataStore
import com.example.miformacionctma.ui.viewmodel.ActividadViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miformacionctma.ui.screens.PantallaCrearActividad
import com.example.miformacionctma.ui.screens.PantallaDetalleActividad

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
                
                val actividades by viewModel.actividades.collectAsStateWithLifecycle()

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
                                viewModel.eliminarActividad(id)
                            }
                        )
                    }

                    composable("crear") {
                        PantallaCrearActividad(
                            onActividadGuardada = { nuevaActividad ->
                                viewModel.agregarActividad(nuevaActividad)
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

@Composable
fun PantallaInicio(
    modifier: Modifier = Modifier
) {
    val actividades = listOf(
        ActividadFormativa(
            id = 1,
            titulo = "Aprender Kotlin",
            descripcion = "Variables y funciones",
            progreso = 80,
            fecha = "2026-08-21",
            diasRestantes = 2,
            prioridad = Prioridad.ALTA
        ),
        ActividadFormativa(
            id = 2,
            titulo = "Jetpack Compose",
            descripcion = "Crear interfaces",
            progreso = 100,
            fecha = "2026-08-17",
            diasRestantes = -2,
            prioridad = Prioridad.MEDIA
        ),
        ActividadFormativa(
            id = 3,
            titulo = "Git y GitHub",
            descripcion = "Control de versiones",
            progreso = 40,
            fecha = "2026-08-20",
            diasRestantes = 1,
            prioridad = Prioridad.ALTA
        )
    )

    val promedio = promedioProgreso(actividades)
    val urgentes = actividadesUrgentes(actividades)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Mi Formación CTMA",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Promedio de progreso: ${"%.2f".format(promedio)}%"
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Actividades urgentes: ${urgentes.size}"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Listado de actividades:"
        )

        Spacer(modifier = Modifier.height(10.dp))

        actividades.forEach { actividad ->
            Text(
                text = "${actividad.titulo} - ${actividad.progreso}%"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MiFormacionCTMATheme {
        PantallaInicio()
    }
}
