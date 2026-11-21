package com.romtracker.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.romtracker.app.logic.ITEMS_POR_DEFECTO
import com.romtracker.app.logic.MAX_ITEMS_CHECKLIST
import com.romtracker.app.logic.limpiarTextoItem
import com.romtracker.app.logic.puedeAgregarItem
import com.romtracker.app.logic.puedeBorrarItem

@Database(
    entities = [Perfil::class, Habito::class, Registro::class, Checklist::class, Agua::class, Animo::class, HabitoPropio::class,
        ChecklistItem::class, ChecklistMarca::class, Automatismo::class, Revision::class],
    version = 10,
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
    abstract fun checklistItemDao(): ChecklistItemDao
    abstract fun checklistMarcaDao(): ChecklistMarcaDao
    abstract fun automatismoDao(): AutomatismoDao
    abstract fun revisionDao(): RevisionDao

    /** Lee todo para la copia de seguridad. Devuelve null si todavía no hay perfil. */
    suspend fun exportar(): Copia? = withTransaction {
        val perfil = perfilDao().obtener() ?: return@withTransaction null
        Copia(perfil, habitoDao().todos(), registroDao().todosLosRegistros(), checklistDao().todas(), aguaDao().todas(), animoDao().todos(), habitoPropioDao().todos(),
            checklistItemDao().todos(), checklistMarcaDao().todas(), automatismoDao().todos(), revisionDao().todas())
    }

    /** Guarda los vasos de un día y crea el hábito Agua si es el primer registro (inicio = esa fecha). */
    suspend fun registrarAgua(fecha: Long, vasos: Int, meta: Int) = withTransaction {
        habitoDao().crear(Habito(TipoHabito.AGUA, fecha))
        aguaDao().guardar(Agua(fecha, vasos, meta))
    }

    /** Crea un hábito propio; su inicio es [hoy]. Devuelve su id. */
    suspend fun crearPropio(
        nombre: String, claveAncla: String, porQue: String, tipo: String, hoy: Long, versionMinima: String = "",
    ): Long =
        habitoPropioDao().crear(HabitoPropio(0, nombre.trim(), claveAncla.trim(), porQue.trim(), tipo, hoy, versionMinima.trim()))

    /** Borra el hábito propio y todo su historial, en una sola transacción. */
    suspend fun borrarPropio(id: Long) = withTransaction {
        registroDao().borrarDeHabito(claveRegistroPropio(id))
        automatismoDao().borrarDeHabito(claveRegistroPropio(id))
        habitoPropioDao().borrar(id)
    }

    /** Agrega un ítem al final de la checklist. Devuelve false si ya hay [MAX_ITEMS_CHECKLIST] o el texto está vacío. */
    suspend fun agregarItemChecklist(texto: String): Boolean = withTransaction {
        val items = checklistItemDao().todos()
        val limpio = limpiarTextoItem(texto)
        if (!puedeAgregarItem(items.size) || limpio.isEmpty()) return@withTransaction false
        checklistItemDao().crear(ChecklistItem(0, limpio, (items.maxOfOrNull { it.orden } ?: 0) + 1))
        true
    }

    /** Cambia el texto de un ítem. Devuelve false si el texto queda vacío. */
    suspend fun renombrarItemChecklist(id: Long, texto: String): Boolean {
        val limpio = limpiarTextoItem(texto)
        if (limpio.isEmpty()) return false
        checklistItemDao().renombrar(id, limpio)
        return true
    }

    /** Borra un ítem y sus marcas. Devuelve false si es el último (siempre queda al menos uno). */
    suspend fun borrarItemChecklist(id: Long): Boolean = withTransaction {
        if (!puedeBorrarItem(checklistItemDao().todos().size)) return@withTransaction false
        checklistMarcaDao().borrarDeItem(id)
        checklistItemDao().borrar(id)
        true
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

    /**
     * Reemplaza TODO lo guardado por la copia, en una sola transacción: o entra todo o no cambia nada.
     * El tema es una preferencia de este celular: se conserva el que había, no el de la copia.
     * Devuelve el perfil que quedó guardado.
     */
    suspend fun reemplazarTodo(copia: Copia): Perfil = withTransaction {
        val temaActual = perfilDao().obtener()?.tema ?: TemaPerfil.CLARO
        val perfil = copia.perfil.copy(tema = temaActual)
        perfilDao().borrarTodo()
        habitoDao().borrarTodo()
        registroDao().borrarTodo()
        checklistDao().borrarTodo()
        aguaDao().borrarTodo()
        animoDao().borrarTodo()
        habitoPropioDao().borrarTodo()
        checklistItemDao().borrarTodo()
        checklistMarcaDao().borrarTodo()
        automatismoDao().borrarTodo()
        revisionDao().borrarTodo()
        perfilDao().guardar(perfil)
        habitoDao().insertar(copia.habitos)
        registroDao().insertar(copia.registros)
        checklistDao().insertar(copia.checklists)
        aguaDao().insertar(copia.aguas)
        animoDao().insertar(copia.animos)
        habitoPropioDao().insertar(copia.habitosPropios)
        checklistItemDao().insertar(copia.checklistItems)
        checklistMarcaDao().insertar(copia.checklistMarcas)
        automatismoDao().insertar(copia.automatismos)
        revisionDao().insertar(copia.revisiones)
        perfil
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

        /**
         * 0.6 → 0.7: crea la checklist editable, siembra los 3 ítems de siempre (ids 1, 2, 3) y copia las marcas
         * de la tabla `checklist` vieja, que NO se toca. Los ítems quedan con id fijo para que las marcas
         * copiadas apunten a ellos.
         */
        /** 0.8.1: tema de la app en el perfil (0 = sistema). Solo agrega una columna. */
    val MIGRACION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `perfil` ADD COLUMN `tema` INTEGER NOT NULL DEFAULT 0")
        }
    }

    /** 0.8.3: versión mínima (R10) en el perfil (Gym) y en los hábitos propios. Solo agrega columnas con valor vacío. */
    val MIGRACION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `perfil` ADD COLUMN `versionMinima` TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE `habito_propio` ADD COLUMN `versionMinima` TEXT NOT NULL DEFAULT ''")
        }
    }

    /** 0.8.5: solo agrega la tabla automatismo (check R6); no toca ninguna tabla con datos existentes. */
    val MIGRACION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `automatismo` (`habito` TEXT NOT NULL, `fecha` INTEGER NOT NULL, " +
                    "`r1` INTEGER NOT NULL, `r2` INTEGER NOT NULL, `r3` INTEGER NOT NULL, `r4` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`habito`, `fecha`))"
            )
        }
    }

    /** 0.9.1: solo agrega la tabla revision (R9, revisión semanal); no toca ninguna tabla con datos existentes. */
    val MIGRACION_9_10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `revision` (`semana` INTEGER NOT NULL, `barrera` TEXT NOT NULL, " +
                    "`cambio` TEXT NOT NULL, PRIMARY KEY(`semana`))"
            )
        }
    }

    val MIGRACION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `checklist_item` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`texto` TEXT NOT NULL, `orden` INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `checklist_marca` (`paraFecha` INTEGER NOT NULL, " +
                        "`itemId` INTEGER NOT NULL, `hecho` INTEGER NOT NULL, PRIMARY KEY(`paraFecha`, `itemId`))"
                )
                ITEMS_POR_DEFECTO.forEach { (id, texto, orden) ->
                    db.execSQL(
                        "INSERT INTO `checklist_item` (`id`, `texto`, `orden`) VALUES (?, ?, ?)",
                        arrayOf<Any>(id, texto, orden),
                    )
                }
                db.execSQL("INSERT INTO `checklist_marca` SELECT `paraFecha`, 1, `bolso` FROM `checklist`")
                db.execSQL("INSERT INTO `checklist_marca` SELECT `paraFecha`, 2, `comida` FROM `checklist`")
                db.execSQL("INSERT INTO `checklist_marca` SELECT `paraFecha`, 3, `hora` FROM `checklist`")
            }
        }

        fun obtener(contexto: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext, AppDatabase::class.java, "romtracker.db"
                ).addMigrations(MIGRACION_1_2, MIGRACION_2_3, MIGRACION_3_4, MIGRACION_4_5, MIGRACION_5_6, MIGRACION_6_7, MIGRACION_7_8, MIGRACION_8_9, MIGRACION_9_10).build().also { instancia = it }
            }
    }
}
