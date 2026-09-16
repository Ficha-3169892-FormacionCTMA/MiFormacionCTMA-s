package com.example.miformacionctma.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrestamoModel(
    @SerialName("id") val id: Long? = null,
    @SerialName("usuario_id") val usuarioId: String? = null,
    @SerialName("tipo_elemento") val tipoElemento: String,
    @SerialName("observaciones") val observaciones: String? = null,
    @SerialName("estado") val estado: String? = "Solicitado",
    @SerialName("fecha_solicitud") val fechaSolicitud: String? = null
)