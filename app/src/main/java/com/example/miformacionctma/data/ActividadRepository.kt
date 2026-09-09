package com.example.miformacionctma.data

import com.example.miformacionctma.domain.ActividadFormativa
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define las operaciones de datos para las actividades.
 * Sigue las directrices del material de estudio Semana 7.
 */
interface ActividadRepository {
    fun observarActividades(): Flow<List<ActividadFormativa>>
    suspend fun guardar(actividad: ActividadFormativa)
    suspend fun eliminar(actividad: ActividadFormativa)
    suspend fun completar(actividad: ActividadFormativa)
    suspend fun obtenerPorId(id: Long): ActividadFormativa?
}

/**
 * Implementación real que usa Room.
 */
class RoomActividadRepository(private val dao: ActividadDao) : ActividadRepository {
    override fun observarActividades() = dao.obtenerTodas()
    
    override suspend fun guardar(actividad: ActividadFormativa) = dao.insertar(actividad)
    
    override suspend fun eliminar(actividad: ActividadFormativa) = dao.eliminar(actividad)
    
    override suspend fun completar(actividad: ActividadFormativa) {
        dao.actualizar(actividad.copy(progreso = 100))
    }

    override suspend fun obtenerPorId(id: Long) = dao.obtenerPorId(id)
}
