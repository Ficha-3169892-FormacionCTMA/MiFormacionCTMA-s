package com.example.miformacionctma.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.miformacionctma.actividadesEjemplo
import com.example.miformacionctma.domain.ActividadFormativa
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "actividades_prefs")

class ActividadDataStore(private val context: Context) {

    private val ACTIVIDADES_KEY = stringPreferencesKey("actividades_list")

    val actividadesFlow: Flow<List<ActividadFormativa>> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[ACTIVIDADES_KEY]
            if (jsonString != null) {
                try {
                    Json.decodeFromString<List<ActividadFormativa>>(jsonString)
                } catch (e: Exception) {
                    actividadesEjemplo
                }
            } else {
                actividadesEjemplo
            }
        }

    suspend fun guardarActividades(actividades: List<ActividadFormativa>) {
        context.dataStore.edit { preferences ->
            preferences[ACTIVIDADES_KEY] = Json.encodeToString(actividades)
        }
    }
}
