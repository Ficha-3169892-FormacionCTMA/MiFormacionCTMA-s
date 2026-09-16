package com.example.miformacionctma.domain

import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class ReglasActividadTest {

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun getHoyStr(): String {
        return sdf.format(Date())
    }

    @Test
    fun `HU 4 CP-01 - Verificar consistencia visual - Título obligatorio`() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "",
            descripcion = "Desc",
            progreso = 50,
            fecha = getHoyStr(),
            diasRestantes = 5,
            prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("El título es obligatorio."))
    }

    @Test
    fun `HU 4 CP-02 - Verificar tarjetas de actividades - Título demasiado corto`() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "ab",
            descripcion = "Desc",
            progreso = 50,
            fecha = getHoyStr(),
            diasRestantes = 5,
            prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("El título debe tener al menos 3 caracteres."))
    }

    @Test
    fun `HU 4 CP-03 - Verificar creación de actividades - Datos válidos`() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "Título Válido",
            descripcion = "Descripción obligatoria",
            progreso = 50,
            fecha = getHoyStr(),
            diasRestantes = 5,
            prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.isEmpty())
    }

    @Test
    fun `HU 4 CP-04 - Verificar pantalla de detalle - Título límite máximo`() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "a".repeat(81),
            descripcion = "Desc",
            progreso = 50,
            fecha = getHoyStr(),
            diasRestantes = 5,
            prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("El título no debe superar los 80 caracteres."))
    }

    @Test
    fun `HU 4 CP-08 - Verificar colores y contraste - Descripción no vacía`() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "Título",
            descripcion = "",
            progreso = 50,
            fecha = getHoyStr(),
            diasRestantes = 5,
            prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("La descripción es obligatoria."))
    }

    @Test
    fun `HU 4 CP-09 - Verificar estados de los componentes - Progreso inválido`() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "Título",
            descripcion = "Desc",
            progreso = -1,
            fecha = getHoyStr(),
            diasRestantes = 5,
            prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("El progreso debe estar entre 0 y 100."))
    }

    @Test
    fun `HU 4 CP-11 - Verificar funcionalidad existente - Fecha pasada`() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "Título",
            descripcion = "Desc",
            progreso = 50,
            fecha = "2020-01-01",
            diasRestantes = -1000,
            prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("La fecha no puede ser anterior a hoy."))
    }

    @Test
    fun `HU 4 CP-12 - Verificar tarjetas de actividades - Descripción demasiado larga`() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "Título",
            descripcion = "a".repeat(241),
            progreso = 50,
            fecha = getHoyStr(),
            diasRestantes = 5,
            prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("La descripción no debe superar los 240 caracteres."))
    }
}
