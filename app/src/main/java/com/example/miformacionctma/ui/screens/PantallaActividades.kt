package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.miformacionctma.ui.state.OperacionUiState

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
    operacionState: OperacionUiState = OperacionUiState.Inactiva
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(operacionState) {
        when (operacionState) {
            is OperacionUiState.Fallida -> {
                snackbarHostState.showSnackbar(operacionState.mensaje)
            }
            is OperacionUiState.Exitosa -> {
                snackbarHostState.showSnackbar("Operación realizada con éxito")
            }
            else -> {}
        }
    }

    var searchBarVisible by remember { mutableStateOf(false) }
    val esInstructor = usuario.role == Role.INSTRUCTOR

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = if (esInstructor) "Gestión de Actividades" else "Mis Actividades",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onReportesClick) {
                        Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = "Reportes")
                    }
                    IconButton(onClick = { searchBarVisible = !searchBarVisible }) {
                        Icon(
                            imageVector = if (searchBarVisible) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            )
        },
        floatingActionButton = {
            if (esInstructor) {
                ExtendedFloatingActionButton(
                    onClick = onAddClick,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Nueva Actividad", fontWeight = FontWeight.Bold) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Card Banner de perfil de usuario
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (esInstructor) Icons.Default.School else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Hola, ${usuario.username}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ficha Formativa CTMA",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = if (esInstructor) "Instructor" else "Aprendiz",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = null
                    )
                }
            }

            if (searchBarVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Filtrar por título...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
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
                            Text(
                                text = uiState.mensaje,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
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
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
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
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            shape = CircleShape,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No hay actividades registradas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (esInstructor) "Empieza a registrar las tareas para tus estudiantes."
            else "Aún no tienes tareas asignadas por tu instructor.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )

        if (esInstructor) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar Primera Actividad", fontWeight = FontWeight.Bold)
            }
        }
    }
}