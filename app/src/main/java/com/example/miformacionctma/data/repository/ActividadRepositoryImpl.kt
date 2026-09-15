package com.example.miformacionctma.data.repository

import com.example.miformacionctma.data.model.ActividadDto
import com.example.miformacionctma.data.model.EvidenciaDto
import com.example.miformacionctma.data.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ActividadRepositoryImpl {

    private val client = SupabaseClient.client

    // Obtener actividades desde la tabla de Supabase
    suspend fun getActividades(): List<ActividadDto> = withContext(Dispatchers.IO) {
        client.postgrest["actividades"]
            .select()
            .decodeList<ActividadDto>()
    }

    // Insertar actividad en Supabase
    suspend fun insertActividad(actividad: ActividadDto): ActividadDto = withContext(Dispatchers.IO) {
        client.postgrest["actividades"]
            .insert(actividad) {
                select()
            }
            .decodeSingle<ActividadDto>()
    }

    // Eliminar actividad en Supabase por ID
    suspend fun deleteActividad(id: Long) = withContext(Dispatchers.IO) {
        client.postgrest["actividades"]
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }

    // Insertar evidencia registrada
    suspend fun insertEvidencia(evidencia: EvidenciaDto) = withContext(Dispatchers.IO) {
        client.postgrest["evidencias"]
            .insert(evidencia)
    }

    // Subir foto al bucket 'evidencias' en Supabase Storage
    suspend fun uploadImagenEvidencia(
        bucketName: String = "evidencias",
        fileName: String,
        fileBytes: ByteArray
    ): String = withContext(Dispatchers.IO) {
        val bucket = client.storage[bucketName]
        bucket.upload(fileName, fileBytes) {
            upsert = true
        }
        bucket.publicUrl(fileName)
    }
}