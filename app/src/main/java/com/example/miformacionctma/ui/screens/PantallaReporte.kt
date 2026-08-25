package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CrearReporteRoute(
    viewModel: CrearReporteViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    CrearReporteContent(
        state = uiState,
        onTituloChange = viewModel::actualizarTitulo,
        onGuardar = viewModel::guardar
    )
}

@Composable
fun CrearReporteContent(
    state: CrearUiState,
    onTituloChange: (String) -> Unit,
    onGuardar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Crear Nuevo Reporte",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = state.titulo,
            onValueChange = onTituloChange,
            label = { Text("Título descriptivo") },
            isError = state.errorTitulo != null,
            supportingText = { state.errorTitulo?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            enabled = !state.guardando
        )
        
        Button(
            onClick = onGuardar,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = !state.guardando
        ) {
            if (state.guardando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Publicar Reporte", fontWeight = FontWeight.Bold)
            }
        }
        
        state.guardadoId?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "¡Éxito! Reporte ID: $it", 
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
