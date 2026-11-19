package com.romtracker.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Perfil::class, Habito::class, Registro::class, Checklist::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun perfilDao(): PerfilDao
    abstract fun habitoDao(): HabitoDao
    abstract fun registroDao(): RegistroDao
    abstract fun checklistDao(): ChecklistDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null

        fun obtener(contexto: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext, AppDatabase::class.java, "romtracker.db"
                ).build().also { instancia = it }
            }
    }
}
