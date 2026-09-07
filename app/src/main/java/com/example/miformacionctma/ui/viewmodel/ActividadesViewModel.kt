package com.example.miformacionctma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miformacionctma.data.ActividadDataSource
import com.example.miformacionctma.ui.state.ListadoUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ActividadesViewModel(private val dataSource: ActividadDataSource) : ViewModel() {

    val uiState: StateFlow<ListadoUiState> = dataSource.actividadesFlow
        .map { lista ->
            if (lista.isEmpty()) ListadoUiState.Vacio
            else ListadoUiState.Contenido(lista)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ListadoUiState.Cargando
        )
}
