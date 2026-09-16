package com.example.miformacionctma.data.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    // URL base de tu proyecto en Supabase (sin /rest/v1/)
    private const val SUPABASE_URL = "https://izxhktpwimnwfgkdnplr.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml6eGhrdHB3aW1ud2Zna2RucGxyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODkzOTY4OTQsImV4cCI6MjEwNDk3Mjg5NH0._R6l_Zo-0pPsBu9cd1kg63am0iIWJ3xXIqpLIo6e5EY"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest) // Base de datos (Tablas actividades y evidencias)
        install(Auth)      // Autenticación de usuarios
        install(Storage)   // Almacenamiento de archivos/fotos
    }
}