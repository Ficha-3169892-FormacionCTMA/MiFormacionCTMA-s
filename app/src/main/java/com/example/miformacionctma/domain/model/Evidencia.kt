package com.example.miformacionctma.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class EvidenciaStatus {
    LOCAL,
    SUBIENDO,
    SINCRONIZADA,
    FALLIDA
}

@Serializable
data class Evidencia(
    val uri: String,
    val mimeType: String,
    val size: Long,
    val status: EvidenciaStatus,
    val actividadId: Long,
    val remoteUrl: String? = null
)
