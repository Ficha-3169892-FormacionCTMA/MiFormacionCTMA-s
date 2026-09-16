package com.example.miformacionctma.domain

import kotlinx.coroutines.flow.StateFlow

interface ReporteRepository {
    val reportes: StateFlow<List<Reporte>>
    fun agregar(reporte: Reporte)
}
