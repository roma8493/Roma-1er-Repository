package com.romtracker.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction

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

    /** Lee todo para la copia de seguridad. Devuelve null si todavía no hay perfil. */
    suspend fun exportar(): Copia? = withTransaction {
        val perfil = perfilDao().obtener() ?: return@withTransaction null
        Copia(perfil, habitoDao().todos(), registroDao().todosLosRegistros(), checklistDao().todas())
    }

    /** Reemplaza TODO lo guardado por la copia, en una sola transacción: o entra todo o no cambia nada. */
    suspend fun reemplazarTodo(copia: Copia) = withTransaction {
        perfilDao().borrarTodo()
        habitoDao().borrarTodo()
        registroDao().borrarTodo()
        checklistDao().borrarTodo()
        perfilDao().guardar(copia.perfil)
        habitoDao().insertar(copia.habitos)
        registroDao().insertar(copia.registros)
        checklistDao().insertar(copia.checklists)
    }

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
