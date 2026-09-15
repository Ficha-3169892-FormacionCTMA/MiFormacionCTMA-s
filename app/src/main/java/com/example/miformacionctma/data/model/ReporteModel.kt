package com.example.miformacionctma.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReporteModel(
    @SerialName("id") val id: Long? = null,
    @SerialName("titulo") val titulo: String,
    @SerialName("descripcion") val descripcion: String,
    @SerialName("categoria") val categoria: String? = "General",
    @SerialName("estado") val estado: String? = "Pendiente",
    @SerialName("created_at") val createdAt: String? = null
)