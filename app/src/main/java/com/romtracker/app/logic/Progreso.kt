package com.romtracker.app.logic

import java.time.LocalDate

/** Registros: fecha -> true (fui / sano) o false (no fui / chatarra). Sin entrada = "sin dato". */
typealias Registros = Map<LocalDate, Boolean>

/** Consistencia "[cumplidos] de [total]". Los días sin dato cuentan en el total pero no como cumplidos. */
data class Consistencia(val cumplidos: Int, val total: Int)

fun consistencia(inicio: LocalDate, hoy: LocalDate, registros: Registros): Consistencia {
    val total = diaDelHabito(inicio, hoy)
    val cumplidos = registros.count { (fecha, ok) -> ok && !fecha.isBefore(inicio) && !fecha.isAfter(hoy) }
    return Consistencia(cumplidos, total)
}

/**
 * Racha actual: días cumplidos seguidos hasta hoy.
 * Si hoy aún no tiene registro, no corta la racha (se cuenta desde ayer).
 * "No fui" y "sin dato" la cortan. Nunca afecta a la fase.
 */
fun rachaActual(inicio: LocalDate, hoy: LocalDate, registros: Registros): Int {
    var dia = if (registros.containsKey(hoy)) hoy else hoy.minusDays(1)
    var racha = 0
    while (!dia.isBefore(inicio) && registros[dia] == true) {
        racha++
        dia = dia.minusDays(1)
    }
    return racha
}
