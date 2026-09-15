package com.example.miformacionctma.data.model

import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.model.ActividadEstado

fun ActividadDto.toDomain(): ActividadFormativa {
    return ActividadFormativa(
        id = this.id ?: 0L,
        titulo = this.titulo,
        descripcion = this.descripcion ?: "",
        estado = when (this.estado?.uppercase()) {
            "COMPLETADA" -> ActividadEstado.entries.find { it.name.equals("COMPLETADA", ignoreCase = true) } ?: ActividadEstado.entries.first()
            "EN_PROGRESO" -> ActividadEstado.entries.find { it.name.equals("EN_PROGRESO", ignoreCase = true) } ?: ActividadEstado.entries.first()
            else -> ActividadEstado.entries.first()
        },
        progreso = 0,
        fecha = "",
        diasRestantes = 0,
        prioridad = Prioridad.MEDIA, // Se reemplaza "" por un valor válido de Prioridad
        instructorId = null,
        estudianteId = null,
        evidencia = null
    )
}

fun ActividadFormativa.toDto(): ActividadDto {
    return ActividadDto(
        id = if (this.id == 0L) null else this.id,
        titulo = this.titulo,
        descripcion = this.descripcion,
        estado = this.estado.name
    )
}