package com.romtracker.app.logic

import com.romtracker.app.data.Agua
import java.time.LocalDate

const val META_VASOS_POR_DEFECTO = 8

/** Día ✓ si llegó a la meta de ese día. */
fun cumplidoAgua(vasos: Int, meta: Int): Boolean = vasos >= meta

/** Suma o resta vasos sin bajar de 0. */
fun ajustarVasos(actual: Int, delta: Int): Int = maxOf(0, actual + delta)

/**
 * Convierte los días de agua en [Registros] para reutilizar fase, consistencia y racha.
 * Cada día se evalúa con SU meta; menos vasos = "no fui"; sin fila = "sin dato".
 */
fun registrosDeAgua(aguas: List<Agua>): Registros =
    aguas.associate { LocalDate.ofEpochDay(it.fecha) to cumplidoAgua(it.vasos, it.meta) }

/** Meta vigente: la del último día registrado, o 8 si todavía no hay ninguno. */
fun metaActual(aguas: List<Agua>): Int =
    aguas.maxByOrNull { it.fecha }?.meta ?: META_VASOS_POR_DEFECTO
