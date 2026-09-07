package com.example.miformacionctma.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad

@Entity(tableName = "actividades")
data class ActividadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descripcion: String?,
    val progreso: Int,
    val fecha: String,
    val diasRestantes: Int,
    val prioridad: Prioridad
)

fun ActividadEntity.toDomain() = ActividadFormativa(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    progreso = progreso,
    fecha = fecha,
    diasRestantes = diasRestantes,
    prioridad = prioridad
)

fun ActividadFormativa.toEntity() = ActividadEntity(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    progreso = progreso,
    fecha = fecha,
    diasRestantes = diasRestantes,
    prioridad = prioridad
)
