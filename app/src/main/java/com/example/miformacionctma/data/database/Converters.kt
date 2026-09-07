package com.example.miformacionctma.data.database

import androidx.room.TypeConverter
import com.example.miformacionctma.domain.Prioridad

class Converters {
    @TypeConverter
    fun fromPrioridad(prioridad: Prioridad): String {
        return prioridad.name
    }

    @TypeConverter
    fun toPrioridad(value: String): Prioridad {
        return Prioridad.valueOf(value)
    }
}
