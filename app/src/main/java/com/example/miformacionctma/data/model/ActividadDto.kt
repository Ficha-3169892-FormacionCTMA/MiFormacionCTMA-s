package com.example.miformacionctma.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActividadDto(
    val id: Long? = null,
    val titulo: String,
    val descripcion: String? = null,
    val estado: String = "ESPERA",
    val progreso: Int = 0,
    @SerialName("user_id")
    val userId: String? = null
)

@Serializable
data class EvidenciaDto(
    val id: Long? = null,
    @SerialName("actividad_id")
    val actividadId: Long,
    @SerialName("file_path")
    val filePath: String,
    @SerialName("file_url")
    val fileUrl: String,
    @SerialName("mime_type")
    val mimeType: String,
    @SerialName("size_bytes")
    val sizeBytes: Long,
    val status: String = "SUBIDA"
)

@Serializable
data class ActividadDtoWithEvidencias(
    val id: Long? = null,
    val titulo: String,
    val descripcion: String? = null,
    val estado: String = "ESPERA",
    val progreso: Int = 0,
    @SerialName("user_id")
    val userId: String? = null,
    val evidencias: List<EvidenciaDto> = emptyList()
)