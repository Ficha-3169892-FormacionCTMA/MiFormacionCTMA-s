package com.example.miformacionctma.data

import com.example.miformacionctma.domain.ActividadFormativa
import kotlinx.coroutines.flow.Flow

enum class SortOrder { FECHA, TITULO, PROGRESO }

interface ActividadDataSource {
    val actividadesFlow: Flow<List<ActividadFormativa>>
    val sortOrderFlow: Flow<SortOrder>
    
    suspend fun guardarActividades(actividades: List<ActividadFormativa>)
    suspend fun updateSortOrder(order: SortOrder)
}
