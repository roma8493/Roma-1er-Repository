package com.romtracker.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Perfil::class, Habito::class, Registro::class, Checklist::class, Agua::class, Animo::class, HabitoPropio::class],
    version = 5,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun perfilDao(): PerfilDao
    abstract fun habitoDao(): HabitoDao
    abstract fun registroDao(): RegistroDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun aguaDao(): AguaDao
    abstract fun animoDao(): AnimoDao
    abstract fun habitoPropioDao(): HabitoPropioDao

    /** Lee todo para la copia de seguridad. Devuelve null si todavía no hay perfil. */
    suspend fun exportar(): Copia? = withTransaction {
        val perfil = perfilDao().obtener() ?: return@withTransaction null
        Copia(perfil, habitoDao().todos(), registroDao().todosLosRegistros(), checklistDao().todas(), aguaDao().todas(), animoDao().todos(), habitoPropioDao().todos())
    }

    /** Guarda los vasos de un día y crea el hábito Agua si es el primer registro (inicio = esa fecha). */
    suspend fun registrarAgua(fecha: Long, vasos: Int, meta: Int) = withTransaction {
        habitoDao().crear(Habito(TipoHabito.AGUA, fecha))
        aguaDao().guardar(Agua(fecha, vasos, meta))
    }

    /** Crea un hábito propio; su inicio es [hoy]. Devuelve su id. */
    suspend fun crearPropio(nombre: String, claveAncla: String, porQue: String, tipo: String, hoy: Long): Long =
        habitoPropioDao().crear(HabitoPropio(0, nombre.trim(), claveAncla.trim(), porQue.trim(), tipo, hoy))

    /** Borra el hábito propio y todo su historial, en una sola transacción. */
    suspend fun borrarPropio(id: Long) = withTransaction {
        registroDao().borrarDeHabito(claveRegistroPropio(id))
        habitoPropioDao().borrar(id)
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
        animoDao().borrarTodo()
        habitoPropioDao().borrarTodo()
        perfilDao().guardar(copia.perfil)
        habitoDao().insertar(copia.habitos)
        registroDao().insertar(copia.registros)
        checklistDao().insertar(copia.checklists)
        aguaDao().insertar(copia.aguas)
        animoDao().insertar(copia.animos)
        habitoPropioDao().insertar(copia.habitosPropios)
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

        /** 0.2.3 → 0.2.4: solo agrega la tabla animo; no toca ninguna tabla con datos existentes. */
        val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `animo` (`fecha` INTEGER NOT NULL, " +
                        "`valor` INTEGER NOT NULL, PRIMARY KEY(`fecha`))"
                )
            }
        }

        /** 0.3 → 0.4: solo agrega la tabla habito_propio; no toca ninguna tabla con datos existentes. */
        val MIGRACION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `habito_propio` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`nombre` TEXT NOT NULL, `claveAncla` TEXT NOT NULL, `porQue` TEXT NOT NULL, " +
                        "`tipo` TEXT NOT NULL, `fechaInicio` INTEGER NOT NULL)"
                )
            }
        }

        fun obtener(contexto: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext, AppDatabase::class.java, "romtracker.db"
                ).addMigrations(MIGRACION_1_2, MIGRACION_2_3, MIGRACION_3_4, MIGRACION_4_5).build().also { instancia = it }
            }
    }
}
