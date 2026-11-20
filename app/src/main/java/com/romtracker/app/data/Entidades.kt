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
)

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
