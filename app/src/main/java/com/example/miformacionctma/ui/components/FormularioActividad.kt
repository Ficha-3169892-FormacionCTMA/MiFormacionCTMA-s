package com.example.miformacionctma.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.screens.FormularioActividadUiState

@Composable
fun FormularioActividad(
    state: FormularioActividadUiState,
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onFechaChange: (String) -> Unit,
    onPrioridadChange: (Prioridad) -> Unit,
    onProgresoChange: (String) -> Unit,
    onGuardarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Nueva Actividad",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Completa los campos para registrar tu avance",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        OutlinedTextField(
            value = state.titulo,
            onValueChange = onTituloChange,
            label = { Text("¿Qué vas a estudiar?") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            isError = state.errores.containsKey("titulo"),
            supportingText = {
                state.errores["titulo"]?.let { Text(it) }
            }
        )

        OutlinedTextField(
            value = state.descripcion,
            onValueChange = onDescripcionChange,
            label = { Text("Descripción (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            isError = state.errores.containsKey("descripcion"),
            supportingText = {
                state.errores["descripcion"]?.let { Text(it) }
            }
        )

        OutlinedTextField(
            value = state.fecha,
            onValueChange = onFechaChange,
            label = { Text("Fecha límite (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            placeholder = { Text("Ej: 2026-08-19") },
            isError = state.errores.containsKey("fecha"),
            supportingText = {
                state.errores["fecha"]?.let { Text(it) }
            }
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Prioridad.entries.forEach { prioridad ->
                        FilterChip(
                            selected = state.prioridad == prioridad,
                            onClick = { onPrioridadChange(prioridad) },
                            label = { Text(prioridad.name) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = state.progreso,
            onValueChange = onProgresoChange,
            label = { Text("Progreso actual (%)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            leadingIcon = { Icon(Icons.Default.List, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = state.errores.containsKey("progreso"),
            supportingText = {
                state.errores["progreso"]?.let { Text(it) }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onGuardarClick,
            enabled = state.puedeGuardar,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Guardar Actividad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}
