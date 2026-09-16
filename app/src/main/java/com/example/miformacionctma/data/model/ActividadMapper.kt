package com.example.miformacionctma.data.model

import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.model.ActividadEstado
import com.example.miformacionctma.domain.model.Evidencia
import com.example.miformacionctma.domain.model.EvidenciaStatus

fun ActividadDto.toDomain(): ActividadFormativa {
    return ActividadFormativa(
        id = this.id ?: 0L,
        titulo = this.titulo,
        descripcion = this.descripcion ?: "",
        estado = when (this.estado?.uppercase()) {
            "ESPERA" -> ActividadEstado.ESPERA
            "EN_CURSO" -> ActividadEstado.EN_CURSO
            "LISTA" -> ActividadEstado.LISTA
            "MAL" -> ActividadEstado.MAL
            else -> ActividadEstado.ESPERA
        },
        progreso = this.progreso,
        fecha = "",
        diasRestantes = 0,
        prioridad = Prioridad.MEDIA,
        instructorId = null,
        estudianteId = null,
        evidencias = emptyList()
    )
}

fun ActividadDtoWithEvidencias.toDomain(): ActividadFormativa {
    val evidenciasDomain = evidencias.map {
        Evidencia(
            uri = it.fileUrl,
            mimeType = it.mimeType,
            size = it.sizeBytes,
            status = EvidenciaStatus.SINCRONIZADA,
            actividadId = it.actividadId,
            userId = it.userId,
            userName = it.userName,
            remoteUrl = it.fileUrl
        )
    }

    return ActividadFormativa(
        id = this.id ?: 0L,
        titulo = this.titulo,
        descripcion = this.descripcion ?: "",
        estado = when (this.estado?.uppercase()) {
            "ESPERA" -> ActividadEstado.ESPERA
            "EN_CURSO" -> ActividadEstado.EN_CURSO
            "LISTA" -> ActividadEstado.LISTA
            "MAL" -> ActividadEstado.MAL
            else -> ActividadEstado.ESPERA
        },
        progreso = this.progreso,
        fecha = "",
        diasRestantes = 0,
        prioridad = Prioridad.MEDIA,
        instructorId = null,
        estudianteId = null,
        evidencias = evidenciasDomain
    )
}

fun ActividadFormativa.toDto(): ActividadDto {
    return ActividadDto(
        id = if (this.id == 0L) null else this.id,
        titulo = this.titulo,
        descripcion = this.descripcion,
        estado = this.estado.name,
        progreso = this.progreso
    )
}