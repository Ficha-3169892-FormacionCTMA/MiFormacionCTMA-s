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

    // --- PRUEBAS HU-2: MEJORAR VALIDACIÓN DEL FORMULARIO ---

    @Test
    fun `HU-2 CP-01 - Título vacío - Retorna error obligatorio`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "", descripcion = "Desc", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("El título es obligatorio."))
    }

    @Test
    fun `HU-2 CP-02 - Título con longitud inferior al mínimo - Retorna error longitud`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "ab", descripcion = "Desc", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("El título debe tener al menos 3 caracteres."))
    }

    @Test
    fun `HU-2 CP-03 - Título con longitud válida - No retorna error`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título Válido", descripcion = "Desc", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertFalse(errores.any { it.contains("título", ignoreCase = true) })
    }

    @Test
    fun `HU-2 CP-04 - Título con longitud superior al máximo - Retorna error máximo`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "a".repeat(81), descripcion = "Desc", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("El título no debe superar los 80 caracteres."))
    }

    @Test
    fun `HU-2 CP-05 - Descripción vacía - Retorna error obligatorio`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("La descripción es obligatoria."))
    }

    @Test
    fun `HU-2 CP-06 - Descripción válida - No retorna error`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "Descripción válida", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertFalse(errores.any { it.contains("descripción", ignoreCase = true) })
    }

    @Test
    fun `HU-2 CP-07 - Fecha vacía o inválida - Retorna error formato`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "Desc", progreso = 50,
            fecha = "2024-13-45", diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("El formato de fecha debe ser YYYY-MM-DD."))
    }

    @Test
    fun `HU-2 CP-08 - Fecha válida - No retorna error`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "Desc", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertFalse(errores.any { it.contains("fecha", ignoreCase = true) })
    }

    @Test
    fun `HU-2 CP-09 - Progreso menor que 0 - Retorna error rango`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "Desc", progreso = -1,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("El progreso debe estar entre 0 y 100."))
    }

    @Test
    fun `HU-2 CP-10 - Progreso mayor que 100 - Retorna error rango`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "Desc", progreso = 101,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.contains("El progreso debe estar entre 0 y 100."))
    }

    @Test
    fun `HU-2 CP-11 - Progreso en los límites - Acepta 0 y 100`() {
        val actividad0 = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "Desc", progreso = 0,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        val actividad100 = ActividadFormativa(
            id = 2L, titulo = "Título", descripcion = "Desc", progreso = 100,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertTrue(validarActividad(actividad0).none { it.contains("progreso") })
        assertTrue(validarActividad(actividad100).none { it.contains("progreso") })
    }

    @Test
    fun `HU-2 CP-14 - Formulario completamente válido - No retorna errores`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título Válido", descripcion = "Descripción Válida", progreso = 100,
            fecha = getHoyStr(), diasRestantes = 0, prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.isEmpty())
    }

    @Test
    fun `HU-2 CP-15 - Formulario completamente inválido - Retorna múltiples errores`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "", descripcion = "", progreso = 150,
            fecha = getAyerStr(), diasRestantes = 0, prioridad = Prioridad.ALTA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.size >= 4)
    }

    @Test
    fun `HU-2 CP-16 - Regresión de creación - El guardado funciona tras los cambios`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título Válido", descripcion = "Descripción Válida", progreso = 100,
            fecha = getHoyStr(), diasRestantes = 0, prioridad = Prioridad.MEDIA
        )
        val errores = validarActividad(actividad)
        assertTrue(errores.isEmpty())
    }

    // --- PRUEBAS HU-1: MEJORAR DISEÑO VISUAL DE LAS PANTALLAS ---

    @Test
    fun `HU-1 CP-11 - Prueba de regresión - Estado COMPLETADA cuando progreso es 100`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "T", descripcion = "D", progreso = 100,
            fecha = getHoyStr(), diasRestantes = 0, prioridad = Prioridad.MEDIA
        )
        assertEquals("COMPLETADA", estadoActividad(actividad))
    }

    @Test
    fun `HU-1 CP-01 - Visualización de actividades - Promedio de progreso correcto`() {
        val lista = listOf(
            ActividadFormativa(1L, "T1", "D1", 50, getHoyStr(), 0, Prioridad.MEDIA),
            ActividadFormativa(2L, "T2", "D2", 100, getHoyStr(), 0, Prioridad.MEDIA)
        )
        assertEquals(75.0, promedioProgreso(lista), 0.01)
    }

    @Test
    fun `HU-1 CP-01 - Visualización de actividades - Filtrado por título funciona`() {
        val lista = listOf(
            ActividadFormativa(1L, "Kotlin Básico", "D1", 50, getHoyStr(), 0, Prioridad.MEDIA),
            ActividadFormativa(2L, "Java Avanzado", "D2", 100, getHoyStr(), 0, Prioridad.MEDIA)
        )
        val resultado = buscarPorTitulo(lista, "kotlin")
        assertEquals(1, resultado.size)
        assertEquals("Kotlin Básico", resultado[0].titulo)
    }
}
