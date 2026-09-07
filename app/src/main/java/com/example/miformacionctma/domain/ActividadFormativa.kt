package com.example.miformacionctma.domain

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
    val prioridad: Prioridad
)
