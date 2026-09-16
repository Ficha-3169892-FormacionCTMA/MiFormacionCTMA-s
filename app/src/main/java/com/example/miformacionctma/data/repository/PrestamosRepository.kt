package com.example.miformacionctma.data.repository

import com.example.miformacionctma.data.model.PrestamoModel
import com.example.miformacionctma.data.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrestamosRepository {

    private val supabase = SupabaseClient.client

    suspend fun obtenerPrestamos(): List<PrestamoModel> = withContext(Dispatchers.IO) {
        supabase.postgrest["prestamos"]
            .select()
            .decodeList<PrestamoModel>()
    }

    suspend fun crearPrestamo(prestamo: PrestamoModel) = withContext(Dispatchers.IO) {
        supabase.postgrest["prestamos"]
            .insert(prestamo)
    }
}