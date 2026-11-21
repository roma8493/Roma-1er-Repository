package com.romtracker.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Fechas guardadas como epochDay (LocalDate.toEpochDay()), según la fecha local del celular. */
@Entity(tableName = "perfil")
data class Perfil(
    @PrimaryKey val id: Int = 1,
    val nombre: String,
    val horaSalidaMinutos: Int, // minutos desde medianoche
    val claveAncla: String,
    val porQue: String,
    /** Meta diaria de vasos de agua. Rige para los días nuevos; cada día guarda la suya en [Agua.meta]. */
    @ColumnInfo(defaultValue = "8") val metaAgua: Int = 8,
    /** Tema de la app: [TemaPerfil.CLARO] (0, por defecto) u [TemaPerfil.OSCURO] (2). */
    @ColumnInfo(defaultValue = "0") val tema: Int = TemaPerfil.CLARO,
    /** Versión mínima del Gym (R10), p. ej. "solo 20 min". Vacío = sin versión mínima. */
    @ColumnInfo(defaultValue = "''") val versionMinima: String = "",
)

/**
 * Valores de [Perfil.tema]. Solo hay dos temas; cualquier otro valor guardado (el 0 y el 1 de la
 * versión 0.8.1, que tenía "Sistema") se trata como claro. Los nombres se usan en la copia.
 */
object TemaPerfil {
    const val CLARO = 0
    const val OSCURO = 2

    fun esOscuro(tema: Int): Boolean = tema == OSCURO

    fun nombre(tema: Int): String = if (esOscuro(tema)) "OSCURO" else "CLARO"

    /** Nombre desconocido, ausente o "SISTEMA" (copias 0.8.1) = CLARO. */
    fun deNombre(nombre: String?): Int = if (nombre == "OSCURO") OSCURO else CLARO
}

/** [tipo]: "GYM" o "COMIDA". */
@Entity(tableName = "habito")
data class Habito(
    @PrimaryKey val tipo: String,
    val fechaInicio: Long,
)

/** Una fila por hábito y día. [cumplido]: true = fui/sano, false = no fui/chatarra. Sin fila = sin dato. */
@Entity(tableName = "registro", primaryKeys = ["habito", "fecha"])
data class Registro(
    val habito: String,
    val fecha: Long,
    val cumplido: Boolean,
)

/** Checklist preparada la noche anterior; [paraFecha] es el día para el que se prepara. */
@Entity(tableName = "checklist")
data class Checklist(
    @PrimaryKey val paraFecha: Long,
    val bolso: Boolean = false,
    val comida: Boolean = false,
    val hora: Boolean = false,
)

/** Un ítem de la checklist nocturna (editable: máximo 6, mínimo 1). [orden] define cómo se muestran. */
@Entity(tableName = "checklist_item")
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val texto: String,
    val orden: Int,
)

/** Marca de un ítem para un día: [paraFecha] es el día para el que se prepara. Sin fila = no hecho. */
@Entity(tableName = "checklist_marca", primaryKeys = ["paraFecha", "itemId"])
data class ChecklistMarca(
    val paraFecha: Long,
    val itemId: Long,
    val hecho: Boolean,
)

/**
 * Vasos de agua de un día y la meta que regía ESE día (así cambiar la meta no reinterpreta el pasado).
 * Día ✓ = vasos >= meta; menos = "no fui"; sin fila = "sin dato".
 */
@Entity(tableName = "agua")
data class Agua(
    @PrimaryKey val fecha: Long,
    val vasos: Int,
    val meta: Int,
)

/** Ánimo de un día: 1 (muy mal) a 5 (muy bien). Sin fila = "sin dato". No es un hábito: no tiene fase ni racha. */
@Entity(tableName = "animo")
data class Animo(
    @PrimaryKey val fecha: Long,
    val valor: Int,
)

/**
 * Hábito creado por el usuario (Sí/No por día). [tipo]: [TipoPropio.HACER] ("quiero hacerlo") o
 * [TipoPropio.DEJAR] ("quiero dejarlo"). Sus días se guardan en la tabla `registro` con la clave
 * [claveRegistroPropio]; allí `cumplido` ya es el ✓ (en DEJAR, ✓ = ese día no lo hice).
 */
@Entity(tableName = "habito_propio")
data class HabitoPropio(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val claveAncla: String,
    val porQue: String,
    val tipo: String,
    val fechaInicio: Long,
    /** Versión mínima (R10), p. ej. "solo 5 min". Vacío = sin versión mínima. */
    @ColumnInfo(defaultValue = "''") val versionMinima: String = "",
)

/**
 * Check de automatismo (R6) de un hábito HACER: 4 respuestas de 1 ("nada automático") a 5 ("totalmente automático").
 * [habito] usa la misma clave que `registro.habito` (GYM, COMIDA, AGUA o PROPIO_<id>). Un check por hábito y día.
 */
@Entity(tableName = "automatismo", primaryKeys = ["habito", "fecha"])
data class Automatismo(
    val habito: String,
    val fecha: Long,
    val r1: Int,
    val r2: Int,
    val r3: Int,
    val r4: Int,
)

/**
 * Revisión semanal (R9). [semana] = epochDay del lunes de esa semana (una revisión por semana).
 * [barrera]: qué la frenó; [cambio]: qué puede cambiar en su entorno. Ambos opcionales (pueden quedar vacíos).
 */
@Entity(tableName = "revision")
data class Revision(
    @PrimaryKey val semana: Long,
    val barrera: String = "",
    val cambio: String = "",
)

object TipoPropio {
    const val HACER = "HACER"
    const val DEJAR = "DEJAR"
    fun valido(tipo: String) = tipo == HACER || tipo == DEJAR
}

const val PREFIJO_PROPIO = "PROPIO_"

/** Valor de `registro.habito` para los días de un hábito propio. */
fun claveRegistroPropio(id: Long) = "$PREFIJO_PROPIO$id"

object TipoHabito {
    const val GYM = "GYM"
    const val COMIDA = "COMIDA"
    const val AGUA = "AGUA"
}
