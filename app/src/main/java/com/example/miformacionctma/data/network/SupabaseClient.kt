package com.example.miformacionctma.data.network

import com.example.miformacionctma.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Postgrest) // Base de datos (Tablas actividades y evidencias)
        install(Auth)      // Autenticación de usuarios
        install(Storage)   // Almacenamiento de archivos/fotos
    }
}