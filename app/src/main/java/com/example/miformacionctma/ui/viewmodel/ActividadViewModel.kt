package com.example.miformacionctma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miformacionctma.data.ActividadDataStore
import com.example.miformacionctma.domain.ActividadFormativa
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActividadViewModel(private val dataStore: ActividadDataStore) : ViewModel() {

    val actividades: StateFlow<List<ActividadFormativa>> = dataStore.actividadesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregarActividad(actividad: ActividadFormativa) {
        viewModelScope.launch {
            val actual = actividades.value.toMutableList()
            actual.add(actividad)
            dataStore.guardarActividades(actual)
        }
    }

    fun actualizarProgreso(id: Long, nuevoProgreso: Int) {
        viewModelScope.launch {
            val actual = actividades.value.map {
                if (it.id == id) it.copy(progreso = nuevoProgreso) else it
            }
            dataStore.guardarActividades(actual)
        }
    }

    fun eliminarActividad(id: Long) {
        viewModelScope.launch {
            val actual = actividades.value.filter { it.id != id }
            dataStore.guardarActividades(actual)
        }
    }
}
