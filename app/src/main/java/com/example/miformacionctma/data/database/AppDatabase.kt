package com.example.miformacionctma.data.database

import android.content.Context
import androidx.room.*
import com.example.miformacionctma.data.dao.ActividadDao
import com.example.miformacionctma.data.entity.ActividadEntity

@Database(entities = [ActividadEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun actividadDao(): ActividadDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "actividad_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
