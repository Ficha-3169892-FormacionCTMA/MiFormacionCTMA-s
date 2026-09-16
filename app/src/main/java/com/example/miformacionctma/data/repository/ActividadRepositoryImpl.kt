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

    // Obtener actividades desde la tabla de Supabase con soporte para evidencias embebidas si existen
    suspend fun getActividades(): List<com.example.miformacionctma.data.model.ActividadDtoWithEvidencias> = withContext(Dispatchers.IO) {
        client.postgrest["actividades"]
            .select(io.github.jan.supabase.postgrest.query.Columns.raw("*, evidencias(*)"))
            .decodeList<com.example.miformacionctma.data.model.ActividadDtoWithEvidencias>()
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

    // Eliminar registro de evidencia de la base de datos
    suspend fun deleteEvidencia(id: Long) = withContext(Dispatchers.IO) {
        client.postgrest["evidencias"]
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }

    suspend fun deleteEvidenciaPorActividad(actividadId: Long) = withContext(Dispatchers.IO) {
        client.postgrest["evidencias"]
            .delete {
                filter {
                    eq("actividad_id", actividadId)
                }
            }
    }

    suspend fun deleteEvidenciaPorUsuario(actividadId: Long, userId: String?) = withContext(Dispatchers.IO) {
        client.postgrest["evidencias"]
            .delete {
                filter {
                    eq("actividad_id", actividadId)
                    if (userId != null) {
                        eq("user_id", userId)
                    } else {
                        exact("user_id", null)
                    }
                }
            }
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

    // Eliminar archivo del Storage de Supabase
    suspend fun deleteImagenEvidencia(
        bucketName: String = "evidencias",
        filePath: String
    ) = withContext(Dispatchers.IO) {
        val bucket = client.storage[bucketName]
        bucket.delete(filePath)
    }
}