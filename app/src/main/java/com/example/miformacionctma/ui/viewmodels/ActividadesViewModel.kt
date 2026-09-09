package com.example.miformacionctma.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miformacionctma.data.AppDatabase
import com.example.miformacionctma.data.RoomActividadRepository
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * Estados de la interfaz de usuario para el listado de actividades.
 * Según directrices de la Semana 7 (Punto 8).
 */
sealed interface ListadoUiState {
    data object Cargando : ListadoUiState
    data object Vacio : ListadoUiState
    data class Contenido(val actividades: List<ActividadFormativa>) : ListadoUiState
    data class Error(val mensaje: String) : ListadoUiState
}

class ActividadesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RoomActividadRepository by lazy {
        RoomActividadRepository(AppDatabase.getDatabase(application).actividadDao())
    }

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    private val _prioridadFiltro = MutableStateFlow<Prioridad?>(null)
    val prioridadFiltro: StateFlow<Prioridad?> = _prioridadFiltro

    // Implementación robusta del estado de UI (Punto 9)
    val uiState: StateFlow<ListadoUiState> = combine(
        repository.observarActividades(),
        _busqueda,
        _prioridadFiltro
    ) { lista, texto, prioridad ->
        lista.filter { actividad ->
            val coincideTexto = actividad.titulo.contains(texto, ignoreCase = true)
            val coincidePrioridad = prioridad == null || actividad.prioridad == prioridad
            coincideTexto && coincidePrioridad
        }
    }
    .map { filtradas ->
        if (filtradas.isEmpty()) ListadoUiState.Vacio
        else ListadoUiState.Contenido(filtradas)
    }
    .catch { error ->
        if (error is CancellationException) throw error
        emit(ListadoUiState.Error("Error al cargar actividades: ${error.localizedMessage}"))
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ListadoUiState.Cargando
    )

    fun actualizarBusqueda(nuevoTexto: String) {
        _busqueda.value = nuevoTexto
    }

    fun filtrarPorPrioridad(prioridad: Prioridad?) {
        _prioridadFiltro.value = prioridad
    }

    fun agregarActividad(actividad: ActividadFormativa) {
        viewModelScope.launch {
            repository.guardar(actividad)
        }
    }

    fun completarActividad(actividad: ActividadFormativa) {
        viewModelScope.launch {
            repository.completar(actividad)
        }
    }

    fun borrarActividad(actividad: ActividadFormativa) {
        viewModelScope.launch {
            repository.eliminar(actividad)
        }
    }
}
