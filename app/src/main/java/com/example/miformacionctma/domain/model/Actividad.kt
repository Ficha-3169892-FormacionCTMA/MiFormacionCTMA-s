package com.example.miformacionctma.domain.model

import com.example.miformacionctma.domain.Prioridad
import kotlinx.serialization.Serializable

@Serializable
enum class ActividadEstado {
    ESPERA,
    EN_CURSO,
    LISTA,
    MAL
}

@Serializable
data class Actividad(
    val id: Long,
    val titulo: String,
    val descripcion: String?,
    val progreso: Int,
    val fecha: String,
    val prioridad: Prioridad,
    val instructorId: String? = null,
    val estudianteId: String? = null,
    val estado: ActividadEstado = ActividadEstado.ESPERA,
    val evidencia: Evidencia? = null
)
