package com.example.miformacionctma.domain

import com.example.miformacionctma.domain.model.ActividadEstado
import com.example.miformacionctma.domain.model.Evidencia
import kotlinx.serialization.Serializable

@Serializable
enum class Prioridad {
    BAJA,
    MEDIA,
    ALTA
}

@Serializable
data class ActividadFormativa(
    val id: Long,
    val titulo: String,
    val descripcion: String?,
    val progreso: Int,
    val fecha: String,
    val diasRestantes: Int,
    val prioridad: Prioridad,
    val instructorId: String? = null,
    val estudianteId: String? = null,
    val estado: ActividadEstado = ActividadEstado.ESPERA,
    val evidencias: List<Evidencia> = emptyList()
)
