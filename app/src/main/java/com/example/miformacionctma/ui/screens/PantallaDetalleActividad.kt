package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.estadoActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleActividad(
    actividad: ActividadFormativa,
    onBack: () -> Unit,
    onEditar: () -> Unit // Nueva acción
) {
    val estado = estadoActividad(actividad)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Actividad") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = onEditar) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar actividad")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Cabecera con Título
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = estado,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Sección de Información
            InfoSection(label = "Descripción", content = actividad.descripcion ?: "Sin descripción adicional")
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoSection(label = "Prioridad", content = actividad.prioridad.name, modifier = Modifier.weight(1f))
                InfoSection(label = "Progreso", content = "${actividad.progreso}%", modifier = Modifier.weight(1f))
            }

            InfoSection(
                label = "Tiempo restante", 
                content = if (actividad.diasRestantes >= 0) "${actividad.diasRestantes} días" else "Actividad vencida"
            )
        }
    }
}

@Composable
private fun InfoSection(
    label: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
