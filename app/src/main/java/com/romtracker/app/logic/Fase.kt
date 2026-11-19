package com.romtracker.app.logic

import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class Fase { ADQUISICION, CONSOLIDACION, AUTOMATICO }

const val DIA_CONSOLIDACION = 90
const val DIA_AUTOMATICO = 270

/**
 * Estado de fase de un hábito.
 * [dia]: número de día desde el inicio (el día de inicio es el día 1).
 * [diasParaSiguiente]: null si ya es Automático.
 * [metaSiguiente]: día en que empieza la fase siguiente (90 o 270); null si es Automático.
 */
data class EstadoFase(
    val fase: Fase,
    val dia: Int,
    val diasParaSiguiente: Int?,
    val metaSiguiente: Int?,
)

/** Día número N del hábito. Si [hoy] es anterior al inicio, devuelve 0. */
fun diaDelHabito(inicio: LocalDate, hoy: LocalDate): Int {
    val dias = ChronoUnit.DAYS.between(inicio, hoy).toInt() + 1
    return if (dias < 0) 0 else dias
}

/** Los días 90 y 270 ya pertenecen a la fase nueva. */
fun estadoDeFase(dia: Int): EstadoFase = when {
    dia < DIA_CONSOLIDACION ->
        EstadoFase(Fase.ADQUISICION, dia, DIA_CONSOLIDACION - dia, DIA_CONSOLIDACION)
    dia < DIA_AUTOMATICO ->
        EstadoFase(Fase.CONSOLIDACION, dia, DIA_AUTOMATICO - dia, DIA_AUTOMATICO)
    else -> EstadoFase(Fase.AUTOMATICO, dia, null, null)
}
