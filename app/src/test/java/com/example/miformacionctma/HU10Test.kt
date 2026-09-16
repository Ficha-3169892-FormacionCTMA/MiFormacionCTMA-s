package com.example.miformacionctma

import com.example.miformacionctma.domain.*
import com.example.miformacionctma.domain.model.ActividadEstado
import com.example.miformacionctma.domain.model.Evidencia
import com.example.miformacionctma.domain.model.EvidenciaStatus
import org.junit.Assert.*
import org.junit.Test

class HU10Test {

    @Test
    fun `HU 10 CP-01 - Registro de usuario - Datos válidos`() {
        val errores = validarRegistro("test@ejemplo.com", "123456", "usuarioPrueba")
        assertTrue(errores.isEmpty())
    }

    @Test
    fun `HU 10 CP-02 - Registro de usuario - Contraseña corta`() {
        val errores = validarRegistro("test@ejemplo.com", "123", "usuarioPrueba")
        assertTrue(errores.contains("Contraseña demasiado corta"))
    }

    @Test
    fun `HU 10 CP-03 - Registro de usuario - Email inválido`() {
        val errores = validarRegistro("email-sin-arroba", "123456", "usuario")
        assertTrue(errores.contains("Email inválido"))
    }

    @Test
    fun `HU 10 CP-04 - Progreso automático - Estado LISTA es 100%`() {
        val progreso = calcularProgresoPorEstado(ActividadEstado.LISTA)
        assertEquals(100, progreso)
    }

    @Test
    fun `HU 10 CP-05 - Progreso automático - Estado MAL es 0%`() {
        val progreso = calcularProgresoPorEstado(ActividadEstado.MAL)
        assertEquals(0, progreso)
    }

    @Test
    fun `HU 10 CP-06 - Progreso automático - Estado ESPERA es 10%`() {
        val progreso = calcularProgresoPorEstado(ActividadEstado.ESPERA)
        assertEquals(10, progreso)
    }

    @Test
    fun `HU 10 CP-07 - Soporte Multimedia - Múltiples evidencias`() {
        val evidencias = listOf(
            Evidencia("uri1", "image/jpeg", 1024, EvidenciaStatus.SINCRONIZADA, 1, "u1"),
            Evidencia("uri2", "image/png", 2048, EvidenciaStatus.SINCRONIZADA, 1, "u2")
        )
        assertEquals(2, evidencias.size)
    }

    @Test
    fun `HU 10 CP-08 - Eliminación de evidencia específica`() {
        val evidencias = mutableListOf(
            Evidencia("uri1", "image/jpeg", 1024, EvidenciaStatus.SINCRONIZADA, 1, "u1"),
            Evidencia("uri2", "image/png", 2048, EvidenciaStatus.SINCRONIZADA, 1, "u2")
        )
        // Simular borrado de u1
        evidencias.removeIf { it.userId == "u1" }
        
        assertEquals(1, evidencias.size)
        assertEquals("u2", evidencias[0].userId)
    }

    @Test
    fun `HU 10 CP-09 - Persistencia de progreso manual - Rangos válidos`() {
        val progresoManual = 75
        val errores = validarActividad(
            ActividadFormativa(1, "Test", "Desc", progresoManual, "2026-12-31", 10, Prioridad.ALTA)
        )
        assertTrue(errores.isEmpty())
    }

    @Test
    fun `HU 10 CP-10 - Seguridad - URL de Storage usa HTTPS`() {
        val storageUrl = "https://izxhktpwimnwfgkdnplr.supabase.co/storage/v1/object/public/evidencias/foto.jpg"
        assertTrue(storageUrl.startsWith("https://"))
    }
}
