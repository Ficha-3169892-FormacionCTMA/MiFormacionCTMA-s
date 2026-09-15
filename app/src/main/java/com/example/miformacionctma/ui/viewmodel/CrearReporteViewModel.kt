package com.example.miformacionctma.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.miformacionctma.domain.Reporte
import com.example.miformacionctma.domain.ReporteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class CrearUiState(
    val titulo: String = "",
    val errorTitulo: String? = null,
    val guardando: Boolean = false,
    val guardadoId: String? = null
)

class CrearReporteViewModel(
    private val repository: ReporteRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CrearUiState())
    val uiState: StateFlow<CrearUiState> = _uiState.asStateFlow()

    fun actualizarTitulo(valor: String) {
        if (valor.length <= 80) {
            _uiState.update { state ->
                state.copy(
                    titulo = valor,
                    errorTitulo = if (valor.length >= 4) null else state.errorTitulo
                )
            }
        }
    }

    fun guardar() {
        val tituloActual = _uiState.value.titulo
        if (tituloActual.isBlank() || tituloActual.length < 4) {
            _uiState.update { 
                it.copy(errorTitulo = "El título debe tener al menos 4 caracteres y no estar en blanco") 
            }
            return
        }

        _uiState.update { it.copy(guardando = true) }
        
        val nuevoReporte = Reporte(
            id = UUID.randomUUID().toString(),
            titulo = tituloActual
        )
        
        repository.agregar(nuevoReporte)
        
        _uiState.update { 
            it.copy(
                guardando = false,
                guardadoId = nuevoReporte.id
            )
        }
    }
}
