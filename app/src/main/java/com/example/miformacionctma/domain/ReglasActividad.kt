package com.example.miformacionctma.domain


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
    }

    return errores
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
    }
}