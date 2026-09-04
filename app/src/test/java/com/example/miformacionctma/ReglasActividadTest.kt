package com.example.miformacionctma

import com.example.miformacionctma.domain.validarTitulo
import com.example.miformacionctma.domain.validarProgreso
import com.example.miformacionctma.domain.validarFecha
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pruebas unitarias para HU-04: Lógica de validación de campos.
 */
class ReglasActividadTest {

    @Test
    fun `HU-04 CP-03 Validar titulo vacio`() {
        val resultado = validarTitulo("")
        assertEquals("El título es obligatorio.", resultado)
    }

    @Test
    fun `HU-04 CP-03 Validar titulo corto`() {
        val resultado = validarTitulo("Ab")
        assertEquals("El título debe tener al menos 3 caracteres.", resultado)
    }

    @Test
    fun `HU-04 CP-03 Validar titulo largo`() {
        val tituloLargo = "A".repeat(81)
        val resultado = validarTitulo(tituloLargo)
        assertEquals("El título no puede exceder los 80 caracteres.", resultado)
    }

    @Test
    fun `HU-04 CP-03 Validar progreso fuera de rango`() {
        assertNotNull(validarProgreso("-1"))
        assertNotNull(validarProgreso("101"))
        assertNotNull(validarProgreso("abc"))
        assertNull(validarProgreso("50"))
    }

    @Test
    fun `HU-04 CP-03 Validar fecha anterior a hoy`() {
        // Asumiendo que hoy es posterior al año 2000
        val resultado = validarFecha("1999-01-01")
        assertEquals("La fecha no puede ser anterior a hoy.", resultado)
    }
}
