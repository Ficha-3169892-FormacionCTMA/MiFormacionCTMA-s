package com.example.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.miformacionctma.domain.model.Role
import com.example.miformacionctma.domain.model.User
import com.example.miformacionctma.ui.screens.LoginScreen
import com.example.miformacionctma.ui.screens.PantallaActividades
import com.example.miformacionctma.ui.screens.PantallaCrearActividad
import com.example.miformacionctma.ui.screens.PantallaDetalleActividad
import com.example.miformacionctma.ui.screens.PantallaReporte
import com.example.miformacionctma.ui.screens.RegisterScreen
import com.example.miformacionctma.ui.state.ListadoUiState
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme
<<<<<<< HEAD
import com.example.miformacionctma.domain.*

val actividadesEjemplo = listOf(
    ActividadFormativa(
        id = 1,
        titulo = "Kotlin básico",
        description = "Aprender fundamentos de Kotlin",
        progreso = 60,
        diasRestantes = 2,
        prioridad = Prioridad.Alta
    ),
    ActividadFormativa(
        id = 2,
        titulo = "Android Studio",
        description = "Practicar Compose",
        progreso = 100,
        diasRestantes = -1,
        prioridad = Prioridad.Media
    ),
    ActividadFormativa(
        id = 3,
        titulo = "Git y GitHub",
        description = "Practicar ramas y commits",
        progreso = 30,
        diasRestantes = 5,
        prioridad = Prioridad.Baja
    )
)

val promedio = promedioProgreso(actividadesEjemplo)
val urgentes = actividadesUrgentes(actividadesEjemplo)
=======
import com.example.miformacionctma.ui.viewmodel.ActividadViewModel
import com.example.miformacionctma.ui.viewmodel.AuthUiState
import com.example.miformacionctma.ui.viewmodel.AuthViewModel
>>>>>>> 1186d539fa38240e1aaef8eb9b67f337fe058051

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiFormacionCTMATheme {
<<<<<<< HEAD
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PantallaInicio(
                        promedio = promedio,
                        urgentes = urgentes.size,
                        modifier = Modifier.padding(innerPadding)
                    )
=======
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
>>>>>>> 1186d539fa38240e1aaef8eb9b67f337fe058051
                }
            }
        }
    }
}

@Composable
<<<<<<< HEAD
fun PantallaInicio(
    promedio: Int,
    urgentes: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Mi Formación CTMA",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Resumen de formación",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Progreso promedio: $promedio%"
        )

        Text(
            text = "Actividades urgentes: $urgentes"
        )
=======
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val actividadViewModel: ActividadViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    val authState by authViewModel.uiState.collectAsState()
    val usuarioActual = when (val state = authState) {
        is AuthUiState.Authenticated -> state.user
        else -> User(username = "Invitado", role = Role.STUDENT)
>>>>>>> 1186d539fa38240e1aaef8eb9b67f337fe058051
    }

<<<<<<< HEAD
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MiFormacionCTMATheme {
        PantallaInicio(
            promedio = 63,
            urgentes = 1
        )
=======
    LaunchedEffect(authState) {
        if (authState is AuthUiState.Authenticated) {
            navController.navigate("actividades") {
                popUpTo(0) { inclusive = true }
            }
        } else if (authState is AuthUiState.RegistrationSuccess) {
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        // 1. Pantalla de Iniciar Sesión
        composable("login") {
            val errorMsg = when (authState) {
                is AuthUiState.Error -> (authState as AuthUiState.Error).message
                is AuthUiState.RegistrationSuccess -> "Registro exitoso. Revisa tu correo para confirmar tu cuenta e iniciar sesión."
                else -> null
            }
            
            LoginScreen(
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
                onNavigateToRegister = {
                    authViewModel.resetAuthState()
                    navController.navigate("register")
                },
                externalError = errorMsg,
                isLoading = authState is AuthUiState.Loading
            )
        }

        // 2. Pantalla de Registro
        composable("register") {
            RegisterScreen(
                onRegister = { email, password, username, role ->
                    authViewModel.register(email, password, username, role)
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                externalError = (authState as? AuthUiState.Error)?.message,
                isLoading = authState is AuthUiState.Loading
            )
        }

        // 3. Pantalla de Actividades
        composable("actividades") {
            val uiState by actividadViewModel.uiState.collectAsState()
            val searchQuery by actividadViewModel.searchQuery.collectAsState()
            val operacionState by actividadViewModel.operacionState.collectAsState()

            PantallaActividades(
                usuario = usuarioActual,
                uiState = uiState,
                searchQuery = searchQuery,
                operacionState = operacionState,
                onSearchQueryChange = { query ->
                    actividadViewModel.actualizarBusqueda(query)
                },
                onActividadClick = { idActividad ->
                    navController.navigate("detalle_actividad/$idActividad")
                },
                onAddClick = {
                    navController.navigate("crear_actividad")
                },
                onDeleteClick = { idActividad ->
                    actividadViewModel.eliminarActividad(idActividad)
                },
                onReportesClick = {
                    navController.navigate("reportes")
                },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 4. Pantalla de Crear Actividad
        composable("crear_actividad") {
            PantallaCrearActividad(
                onActividadGuardada = { nuevaActividad ->
                    actividadViewModel.agregarActividad(nuevaActividad)
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // 5. Pantalla de Detalle de Actividad
        composable("detalle_actividad/{idActividad}") { backStackEntry ->
            val idActividadStr = backStackEntry.arguments?.getString("idActividad")
            val idActividad = idActividadStr?.toLongOrNull() ?: 0L

            val uiState by actividadViewModel.uiState.collectAsState()
            val actividades = when (val state = uiState) {
                is ListadoUiState.Contenido -> state.actividades
                else -> emptyList()
            }
            val context = LocalContext.current

            PantallaDetalleActividad(
                actividadId = idActividad,
                actividades = actividades,
                userRole = usuarioActual.role,
                onBackClick = {
                    navController.popBackStack()
                },
                onDeleteClick = { id ->
                    actividadViewModel.eliminarActividad(id)
                    navController.popBackStack()
                },
                onStatusUpdate = { id, nuevoEstado ->
                    actividadViewModel.actualizarEstado(id, nuevoEstado)
                },
                onEvidenciaCaptured = { id, uri, mimeType, size ->
                    actividadViewModel.subiryAdjuntarEvidencia(context, id, uri, mimeType, size)
                },
                onEvidenciaDelete = { id ->
                    actividadViewModel.eliminarEvidencia(id)
                }
            )
        }

        // 6. Pantalla de Reportes
        composable("reportes") {
            PantallaReporte(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
>>>>>>> 1186d539fa38240e1aaef8eb9b67f337fe058051
    }
}