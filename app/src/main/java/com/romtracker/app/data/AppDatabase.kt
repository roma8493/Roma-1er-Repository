package com.romtracker.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Perfil::class, Habito::class, Registro::class, Checklist::class, Agua::class],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun perfilDao(): PerfilDao
    abstract fun habitoDao(): HabitoDao
    abstract fun registroDao(): RegistroDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun aguaDao(): AguaDao

    /** Lee todo para la copia de seguridad. Devuelve null si todavía no hay perfil. */
    suspend fun exportar(): Copia? = withTransaction {
        val perfil = perfilDao().obtener() ?: return@withTransaction null
        Copia(perfil, habitoDao().todos(), registroDao().todosLosRegistros(), checklistDao().todas(), aguaDao().todas())
    }

    /** Guarda los vasos de un día y crea el hábito Agua si es el primer registro (inicio = esa fecha). */
    suspend fun registrarAgua(fecha: Long, vasos: Int, meta: Int) = withTransaction {
        habitoDao().crear(Habito(TipoHabito.AGUA, fecha))
        aguaDao().guardar(Agua(fecha, vasos, meta))
    }

    /**
     * Guarda el perfil. Si cambió la meta de agua, la nueva rige desde hoy: se actualiza la meta
     * del día de hoy (si ya tiene vasos) y los días anteriores conservan la suya.
     */
    suspend fun guardarPerfil(perfil: Perfil, hoy: Long) = withTransaction {
        val anterior = perfilDao().obtener()
        perfilDao().guardar(perfil)
        if (anterior != null && anterior.metaAgua != perfil.metaAgua) {
            aguaDao().todas().firstOrNull { it.fecha == hoy }?.let {
                aguaDao().guardar(it.copy(meta = perfil.metaAgua))
            }
        }
    }

    /** Reemplaza TODO lo guardado por la copia, en una sola transacción: o entra todo o no cambia nada. */
    suspend fun reemplazarTodo(copia: Copia) = withTransaction {
        perfilDao().borrarTodo()
        habitoDao().borrarTodo()
        registroDao().borrarTodo()
        checklistDao().borrarTodo()
        aguaDao().borrarTodo()
        perfilDao().guardar(copia.perfil)
        habitoDao().insertar(copia.habitos)
        registroDao().insertar(copia.registros)
        checklistDao().insertar(copia.checklists)
        aguaDao().insertar(copia.aguas)
    }

    companion object {
        @Volatile private var instancia: AppDatabase? = null

        /** 0.2 → 0.3: solo agrega la tabla agua; no toca ninguna tabla con datos existentes. */
        val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `agua` (`fecha` INTEGER NOT NULL, " +
                        "`vasos` INTEGER NOT NULL, `meta` INTEGER NOT NULL, PRIMARY KEY(`fecha`))"
                )
            }
        }

        /** 0.2.2 → 0.2.3: agrega la meta de agua al perfil (8 por defecto); no toca los datos existentes. */
        val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `perfil` ADD COLUMN `metaAgua` INTEGER NOT NULL DEFAULT 8")
            }
        }

        fun obtener(contexto: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext, AppDatabase::class.java, "romtracker.db"
                ).addMigrations(MIGRACION_1_2, MIGRACION_2_3).build().also { instancia = it }
            }
    }
}
