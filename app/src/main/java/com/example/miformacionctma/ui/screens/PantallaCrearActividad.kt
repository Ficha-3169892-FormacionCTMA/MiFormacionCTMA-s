package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.validarActividad
import com.example.miformacionctma.ui.components.FormularioActividad
import com.example.miformacionctma.ui.state.OperacionUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearActividad(
    operacionState: OperacionUiState,
    onActividadGuardada: (ActividadFormativa) -> Unit,
    onBackClick: () -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf("") }
    var prioridad by rememberSaveable { mutableStateOf(Prioridad.MEDIA) }
    var progreso by rememberSaveable { mutableStateOf("0") }

    val uiState = remember(titulo, descripcion, fecha, prioridad, progreso, operacionState) {
        val erroresList = validarActividad(
            ActividadFormativa(
                id = 0,
                titulo = titulo,
                descripcion = descripcion,
                progreso = progreso.toIntOrNull() ?: -1,
                fecha = fecha,
                diasRestantes = 0,
                prioridad = prioridad
            )
        )

        val erroresMap = mutableMapOf<String, String>()
        erroresList.forEach { error ->
            when {
                error.contains("título", ignoreCase = true) -> erroresMap["titulo"] = error
                error.contains("descripción", ignoreCase = true) -> erroresMap["descripcion"] = error
                error.contains("fecha", ignoreCase = true) -> erroresMap["fecha"] = error
                error.contains("progreso", ignoreCase = true) -> erroresMap["progreso"] = error
            }
        }

        FormularioActividadUiState(
            titulo = titulo,
            descripcion = descripcion,
            fecha = fecha,
            prioridad = prioridad,
            progreso = progreso,
            errores = erroresMap,
            puedeGuardar = erroresMap.isEmpty() && operacionState !is OperacionUiState.EnCurso
        )
    }

    // Efecto para navegar hacia atrás si el guardado fue exitoso
    LaunchedEffect(operacionState) {
        if (operacionState is OperacionUiState.Exitosa) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nueva Actividad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            FormularioActividad(
                state = uiState,
                onTituloChange = { titulo = it },
                onDescripcionChange = { descripcion = it },
                onFechaChange = { fecha = it },
                onPrioridadChange = { prioridad = it },
                onProgresoChange = { progreso = it },
                onGuardarClick = {
                    onActividadGuardada(
                        ActividadFormativa(
                            id = System.currentTimeMillis(),
                            titulo = titulo,
                            descripcion = descripcion,
                            progreso = progreso.toIntOrNull() ?: 0,
                            fecha = fecha,
                            diasRestantes = 0,
                            prioridad = prioridad
                        )
                    )
                }
            )

            if (operacionState is OperacionUiState.EnCurso) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            if (operacionState is OperacionUiState.Fallida) {
                AlertDialog(
                    onDismissRequest = { /* No hacer nada o resetear estado */ },
                    confirmButton = {
                        TextButton(onClick = { /* Resetear estado en VM */ }) {
                            Text("Aceptar")
                        }
                    },
                    title = { Text("Error al guardar") },
                    text = { Text(operacionState.mensaje) }
                )
            }
        }
    }
}
