package com.example.miformacionctma.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miformacionctma.data.model.EvidenciaDto
import com.example.miformacionctma.data.model.toDomain
import com.example.miformacionctma.data.model.toDto
import com.example.miformacionctma.data.repository.ActividadRepositoryImpl
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.model.ActividadEstado
import com.example.miformacionctma.domain.model.Evidencia
import com.example.miformacionctma.domain.model.EvidenciaStatus
import com.example.miformacionctma.ui.state.ListadoUiState
import com.example.miformacionctma.ui.state.OperacionUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ActividadViewModel(
    private val repository: ActividadRepositoryImpl = ActividadRepositoryImpl()
) : ViewModel() {

    private val _actividades = MutableStateFlow<List<ActividadFormativa>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _loadError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ListadoUiState> = combine(
        _actividades,
        _searchQuery,
        _loadError
    ) { lista, query, error ->
        if (error != null) return@combine ListadoUiState.Error(error)

        val filtered = if (query.isBlank()) lista
        else lista.filter { it.titulo.contains(query, ignoreCase = true) }

        if (filtered.isEmpty()) ListadoUiState.Vacio
        else ListadoUiState.Contenido(filtered)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ListadoUiState.Cargando
        )

    private val _operacionState = MutableStateFlow<OperacionUiState>(OperacionUiState.Inactiva)
    val operacionState: StateFlow<OperacionUiState> = _operacionState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        cargarActividades()
    }

    fun cargarActividades() {
        viewModelScope.launch {
            _loadError.value = null
            try {
                val dtoList = repository.getActividades()
                _actividades.value = dtoList.map { it.toDomain() }
            } catch (e: Exception) {
                _loadError.value = e.localizedMessage ?: "Error al conectar con Supabase"
                _operacionState.value = OperacionUiState.Fallida(_loadError.value!!)
            }
        }
    }

    fun actualizarBusqueda(query: String) {
        _searchQuery.value = query
    }

    fun agregarActividad(actividad: ActividadFormativa) {
        viewModelScope.launch {
            _operacionState.value = OperacionUiState.EnCurso // <-- Corregido (antes Cargando)
            try {
                val dtoGuardado = repository.insertActividad(actividad.toDto())
                _actividades.update { it + dtoGuardado.toDomain() }
                _operacionState.value = OperacionUiState.Exitosa // <-- Corregido (antes Exito)
                _eventFlow.emit("Actividad registrada en la nube")
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Fallida(e.localizedMessage ?: "Error al guardar")
            }
        }
    }

    fun actualizarEstado(id: Long, nuevoEstado: ActividadEstado) {
        viewModelScope.launch {
            val nuevoProgreso = when (nuevoEstado) {
                ActividadEstado.LISTA -> 100
                ActividadEstado.MAL -> 0
                ActividadEstado.ESPERA -> 10
                ActividadEstado.EN_CURSO -> 25
            }

            _actividades.update { lista ->
                lista.map { 
                    if (it.id == id) it.copy(estado = nuevoEstado, progreso = nuevoProgreso) 
                    else it 
                }
            }
            try {
                val actividadActual = _actividades.value.find { it.id == id }
                actividadActual?.let {
                    repository.insertActividad(it.toDto())
                }
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Fallida("Error al actualizar estado en Supabase")
            }
        }
    }

    fun actualizarProgreso(id: Long, nuevoProgreso: Int) {
        viewModelScope.launch {
            _actividades.update { lista ->
                lista.map { if (it.id == id) it.copy(progreso = nuevoProgreso) else it }
            }
            try {
                val actividadActual = _actividades.value.find { it.id == id }
                actividadActual?.let {
                    repository.insertActividad(it.toDto())
                }
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Fallida("Error al actualizar progreso en Supabase")
            }
        }
    }

    fun eliminarActividad(id: Long) {
        viewModelScope.launch {
            _operacionState.value = OperacionUiState.EnCurso // <-- Corregido (antes Cargando)
            try {
                repository.deleteActividad(id)
                _actividades.update { lista -> lista.filter { it.id != id } }
                _operacionState.value = OperacionUiState.Exitosa // <-- Corregido (antes Exito)
                _eventFlow.emit("Actividad eliminada correctamente")
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Fallida(e.localizedMessage ?: "Error al eliminar")
            }
        }
    }

    fun subiryAdjuntarEvidencia(
        context: Context,
        actividadId: Long,
        uri: Uri,
        mimeType: String,
        size: Long,
        userId: String,
        username: String
    ) {
        viewModelScope.launch {
            val nuevaEvidencia = Evidencia(
                uri = uri.toString(),
                mimeType = mimeType,
                size = size,
                status = EvidenciaStatus.SUBIENDO,
                actividadId = actividadId,
                userId = userId,
                userName = username
            )

            // Actualización inmediata para mostrar la previsualización local
            _actividades.update { lista ->
                lista.map { actividad ->
                    if (actividad.id == actividadId) {
                        actividad.copy(evidencias = actividad.evidencias + nuevaEvidencia)
                    } else actividad
                }
            }

            _operacionState.value = OperacionUiState.EnCurso
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: throw Exception("No se pudo leer el archivo de la imagen")

                // Nombre del archivo incluye el nombre de usuario
                val safeUsername = username.replace(" ", "_").lowercase()
                val fileName = "evidencia_${safeUsername}_${actividadId}_${System.currentTimeMillis()}.jpg"

                val publicUrl = repository.uploadImagenEvidencia(
                    bucketName = "evidencias",
                    fileName = fileName,
                    fileBytes = bytes
                )

                repository.insertEvidencia(
                    EvidenciaDto(
                        actividadId = actividadId,
                        userId = userId,
                        userName = username,
                        filePath = "evidencias/$fileName",
                        fileUrl = publicUrl,
                        mimeType = mimeType,
                        sizeBytes = size
                    )
                )

                _actividades.update { lista ->
                    lista.map { actividad ->
                        if (actividad.id == actividadId) {
                            actividad.copy(
                                evidencias = actividad.evidencias.map {
                                    if (it.userId == userId && it.status == EvidenciaStatus.SUBIENDO) {
                                        it.copy(
                                            status = EvidenciaStatus.SINCRONIZADA,
                                            remoteUrl = publicUrl
                                        )
                                    } else it
                                }
                            )
                        } else actividad
                    }
                }

                _operacionState.value = OperacionUiState.Exitosa
                _eventFlow.emit("Foto de evidencia subida por $username")
            } catch (e: Exception) {
                _actividades.update { lista ->
                    lista.map { actividad ->
                        if (actividad.id == actividadId) {
                            actividad.copy(
                                evidencias = actividad.evidencias.map {
                                    if (it.userId == userId && it.status == EvidenciaStatus.SUBIENDO) {
                                        it.copy(status = EvidenciaStatus.FALLIDA)
                                    } else it
                                }
                            )
                        } else actividad
                    }
                }
                _operacionState.value = OperacionUiState.Fallida(
                    e.localizedMessage ?: "Error al subir la evidencia"
                )
            }
        }
    }

    fun eliminarEvidencia(actividadId: Long, userId: String?) {
        viewModelScope.launch {
            val actividad = _actividades.value.find { it.id == actividadId }
            // Buscar la evidencia específica por userId
            val searchId = if (userId.isNullOrBlank()) null else userId
            val evidencia = actividad?.evidencias?.find { it.userId == searchId } ?: return@launch

            _operacionState.value = OperacionUiState.EnCurso
            try {
                // 1. Borrar archivo del Storage
                val filename = if (evidencia.remoteUrl != null) {
                    evidencia.remoteUrl.substringAfterLast("/")
                } else {
                    evidencia.uri.substringAfterLast("/")
                }
                
                repository.deleteImagenEvidencia(filePath = filename)

                // 2. Borrar de la base de datos por actividadId y userId
                repository.deleteEvidenciaPorUsuario(actividadId, searchId)

                _actividades.update { lista ->
                    lista.map { act ->
                        if (act.id == actividadId) {
                            act.copy(evidencias = act.evidencias.filter { it.userId != searchId })
                        } else act
                    }
                }
                _operacionState.value = OperacionUiState.Exitosa
                _eventFlow.emit("Evidencia eliminada")
            } catch (e: Exception) {
                // Mensaje detallado si es error de SQL (columna no existe)
                val msg = if (e.message?.contains("user_id") == true) {
                    "Error: Asegúrate de haber ejecutado el SQL en Supabase para crear la columna user_id"
                } else {
                    "Error al eliminar evidencia: ${e.message}"
                }
                _operacionState.value = OperacionUiState.Fallida(msg)
            }
        }
    }
}