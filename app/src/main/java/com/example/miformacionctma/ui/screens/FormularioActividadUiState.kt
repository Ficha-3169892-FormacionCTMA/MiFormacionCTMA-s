package com.example.miformacionctma.ui.screens

import com.example.miformacionctma.domain.Prioridad

data class FormularioActividadUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val prioridad: Prioridad = Prioridad.MEDIA,
    val progreso: String = "0",
    val errores: Map<String, String> = emptyMap(),
    val puedeGuardar: Boolean = false
)
