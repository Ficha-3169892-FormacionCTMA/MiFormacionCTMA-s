package com.example.miformacionctma.data.repository

import com.example.miformacionctma.data.dao.ActividadDao
import com.example.miformacionctma.data.entity.toDomain
import com.example.miformacionctma.data.entity.toEntity
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.actividadesEjemplo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class ActividadRepository(private val actividadDao: ActividadDao) {

    val todasLasActividades: Flow<List<ActividadFormativa>> = actividadDao.getAllActividades()
        .map { entities -> entities.map { it.toDomain() } }

    suspend fun checkInitialData() {
        if (actividadDao.getCount() == 0) {
            actividadDao.insertAll(actividadesEjemplo.map { it.toEntity().copy(id = 0) })
        }
    }

    suspend fun insertar(actividad: ActividadFormativa) {
        actividadDao.insertActividad(actividad.toEntity().copy(id = 0))
    }

    suspend fun actualizar(actividad: ActividadFormativa) {
        actividadDao.insertActividad(actividad.toEntity())
    }

    suspend fun eliminar(id: Long) {
        actividadDao.deleteById(id)
    }
}
