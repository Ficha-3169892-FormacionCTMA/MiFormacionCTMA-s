package com.example.miformacionctma.data.repository

import com.example.miformacionctma.data.model.ReporteModel
import com.example.miformacionctma.data.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReportesRepository {

    private val supabase = SupabaseClient.client

    suspend fun obtenerReportes(): List<ReporteModel> = withContext(Dispatchers.IO) {
        supabase.postgrest["reportes"]
            .select()
            .decodeList<ReporteModel>()
    }

    suspend fun crearReporte(reporte: ReporteModel) = withContext(Dispatchers.IO) {
        supabase.postgrest["reportes"]
            .insert(reporte)
    }
}