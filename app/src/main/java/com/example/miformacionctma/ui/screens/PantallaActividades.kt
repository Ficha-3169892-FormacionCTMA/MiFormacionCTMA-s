package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
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
    val esInstructor = usuario.role == Role.INSTRUCTOR

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
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
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
            // Banner de sesión de usuario
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (esInstructor) Icons.Default.School else Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hola, ${usuario.username}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    SuggestionChip(
                        onClick = { },
                        label = {
                            Text(
                                text = if (esInstructor) "Instructor" else "Aprendiz",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }

            if (searchBarVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Filtrar por título...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
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
                            Text(uiState.mensaje, color = Color.Red)
                        }
                    }
                    is ListadoUiState.Contenido -> {
                        ContenidoActividades(
                            actividades = uiState.actividades,
                            esInstructor = esInstructor,
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
    esInstructor: Boolean,
    onActividadClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val esPantallaAncha = maxWidth >= 600.dp

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
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.size(120.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "No hay actividades registradas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = if (esInstructor) "Empieza a registrar las tareas para tus estudiantes."
            else "Aún no tienes tareas asignadas por tu instructor.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (esInstructor) {
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
}