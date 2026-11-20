package com.romtracker.app.logic

import java.time.LocalDate

/** Las 5 caritas, de 1 (muy mal) a 5 (muy bien). */
val CARITAS = listOf("😞", "🙁", "😐", "🙂", "😄")
val NOMBRES_ANIMO = listOf("Muy mal", "Mal", "Más o menos", "Bien", "Muy bien")

/** Carita de un valor 1..5; null si no es válido. */
fun caritaDe(valor: Int?): String? = valor?.let { CARITAS.getOrNull(it - 1) }

/** El ánimo no es un hábito (no tiene inicio): se edita hoy y los 7 días anteriores. */
fun animoEditable(fecha: LocalDate, hoy: LocalDate): Boolean =
    esEditable(fecha, hoy.minusDays(DIAS_EDITABLES_ATRAS.toLong()), hoy)
