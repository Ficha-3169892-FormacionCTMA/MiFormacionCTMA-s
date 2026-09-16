package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miformacionctma.ui.viewmodel.CrearReporteViewModel
import com.example.miformacionctma.ui.viewmodel.CrearUiState

@Composable
fun PantallaReporte(
    onBackClick: () -> Unit,
    viewModel: CrearReporteViewModel = viewModel()
) {
    // Escuchamos el único estado expuesto por el ViewModel
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Si ya se guardó con éxito, podemos navegar hacia atrás automáticamente
    LaunchedEffect(state.guardadoId) {
        if (state.guardadoId != null) {
            onBackClick()
        }
    }

    CrearReporteContent(
        state = state,
        onBackClick = onBackClick,
        onTituloChange = { viewModel.actualizarTitulo(it) },
        onGuardar = { viewModel.guardar() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearReporteContent(
    state: CrearUiState,
    onBackClick: () -> Unit,
    onTituloChange: (String) -> Unit,
    onGuardar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Reporte") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.titulo,
                onValueChange = onTituloChange,
                label = { Text("Título del reporte") },
                isError = state.errorTitulo != null,
                supportingText = {
                    if (state.errorTitulo != null) {
                        Text(
                            text = state.errorTitulo,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.guardando
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onGuardar,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.guardando
            ) {
                if (state.guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar Reporte")
                }
            }
        }
    }
}