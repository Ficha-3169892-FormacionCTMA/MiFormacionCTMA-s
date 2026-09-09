package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.components.ResumenActividades
import com.example.miformacionctma.ui.components.TarjetaActividad
import com.example.miformacionctma.ui.viewmodels.ListadoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividades(
    uiState: ListadoUiState, // Ahora usamos el estado sellado
    busqueda: String,
    prioridadSeleccionada: Prioridad?,
    onBusquedaChange: (String) -> Unit,
    onPrioridadChange: (Prioridad?) -> Unit,
    onActividadClick: (ActividadFormativa) -> Unit,
    onAgregarClick: () -> Unit,
    onCompletarActividad: (ActividadFormativa) -> Unit,
    onBorrarActividad: (ActividadFormativa) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Formación CTMA") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregarClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            
            // Los filtros siempre están visibles si no hay un error crítico
            if (uiState !is ListadoUiState.Error) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    SeccionFiltros(
                        busqueda = busqueda,
                        prioridadSeleccionada = prioridadSeleccionada,
                        onBusquedaChange = onBusquedaChange,
                        onPrioridadChange = onPrioridadChange
                    )
                }
            }

            // Manejo de estados según la Semana 7
            when (uiState) {
                is ListadoUiState.Cargando -> EstadoCargando()
                is ListadoUiState.Error -> EstadoError(uiState.mensaje)
                is ListadoUiState.Vacio -> EstadoVacio(onAgregarClick)
                is ListadoUiState.Contenido -> ListaContenido(
                    actividades = uiState.actividades,
                    onActividadClick = onActividadClick,
                    onCompletarActividad = onCompletarActividad,
                    onBorrarActividad = onBorrarActividad
                )
            }
        }
    }
}

@Composable
private fun ListaContenido(
    actividades: List<ActividadFormativa>,
    onActividadClick: (ActividadFormativa) -> Unit,
    onCompletarActividad: (ActividadFormativa) -> Unit,
    onBorrarActividad: (ActividadFormativa) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth < 600.dp) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { ResumenActividades(actividades = actividades) }
                items(actividades, key = { it.id }) { actividad ->
                    TarjetaActividad(
                        actividad = actividad,
                        onClick = { onActividadClick(actividad) },
                        onCompletar = { onCompletarActividad(actividad) },
                        onBorrar = { onBorrarActividad(actividad) }
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    ResumenActividades(actividades = actividades)
                }
                items(actividades, key = { it.id }) { actividad ->
                    TarjetaActividad(
                        actividad = actividad,
                        onClick = { onActividadClick(actividad) },
                        onCompletar = { onCompletarActividad(actividad) },
                        onBorrar = { onBorrarActividad(actividad) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EstadoCargando() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EstadoError(mensaje: String) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Error, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.size(16.dp))
            Text(mensaje, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun SeccionFiltros(
    busqueda: String,
    prioridadSeleccionada: Prioridad?,
    onBusquedaChange: (String) -> Unit,
    onPrioridadChange: (Prioridad?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar actividad...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Prioridad.entries.forEach { prioridad ->
                FilterChip(
                    selected = prioridadSeleccionada == prioridad,
                    onClick = { onPrioridadChange(if (prioridadSeleccionada == prioridad) null else prioridad) },
                    label = { Text(prioridad.name) }
                )
            }
        }
    }
}

@Composable
private fun EstadoVacio(onAgregarClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Text("No hay actividades", style = MaterialTheme.typography.headlineSmall)
            Text("Empieza agregando una nueva actividad formativa.", textAlign = TextAlign.Center)
            Button(onClick = onAgregarClick, modifier = Modifier.padding(top = 16.dp)) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Agregar actividad")
            }
        }
    }
}
