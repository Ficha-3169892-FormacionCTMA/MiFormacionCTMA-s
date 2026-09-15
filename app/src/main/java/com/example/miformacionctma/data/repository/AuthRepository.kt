package com.example.miformacionctma.data.repository

import com.example.miformacionctma.data.model.ProfileDto
import com.example.miformacionctma.data.network.SupabaseClient
import com.example.miformacionctma.domain.model.Role
import com.example.miformacionctma.domain.model.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {
    private val client = SupabaseClient.client

    suspend fun signUp(email: String, pass: String, username: String, role: Role) = withContext(Dispatchers.IO) {
        client.auth.signUpWith(Email) {
            this.email = email
            password = pass
            data = buildJsonObject {
                put("username", username)
                put("rol", role.name)
            }
        }
    }

    suspend fun signIn(email: String, pass: String): User = withContext(Dispatchers.IO) {
        client.auth.signInWith(Email) {
            this.email = email
            password = pass
        }
        
        val currentUser = client.auth.currentUserOrNull() ?: throw Exception("Error al iniciar sesión")
        
        // Obtener perfil para el rol
        val profile = client.postgrest["perfiles"]
            .select {
                filter {
                    eq("id", currentUser.id)
                }
            }
            .decodeSingle<ProfileDto>()
            
        User(
            id = currentUser.id,
            username = profile.username ?: currentUser.email ?: "Usuario",
            email = currentUser.email ?: "",
            role = try { Role.valueOf(profile.rol ?: "STUDENT") } catch(e: Exception) { Role.STUDENT }
        )
    }

    suspend fun signOut() = withContext(Dispatchers.IO) {
        client.auth.signOut()
    }

    suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        val session = client.auth.currentSessionOrNull() ?: return@withContext null
        val currentUser = client.auth.currentUserOrNull() ?: return@withContext null
        
        try {
            val profile = client.postgrest["perfiles"]
                .select {
                    filter {
                        eq("id", currentUser.id)
                    }
                }
                .decodeSingle<ProfileDto>()

            User(
                id = currentUser.id,
                username = profile.username ?: currentUser.email ?: "Usuario",
                email = currentUser.email ?: "",
                role = try { Role.valueOf(profile.rol ?: "STUDENT") } catch(e: Exception) { Role.STUDENT }
            )
        } catch (e: Exception) {
            null
        }
    }
}
