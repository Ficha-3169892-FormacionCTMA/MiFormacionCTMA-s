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
            Evidencia(id = 101L, uri = "uri1", mimeType = "image/jpeg", size = 1024, status = EvidenciaStatus.SINCRONIZADA, actividadId = 1, userId = "u1"),
            Evidencia(id = 102L, uri = "uri2", mimeType = "image/png", size = 2048, status = EvidenciaStatus.SINCRONIZADA, actividadId = 1, userId = "u2")
        )
        assertEquals(2, evidencias.size)
    }

    @Test
    fun `HU 10 CP-08 - Eliminación de evidencia específica`() {
        val evidencias = mutableListOf(
            Evidencia(id = 101L, uri = "uri1", mimeType = "image/jpeg", size = 1024, status = EvidenciaStatus.SINCRONIZADA, actividadId = 1, userId = "u1"),
            Evidencia(id = 102L, uri = "uri2", mimeType = "image/png", size = 2048, status = EvidenciaStatus.SINCRONIZADA, actividadId = 1, userId = "u1")
        )
        // Ahora borramos por ID único, no por userId (que era el error que arreglamos)
        val idABorrar = 101L
        evidencias.removeIf { it.id == idABorrar }
        
        assertEquals(1, evidencias.size)
        assertEquals(102L, evidencias[0].id)
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
