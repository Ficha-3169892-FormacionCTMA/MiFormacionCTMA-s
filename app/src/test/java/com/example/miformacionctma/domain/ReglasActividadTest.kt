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

    // --- PRUEBAS HU-4: MEJORAR PERSISTENCIA DEL ESTADO DE LAS ACTIVIDADES ---

    @Test
    fun `HU-4 CP-01 - Crear y guardar una actividad - Validación lo acepta`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Actividad Persistente", descripcion = "Descripción obligatoria", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertTrue(validarActividad(actividad).isEmpty())
    }

    @Test
    fun `HU-4 CP-07 - Información completa - Todos los datos son válidos`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título Válido", descripcion = "Descripción Válida", progreso = 100,
            fecha = getHoyStr(), diasRestantes = 0, prioridad = Prioridad.ALTA
        )
        assertTrue(validarActividad(actividad).isEmpty())
    }

    // --- PRUEBAS HU-3: AGREGAR PRUEBAS UNITARIAS ---

    @Test
    fun `HU-3 CP-01 - Título vacío - Validación lo rechaza`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "", descripcion = "Desc", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertTrue(validarActividad(actividad).any { it.contains("título", ignoreCase = true) })
    }

    @Test
    fun `HU-3 CP-02 - Título válido - Validación lo acepta`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título Válido", descripcion = "Desc", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertFalse(validarActividad(actividad).any { it.contains("título", ignoreCase = true) })
    }

    @Test
    fun `HU-3 CP-03 - Título demasiado corto - Validación lo rechaza`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "ab", descripcion = "Desc", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertTrue(validarActividad(actividad).any { it.contains("3 caracteres") })
    }

    @Test
    fun `HU-3 CP-04 - Título demasiado largo - Validación lo rechaza`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "a".repeat(81), descripcion = "Desc", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertTrue(validarActividad(actividad).any { it.contains("80 caracteres") })
    }

    @Test
    fun `HU-3 CP-05 - Descripción vacía - Validación la rechaza`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertTrue(validarActividad(actividad).any { it.contains("descripción", ignoreCase = true) })
    }

    @Test
    fun `HU-3 CP-06 - Descripción válida - Validación la acepta`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "Descripción válida", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertFalse(validarActividad(actividad).any { it.contains("descripción", ignoreCase = true) })
    }

    @Test
    fun `HU-3 CP-07 - Progreso igual a 0 - Validación lo acepta`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "Desc", progreso = 0,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertFalse(validarActividad(actividad).any { it.contains("progreso", ignoreCase = true) })
    }

    @Test
    fun `HU-3 CP-08 - Progreso igual a 100 - Validación lo acepta`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "Desc", progreso = 100,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertFalse(validarActividad(actividad).any { it.contains("progreso", ignoreCase = true) })
    }

    @Test
    fun `HU-3 CP-09 - Progreso menor que 0 - Validación lo rechaza`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "Desc", progreso = -1,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertTrue(validarActividad(actividad).any { it.contains("progreso", ignoreCase = true) })
    }

    @Test
    fun `HU-3 CP-10 - Progreso mayor que 100 - Validación lo rechaza`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "Desc", progreso = 101,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertTrue(validarActividad(actividad).any { it.contains("progreso", ignoreCase = true) })
    }

    @Test
    fun `HU-3 CP-11 - Fecha válida - Validación la acepta`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "Desc", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertFalse(validarActividad(actividad).any { it.contains("fecha", ignoreCase = true) })
    }

    @Test
    fun `HU-3 CP-12 - Fecha inválida - Validación la rechaza`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título", descripcion = "Desc", progreso = 50,
            fecha = "fecha-invalida", diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertTrue(validarActividad(actividad).any { it.contains("fecha", ignoreCase = true) })
    }

    // --- PRUEBAS HU-2: MEJORAR VALIDACIÓN DEL FORMULARIO ---

    @Test
    fun `HU-2 CP-01 - Título vacío - Retorna error obligatorio`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "", descripcion = "Desc", progreso = 50,
            fecha = getHoyStr(), diasRestantes = 5, prioridad = Prioridad.ALTA
        )
        assertTrue(validarActividad(actividad).contains("El título es obligatorio."))
    }

    @Test
    fun `HU-2 CP-16 - Regresión de creación - El guardado funciona tras los cambios`() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Título Válido", descripcion = "Descripción Válida", progreso = 100,
            fecha = getHoyStr(), diasRestantes = 0, prioridad = Prioridad.MEDIA
        )
        assertTrue(validarActividad(actividad).isEmpty())
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
}
