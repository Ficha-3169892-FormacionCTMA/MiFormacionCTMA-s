package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.data.SortOrder
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.ui.components.ResumenActividades
import com.example.miformacionctma.ui.components.TarjetaActividad
import com.example.miformacionctma.ui.state.ListadoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividades(
    uiState: ListadoUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    currentSortOrder: SortOrder,
    onSortOrderChange: (SortOrder) -> Unit,
    onActividadClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    var searchBarVisible by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mis Actividades",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    IconButton(onClick = { searchBarVisible = !searchBarVisible }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                    Box {
                        IconButton(onClick = { sortMenuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Ordenar")
                        }
                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false }
                        ) {
                            SortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    text = { Text("Ordenar por ${order.name.lowercase().capitalize()}") },
                                    onClick = {
                                        onSortOrderChange(order)
                                        sortMenuExpanded = false
                                    },
                                    leadingIcon = {
                                        if (currentSortOrder == order) Icon(Icons.Default.Check, null)
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(
                    Icons.Default.Add, 
                    contentDescription = "Agregar actividad",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (searchBarVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    placeholder = { Text("Filtrar por título...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (uiState) {
                    is ListadoUiState.Cargando -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is ListadoUiState.Vacio -> {
                        EstadoVacio(onAddClick = onAddClick)
                    }
                    is ListadoUiState.Error -> {
                        EstadoError(mensaje = uiState.mensaje)
                    }
                    is ListadoUiState.Contenido -> {
                        ContenidoActividades(
                            actividades = uiState.actividades,
                            onActividadClick = onActividadClick,
                            onDeleteClick = onDeleteClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContenidoActividades(
    actividades: List<ActividadFormativa>,
    onActividadClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth < 600.dp) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { ResumenActividades(actividades = actividades) }
                items(
                    items = actividades,
                    key = { actividad -> actividad.id }
                ) { actividad ->
                    TarjetaActividad(
                        actividad = actividad,
                        onClick = { onActividadClick(actividad.id) },
                        onDeleteClick = { onDeleteClick(actividad.id) }
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    ResumenActividades(actividades = actividades)
                }
                items(
                    items = actividades,
                    key = { actividad -> actividad.id }
                ) { actividad ->
                    TarjetaActividad(
                        actividad = actividad,
                        onClick = { onActividadClick(actividad.id) },
                        onDeleteClick = { onDeleteClick(actividad.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EstadoVacio(
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.size(120.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.padding(24.dp).fillMaxSize(),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = "No hay actividades aún",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Empieza a organizar tus tareas formativas ahora mismo.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Button(
            onClick = onAddClick,
            modifier = Modifier.height(56.dp).padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Registrar Primera Actividad", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EstadoError(mensaje: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ocurrió un error",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
