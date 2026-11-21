package com.romtracker.app.logic

import com.romtracker.app.data.Automatismo

/** Cada cuántos días toca un check de automatismo (R6): desde el inicio del hábito o desde el último check. */
const val DIAS_ENTRE_CHECKS = 28

/** Puntaje de un check: promedio de las 4 respuestas (1 a 5). */
fun puntajeAutomatismo(c: Automatismo): Double = (c.r1 + c.r2 + c.r3 + c.r4) / 4.0

/**
 * ¿Toca el check de un hábito HACER? [inicio] y [hoy] son epochDay; [ultimoCheck] es la fecha (epochDay) del último
 * check hecho, o null si nunca hizo uno. Toca cuando pasaron [DIAS_ENTRE_CHECKS] días desde el último check
 * (o desde el inicio si no hubo ninguno).
 */
fun tocaCheckAutomatismo(inicio: Long, ultimoCheck: Long?, hoy: Long): Boolean =
    hoy - (ultimoCheck ?: inicio) >= DIAS_ENTRE_CHECKS

/**
 * Claves de los hábitos a los que hoy les toca el check. [habitosHacer] = clave del hábito → fecha de inicio, solo de
 * hábitos HACER (el que llama excluye Ánimo y los DEJAR). Sigue el orden de [habitosHacer].
 */
fun habitosQueTocan(habitosHacer: List<Pair<String, Long>>, checks: List<Automatismo>, hoy: Long): List<String> =
    habitosHacer.filter { (clave, inicio) ->
        tocaCheckAutomatismo(inicio, checks.filter { it.habito == clave }.maxOfOrNull { it.fecha }, hoy)
    }.map { it.first }

/** Checks de un hábito, del más viejo al más nuevo (para el gráfico de evolución). */
fun checksDeHabito(clave: String, checks: List<Automatismo>): List<Automatismo> =
    checks.filter { it.habito == clave }.sortedBy { it.fecha }
