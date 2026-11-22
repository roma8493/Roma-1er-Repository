package com.romtracker.app.logic

import com.romtracker.app.data.Antojo
import com.romtracker.app.data.claveRegistroPropio

/** Antojos resistidos (R11) de un hábito propio DEJAR hoy. Todo en epochDay. */
fun antojosDeHoy(antojos: List<Antojo>, id: Long, hoy: Long): Int {
    val clave = claveRegistroPropio(id)
    return antojos.count { it.habito == clave && it.fecha == hoy }
}

/** Antojos resistidos desde el lunes de la semana de [hoy] hasta hoy (los días futuros no cuentan). */
fun antojosDeLaSemana(antojos: List<Antojo>, id: Long, hoy: Long): Int {
    val clave = claveRegistroPropio(id)
    val lunes = lunesDe(hoy)
    return antojos.count { it.habito == clave && it.fecha in lunes..hoy }
}

/** Antojos resistidos de siempre. */
fun antojosTotales(antojos: List<Antojo>, id: Long): Int {
    val clave = claveRegistroPropio(id)
    return antojos.count { it.habito == clave }
}

/** Resultado de tocar "Resistí un antojo" (+1) o su corrección (−1) sobre un conteo: nunca baja de 0. */
fun conteoTrasCambio(actual: Int, delta: Int): Int = maxOf(0, actual + delta)
