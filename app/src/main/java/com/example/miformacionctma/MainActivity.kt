package com.example.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.miformacionctma.domain.model.Role
import com.example.miformacionctma.domain.model.User // <-- Importación que soluciona el error
import com.example.miformacionctma.ui.screens.LoginScreen
import com.example.miformacionctma.ui.screens.PantallaActividades
import com.example.miformacionctma.ui.screens.RegisterScreen
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme
import com.example.miformacionctma.ui.viewmodel.ActividadViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        // 1. Pantalla de Iniciar Sesión
        composable("login") {
            LoginScreen(
                onLogin = { email, password, role ->
                    // Aquí procesas la autenticación y navegas
                    navController.navigate("actividades") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        // 2. Pantalla de Registro
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("actividades") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Pantalla de Actividades
        composable("actividades") {
            val actividadViewModel: ActividadViewModel = viewModel()

            // Obtenemos los estados observados desde el ViewModel
            val uiState by actividadViewModel.uiState.collectAsState()
            val searchQuery by actividadViewModel.searchQuery.collectAsState()

            // Usuario de prueba o proveniente de tu sesión actual / AuthViewModel
            val usuarioActual = User(
                username = "Santiago", // O una cadena representando un ID/correo si lo usas como String
                role = Role.INSTRUCTOR
            )

            PantallaActividades(
                usuario = usuarioActual,
                uiState = uiState,
                searchQuery = searchQuery,
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
                }
            )
        }
    }
}