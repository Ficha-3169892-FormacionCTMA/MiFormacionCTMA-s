package com.example.miformacionctma.domain

<<<<<<< HEAD
fun estaAtrasada(actividad: ActividadFormativa): Boolean{
    return actividad.progreso < 100 && actividad.diasRestantes < 0
}

fun porcentajePendiente(actividad: ActividadFormativa): Int{
    return 100 - actividad.progreso
}

fun validarActividad(actividad: ActividadFormativa): List<String> {
    val errores = mutableListOf<String>()

    if (actividad.titulo.isBlank()) {
        errores.add("El título es obligatorio")
    }

    if (actividad.progreso !in 0..100) {
        errores.add("El progreso debe estar entre 0 y 100")
=======

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun validarActividad(actividad: ActividadFormativa): List<String> {

    val errores = mutableListOf<String>()

    if (actividad.titulo.isBlank()) {
        errores.add("El título es obligatorio.")
    } else if (actividad.titulo.length < 3) {
        errores.add("El título debe tener al menos 3 caracteres.")
    } else if (actividad.titulo.length > 80) {
        errores.add("El título no debe superar los 80 caracteres.")
    }

    if (actividad.descripcion.isNullOrBlank()) {
        errores.add("La descripción es obligatoria.")
    } else if (actividad.descripcion.length > 240) {
        errores.add("La descripción no debe superar los 240 caracteres.")
    }

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    sdf.isLenient = false
    try {
        val fechaIngresada = sdf.parse(actividad.fecha)
        val hoy = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        if (fechaIngresada != null && fechaIngresada.before(hoy)) {
            errores.add("La fecha no puede ser anterior a hoy.")
        }
    } catch (e: Exception) {
        errores.add("El formato de fecha debe ser YYYY-MM-DD.")
    }

    if (actividad.progreso !in 0..100) {
        errores.add("El progreso debe estar entre 0 y 100.")
>>>>>>> 1186d539fa38240e1aaef8eb9b67f337fe058051
    }

    return errores
}

<<<<<<< HEAD
fun estadoActividad(actividad: ActividadFormativa): EstadoActividad{
    return when {
        actividad.progreso == 100 -> EstadoActividad.Completada
        actividad.diasRestantes < 0 -> EstadoActividad.Vencida
        actividad.progreso == 0 -> EstadoActividad.Pendiente
        else -> EstadoActividad.En_progreso
    }
}

fun actividadesUrgentes(
    actividades: List<ActividadFormativa>
): List<ActividadFormativa> {
    return actividades.filter { actividad ->
        actividad.progreso < 100 &&
                actividad.diasRestantes <= 2
    }
}

fun  promedioProgreso(
    actividades: List<ActividadFormativa>
): Int {
    if (actividades.isEmpty()){
        return 0
    }

    return actividades.map {actividad ->
        actividad.progreso
    }.sum() / actividades.size
}

fun buscarPorTitulo(
    actividades: List<ActividadFormativa>,
    texto: String
): List<ActividadFormativa> {
    val textoLimpio = texto.trim()

    return actividades.filter { actividad ->
        actividad.titulo.contains(textoLimpio, ignoreCase = true)
=======
fun validarEvidencia(mimeType: String, size: Long): String? {
    if (size > 5 * 1024 * 1024) {
        return "La imagen supera el límite de 5MB."
    }
    if (!mimeType.startsWith("image/")) {
        return "Solo se permiten archivos de imagen."
    }
    return null
}

fun estadoActividad(actividad: ActividadFormativa): String {

    return when {
        actividad.progreso == 100 -> "COMPLETADA"
        actividad.diasRestantes < 0 -> "VENCIDA"
        actividad.progreso > 0 -> "EN PROCESO"
        else -> "PENDIENTE"
    }
}

fun actividadesUrgentes(lista: List<ActividadFormativa>): List<ActividadFormativa> {

    return lista.filter {
        it.progreso < 100 && it.diasRestantes <= 2
    }
}

fun promedioProgreso(lista: List<ActividadFormativa>): Double {

    if (lista.isEmpty()) return 0.0

    return lista.map { it.progreso }.average()
}

fun buscarPorTitulo(
    lista: List<ActividadFormativa>,
    titulo: String
): List<ActividadFormativa> {

    return lista.filter {
        it.titulo.trim().contains(
            titulo.trim(),
            ignoreCase = true
        )
>>>>>>> 1186d539fa38240e1aaef8eb9b67f337fe058051
    }
}