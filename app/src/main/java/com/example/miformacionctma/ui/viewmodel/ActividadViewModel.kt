package com.example.miformacionctma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miformacionctma.data.repository.ActividadRepository
import com.example.miformacionctma.domain.ActividadFormativa
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActividadViewModel(private val repository: ActividadRepository) : ViewModel() {

    val actividades: StateFlow<List<ActividadFormativa>> = repository.todasLasActividades
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.checkInitialData()
        }
    }

    fun agregarActividad(actividad: ActividadFormativa) {
        viewModelScope.launch {
            repository.insertar(actividad)
        }
    }

    fun actualizarProgreso(id: Long, nuevoProgreso: Int) {
        viewModelScope.launch {
            val actividadActual = actividades.value.find { it.id == id }
            actividadActual?.let {
                repository.actualizar(it.copy(progreso = nuevoProgreso))
            }
        }
    }

    fun eliminarActividad(id: Long) {
        viewModelScope.launch {
            repository.eliminar(id)
        }
    }
}
