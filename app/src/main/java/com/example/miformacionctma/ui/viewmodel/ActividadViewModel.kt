package com.example.miformacionctma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miformacionctma.data.ActividadDataSource
import com.example.miformacionctma.data.SortOrder
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.ui.state.ListadoUiState
import com.example.miformacionctma.ui.state.OperacionUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ActividadViewModel(private val dataSource: ActividadDataSource) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val sortOrder: StateFlow<SortOrder> = dataSource.sortOrderFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SortOrder.FECHA)

    // REGLA SEMANA 7: combine para reactividad avanzada con múltiples fuentes (CA-03)
    val uiState: StateFlow<ListadoUiState> = combine(
        dataSource.actividadesFlow,
        dataSource.sortOrderFlow,
        _searchQuery
    ) { actividades, order, query ->
        val filtradas = if (query.isBlank()) {
            actividades
        } else {
            actividades.filter { it.titulo.contains(query, ignoreCase = true) }
        }

        val ordenadas = when (order) {
            SortOrder.FECHA -> filtradas.sortedBy { it.fecha }
            SortOrder.TITULO -> filtradas.sortedBy { it.titulo }
            SortOrder.PROGRESO -> filtradas.sortedByDescending { it.progreso }
        }

        if (ordenadas.isEmpty()) ListadoUiState.Vacio
        else ListadoUiState.Contenido(ordenadas)
    }
    .catch { e ->
        emit(ListadoUiState.Error(e.message ?: "Error al cargar los datos"))
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ListadoUiState.Cargando
    )

    private val _operacionState = MutableStateFlow<OperacionUiState>(OperacionUiState.Inactiva)
    val operacionState: StateFlow<OperacionUiState> = _operacionState.asStateFlow()

    fun actualizarBusqueda(query: String) {
        _searchQuery.value = query
    }

    fun cambiarOrden(order: SortOrder) {
        viewModelScope.launch {
            dataSource.updateSortOrder(order)
        }
    }

    fun agregarActividad(actividad: ActividadFormativa) {
        ejecutarOperacion {
            val actual = dataSource.actividadesFlow.first().toMutableList()
            actual.add(actividad)
            dataSource.guardarActividades(actual)
        }
    }

    fun actualizarProgreso(id: Long, nuevoProgreso: Int) {
        ejecutarOperacion {
            val actual = dataSource.actividadesFlow.first().map {
                if (it.id == id) it.copy(progreso = nuevoProgreso) else it
            }
            dataSource.guardarActividades(actual)
        }
    }

    fun eliminarActividad(id: Long) {
        ejecutarOperacion {
            val actual = dataSource.actividadesFlow.first().filter { it.id != id }
            dataSource.guardarActividades(actual)
        }
    }

    private fun ejecutarOperacion(bloque: suspend () -> Unit) {
        viewModelScope.launch {
            _operacionState.value = OperacionUiState.EnCurso
            try {
                bloque()
                _operacionState.value = OperacionUiState.Exitosa
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Fallida(e.message ?: "Error en la operación")
            } finally {
                _operacionState.value = OperacionUiState.Inactiva
            }
        }
    }
}
