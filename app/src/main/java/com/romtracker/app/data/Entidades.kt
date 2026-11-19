package com.romtracker.app.data

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

object TipoHabito {
    const val GYM = "GYM"
    const val COMIDA = "COMIDA"
    const val AGUA = "AGUA"
}
