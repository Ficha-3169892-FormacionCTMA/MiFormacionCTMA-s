package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormularioActividad(
    actividadInicial: ActividadFormativa? = null, // Nueva opción
    onBack: () -> Unit,
    onGuardar: (ActividadFormativa) -> Unit
) {
    // 1. Estados locales inicializados con la actividad si existe
    var titulo by remember { mutableStateOf(actividadInicial?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(actividadInicial?.descripcion ?: "") }
    var prioridad by remember { mutableStateOf(actividadInicial?.prioridad ?: Prioridad.MEDIA) }

    // Validación simple
    val esValido = titulo.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (actividadInicial == null) "Nueva Actividad" else "Editar Actividad") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ... campos de texto (se mantienen igual porque ya usan los estados) ...
            Text(
                text = "Información de la actividad",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la actividad *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = titulo.isEmpty() // Marca en rojo si está vacío
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text(
                text = "Prioridad",
                style = MaterialTheme.typography.labelLarge
            )

            // Selector de Prioridad usando Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Prioridad.entries.forEach { opcion ->
                    FilterChip(
                        selected = prioridad == opcion,
                        onClick = { prioridad = opcion },
                        label = { Text(opcion.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Button(
                onClick = {
                    if (esValido) {
                        val actividadFinal = ActividadFormativa(
                            id = actividadInicial?.id ?: System.currentTimeMillis(), // Mantener ID si existe
                            titulo = titulo.trim(),
                            descripcion = descripcion.trim().ifBlank { null },
                            progreso = actividadInicial?.progreso ?: 0, // Mantener progreso
                            diasRestantes = actividadInicial?.diasRestantes ?: 7,
                            prioridad = prioridad
                        )
                        onGuardar(actividadFinal)
                    }
                },
                enabled = esValido,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (actividadInicial == null) "Guardar Actividad" else "Actualizar Actividad")
            }
        }
    }
}
