package com.example.miformacionctma.domain

<<<<<<< HEAD
data class ActividadFormativa(
    val id: Long,
    val titulo: String,
    val description: String?,
    val progreso: Int,
    val diasRestantes: Int,
    val prioridad: Prioridad
)

enum class Prioridad{
    Baja,
    Media,
    Alta
}

enum class EstadoActividad {
    Pendiente,
    En_progreso,
    Vencida,
    Completada
}

=======
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
    val evidencia: Evidencia? = null
)
>>>>>>> 1186d539fa38240e1aaef8eb9b67f337fe058051
