package com.example.miformacionctma.ui.state

import com.example.miformacionctma.domain.ActividadFormativa

/**
 * Representa los 4 estados exclusivos del listado principal.
 * Garantiza que la UI solo maneje un estado válido a la vez.
 */
sealed interface ListadoUiState {
    data object Cargando : ListadoUiState
    data object Vacio : ListadoUiState
    data class Contenido(val actividades: List<ActividadFormativa>) : ListadoUiState
    data class Error(val mensaje: String) : ListadoUiState
}

/**
 * Representa el estado de operaciones CRUD (Guardar, Editar, Eliminar).
 * Permite manejar feedback visual como Loading Spinners o Diálogos de error.
 */
sealed interface OperacionUiState {
    data object Inactiva : OperacionUiState
    data object EnCurso : OperacionUiState
    data object Exitosa : OperacionUiState
    data class Fallida(val mensaje: String) : OperacionUiState
}
