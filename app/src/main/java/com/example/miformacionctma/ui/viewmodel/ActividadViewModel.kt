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
            _actividades.update { lista ->
                lista.map { if (it.id == id) it.copy(estado = nuevoEstado) else it }
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
        size: Long
    ) {
        viewModelScope.launch {
            // Actualización inmediata para mostrar la previsualización local
            _actividades.update { lista ->
                lista.map { actividad ->
                    if (actividad.id == actividadId) {
                        actividad.copy(
                            evidencia = Evidencia(
                                uri = uri.toString(),
                                mimeType = mimeType,
                                size = size,
                                status = EvidenciaStatus.SUBIENDO,
                                actividadId = actividadId
                            )
                        )
                    } else actividad
                }
            }

            _operacionState.value = OperacionUiState.EnCurso
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: throw Exception("No se pudo leer el archivo de la imagen")

                val fileName = "evidencia_${actividadId}_${System.currentTimeMillis()}.jpg"

                val publicUrl = repository.uploadImagenEvidencia(
                    bucketName = "evidencias",
                    fileName = fileName,
                    fileBytes = bytes
                )

                repository.insertEvidencia(
                    EvidenciaDto(
                        actividadId = actividadId,
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
                                evidencia = actividad.evidencia?.copy(
                                    status = EvidenciaStatus.SINCRONIZADA,
                                    remoteUrl = publicUrl
                                )
                            )
                        } else actividad
                    }
                }

                _operacionState.value = OperacionUiState.Exitosa
                _eventFlow.emit("Foto de evidencia subida a Supabase Storage")
            } catch (e: Exception) {
                _actividades.update { lista ->
                    lista.map { actividad ->
                        if (actividad.id == actividadId) {
                            actividad.copy(
                                evidencia = actividad.evidencia?.copy(status = EvidenciaStatus.FALLIDA)
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

    fun eliminarEvidencia(actividadId: Long) {
        viewModelScope.launch {
            val actividad = _actividades.value.find { it.id == actividadId }
            val evidencia = actividad?.evidencia ?: return@launch

            _operacionState.value = OperacionUiState.EnCurso
            try {
                // 1. Borrar archivo del Storage si existe remoteUrl
                // Para borrar necesitamos el path relativo. Supabase storage suele guardarlo en remoteUrl pero a veces solo necesitamos el path.
                // En upload usamos "evidencias/$fileName".
                val filePath = evidencia.remoteUrl?.substringAfterLast("/public/evidencias/") 
                    ?: evidencia.uri.substringAfterLast("/")
                
                // Si la URL es la pública de Supabase, suele ser .../storage/v1/object/public/evidencias/archivo.jpg
                val cleanPath = if (filePath.contains("evidencias/")) filePath.substringAfter("evidencias/") else filePath
                
                repository.deleteImagenEvidencia(filePath = cleanPath)

                // 2. Borrar de la base de datos (se asume que hay un ID de evidencia o se identifica por actividad)
                // Como nuestra Evidencia no tiene ID propio en el domain, pero si en DTO, y el Repo usa ID de evidencia.
                // Sin embargo, podemos buscarla por actividadId si solo hay una.
                // El repository.deleteEvidencia(id) espera el ID de la tabla evidencias.
                // Necesitamos el ID. Modifiquemos el DTO o el repo para borrar por actividadId.
                
                // Por ahora, borremos por actividadId si el repo lo permite o ajustemos el repo.
                repository.deleteEvidenciaPorActividad(actividadId)

                _actividades.update { lista ->
                    lista.map { if (it.id == actividadId) it.copy(evidencia = null) else it }
                }
                _operacionState.value = OperacionUiState.Exitosa
                _eventFlow.emit("Evidencia eliminada")
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Fallida("Error al eliminar evidencia: ${e.message}")
            }
        }
    }
}