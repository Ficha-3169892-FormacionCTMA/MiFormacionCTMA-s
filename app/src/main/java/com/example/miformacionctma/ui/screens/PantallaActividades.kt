package com.example.miformacionctma.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.estadoActividad
import com.example.miformacionctma.domain.model.Role
import com.example.miformacionctma.domain.model.User
import com.example.miformacionctma.ui.components.ResumenActividades
import com.example.miformacionctma.ui.components.TarjetaActividad
import com.example.miformacionctma.ui.state.ListadoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividades(
    usuario: User,
    uiState: ListadoUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onActividadClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: (Long) -> Unit,
    onReportesClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    operacionState: com.example.miformacionctma.ui.state.OperacionUiState = com.example.miformacionctma.ui.state.OperacionUiState.Inactiva
) {
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(operacionState) {
        when (operacionState) {
            is com.example.miformacionctma.ui.state.OperacionUiState.Fallida -> {
                snackbarHostState.showSnackbar(operacionState.mensaje)
            }
            is com.example.miformacionctma.ui.state.OperacionUiState.Exitosa -> {
                snackbarHostState.showSnackbar("Operación realizada con éxito")
            }
            else -> {}
        }
    }

    var searchBarVisible by remember { mutableStateOf(false) }
    var filtroEstado by remember { mutableStateOf("Todas") }
    var actividadAEliminarId by remember { mutableStateOf<Long?>(null) }
    val esInstructor = usuario.role == Role.INSTRUCTOR

    // Diálogo de confirmación para eliminar actividad
    if (actividadAEliminarId != null) {
        AlertDialog(
            onDismissRequest = { actividadAEliminarId = null },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = "¿Eliminar actividad?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Esta acción eliminará la actividad y sus evidencias asociadas. No se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = actividadAEliminarId
                        actividadAEliminarId = null
                        if (id != null) {
                            onDeleteClick(id)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { actividadAEliminarId = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (esInstructor) "Gestión de Actividades" else "Mis Actividades",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    IconButton(onClick = onReportesClick) {
                        Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = "Ir a Reportes")
                    }
                    IconButton(onClick = { searchBarVisible = !searchBarVisible }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (esInstructor) {
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
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            // Banner de usuario con Avatar e identificación visual de Rol
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = usuario.username.take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = usuario.username,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = usuario.email,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = if (esInstructor) "Instructor" else "Aprendiz",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (esInstructor) Icons.Default.School else Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Barra de Búsqueda Desplegable con Animación
            AnimatedVisibility(
                visible = searchBarVisible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Filtrar por título...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }

            // Chips de Filtro Rápido por Estado
            val opcionesFiltro = listOf("Todas", "Pendiente", "En progreso", "Completada")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(opcionesFiltro) { opcion ->
                    FilterChip(
                        selected = (filtroEstado == opcion),
                        onClick = { filtroEstado = opcion },
                        label = { Text(opcion, fontWeight = FontWeight.SemiBold) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (uiState) {
                    is ListadoUiState.Cargando -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is ListadoUiState.Vacio -> {
                        EstadoVacio(
                            esInstructor = esInstructor,
                            onAddClick = onAddClick
                        )
                    }
                    is ListadoUiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(uiState.mensaje, color = MaterialTheme.colorScheme.error)
                        }
                    }
                    is ListadoUiState.Contenido -> {
                        val actividadesFiltradas = if (filtroEstado == "Todas") {
                            uiState.actividades
                        } else {
                            uiState.actividades.filter { actividad ->
                                estadoActividad(actividad).equals(filtroEstado, ignoreCase = true)
                            }
                        }

                        if (actividadesFiltradas.isEmpty()) {
                            EstadoVacioFiltro(
                                filtro = filtroEstado,
                                onLimpiarFiltro = { filtroEstado = "Todas" }
                            )
                        } else {
                            ContenidoActividades(
                                actividades = actividadesFiltradas,
                                esInstructor = esInstructor,
                                onActividadClick = onActividadClick,
                                onDeleteClick = { id -> actividadAEliminarId = id }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContenidoActividades(
    actividades: List<ActividadFormativa>,
    esInstructor: Boolean,
    onActividadClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val esPantallaAncha = this.maxWidth >= 600.dp

        if (!esPantallaAncha) {
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
                        onDeleteClick = if (esInstructor) { { onDeleteClick(actividad.id) } } else null
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
                        onDeleteClick = if (esInstructor) { { onDeleteClick(actividad.id) } } else null
                    )
                }
            }
        }
    }
}

@Composable
private fun EstadoVacio(
    esInstructor: Boolean,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            shape = CircleShape,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.TaskAlt,
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Sin actividades asignadas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (esInstructor) "Empieza creando la primera actividad para tu grupo."
            else "Aún no tienes tareas asignadas por tu instructor.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (esInstructor) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAddClick,
                modifier = Modifier.height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar Primera Actividad", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun EstadoVacioFiltro(
    filtro: String,
    onLimpiarFiltro: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.FilterListOff,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay actividades con estado \"$filtro\"",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onLimpiarFiltro) {
            Text("Mostrar todas las actividades")
        }
    }
}
