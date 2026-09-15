package com.example.miformacionctma.data.repository

import com.example.miformacionctma.data.model.ReporteModel
import com.example.miformacionctma.data.network.SupabaseClient
import com.example.miformacionctma.domain.Reporte
import com.example.miformacionctma.domain.ReporteRepository
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportesRepository : ReporteRepository {

    private val supabase = SupabaseClient.client
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _reportes = MutableStateFlow<List<Reporte>>(emptyList())
    override val reportes: StateFlow<List<Reporte>> = _reportes.asStateFlow()

    suspend fun obtenerReportes(): List<ReporteModel> = withContext(Dispatchers.IO) {
        supabase.postgrest["reportes"]
            .select()
            .decodeList<ReporteModel>()
    }

    suspend fun crearReporte(reporte: ReporteModel) = withContext(Dispatchers.IO) {
        supabase.postgrest["reportes"]
            .insert(reporte)
    }

    override fun agregar(reporte: Reporte) {
        scope.launch {
            try {
                crearReporte(
                    ReporteModel(
                        titulo = reporte.titulo,
                        descripcion = "Reporte generado desde la app", // Valor por defecto
                        categoria = "General"
                    )
                )
                // Actualizar lista local (opcional, dependiendo de si queremos reactividad inmediata)
                val listaActual = _reportes.value.toMutableList()
                listaActual.add(reporte)
                _reportes.value = listaActual
            } catch (e: Exception) {
                // Manejar error (en un repo real esto se propagaría o registraría)
                e.printStackTrace()
            }
        }
    }
}