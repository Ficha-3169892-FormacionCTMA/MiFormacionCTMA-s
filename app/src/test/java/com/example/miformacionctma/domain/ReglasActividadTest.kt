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

    private fun getAyerStr(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return sdf.format(cal.time)
    }

    @Test
    fun `CP-04 - Validación del formulario - Título vacío retorna error`() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "",
            descripcion = null,
            progreso = 50,
            fecha = getHoyStr(),
            diasRestantes = 5,
            prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("El título es obligatorio."))
    }

    @Test
    fun `CP-04 - Validación del formulario - Título corto retorna error`() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "ab",
            descripcion = null,
            progreso = 50,
            fecha = getHoyStr(),
            diasRestantes = 5,
            prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("El título debe tener al menos 3 caracteres."))
    }

    @Test
    fun `CP-04 - Validación del formulario - Fecha anterior a hoy retorna error`() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "Título válido",
            descripcion = null,
            progreso = 50,
            fecha = getAyerStr(),
            diasRestantes = 5,
            prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("La fecha no puede ser anterior a hoy."))
    }

    @Test
    fun `CP-04 - Validación del formulario - Progreso fuera de rango retorna error`() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "Título válido",
            descripcion = null,
            progreso = 150,
            fecha = getHoyStr(),
            diasRestantes = 5,
            prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("El progreso debe estar entre 0 y 100."))
    }

    @Test
    fun `CP-03 - Creación de actividad - Datos válidos no retorna errores`() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "Título válido",
            descripcion = "Descripción válida",
            progreso = 100,
            fecha = getHoyStr(),
            diasRestantes = 0,
            prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.isEmpty())
    }

    @Test
    fun `CP-11 - Prueba de regresión - Estado COMPLETADA cuando progreso es 100`() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "T",
            descripcion = null,
            progreso = 100,
            fecha = getHoyStr(),
            diasRestantes = 0,
            prioridad = Prioridad.MEDIA
        )
        assertEquals("COMPLETADA", estadoActividad(actividad))
    }

    @Test
    fun `CP-01 - Visualización de actividades - PromedioProgreso cálculo correcto`() {
        val lista = listOf(
            ActividadFormativa(1L, "T1", null, 50, getHoyStr(), 0, Prioridad.MEDIA),
            ActividadFormativa(2L, "T2", null, 100, getHoyStr(), 0, Prioridad.MEDIA)
        )
        assertEquals(75.0, promedioProgreso(lista), 0.01)
    }

    @Test
    fun `CP-01 - Visualización de actividades - BuscarPorTitulo filtrado correcto`() {
        val lista = listOf(
            ActividadFormativa(1L, "Kotlin Básico", null, 50, getHoyStr(), 0, Prioridad.MEDIA),
            ActividadFormativa(2L, "Java Avanzado", null, 100, getHoyStr(), 0, Prioridad.MEDIA)
        )
        val resultado = buscarPorTitulo(lista, "kotlin")
        assertEquals(1, resultado.size)
        assertEquals("Kotlin Básico", resultado[0].titulo)
    }
}
