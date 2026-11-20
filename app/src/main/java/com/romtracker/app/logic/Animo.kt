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

/** Días con ánimo registrado que hacen falta para mostrar el cruce con los hábitos. */
const val DIAS_MINIMOS_CRUCE = 14

/** Diferencia de promedio (en caritas) desde la cual se dice "más alto/más bajo"; menos = "sin diferencia clara". */
const val DIFERENCIA_NOTABLE = 0.5

/** Promedio de ánimo en días ✓ y en días no ✓ de un hábito (solo días con ánimo Y con registro del hábito). */
data class CruceHabito(
    val diasCumplido: Int,
    val promedioCumplido: Double?,
    val diasNoCumplido: Int,
    val promedioNoCumplido: Double?,
)

fun cruceAnimo(animos: Map<LocalDate, Int>, registros: Registros): CruceHabito {
    val cumplidos = animos.filter { (f, _) -> registros[f] == true }.values
    val noCumplidos = animos.filter { (f, _) -> registros[f] == false }.values
    return CruceHabito(
        cumplidos.size, cumplidos.takeIf { it.isNotEmpty() }?.average(),
        noCumplidos.size, noCumplidos.takeIf { it.isNotEmpty() }?.average(),
    )
}

/** Cuántos días con ánimo faltan para llegar al umbral (0 = ya se puede mostrar). */
fun diasFaltanParaCruce(animos: Map<LocalDate, Int>): Int =
    maxOf(0, DIAS_MINIMOS_CRUCE - animos.size)

private fun un(d: Double) = String.format(java.util.Locale("es", "AR"), "%.1f", d)

/** Texto neutro ("se asocia", nunca "causa"); sin juicios sobre los días no ✓. */
fun textoCruce(c: CruceHabito): String {
    val si = c.promedioCumplido
    val no = c.promedioNoCumplido
    if (si == null || no == null) {
        return "Todavía no hay días ✓ y no ✓ con ánimo para comparar."
    }
    val datos = "Ánimo promedio: ${un(si)} en días ✓ (${c.diasCumplido}) y ${un(no)} en días no ✓ (${c.diasNoCumplido})."
    val lectura = when {
        si - no >= DIFERENCIA_NOTABLE -> "En tus datos, los días ✓ se asocian con un ánimo más alto."
        no - si >= DIFERENCIA_NOTABLE -> "En tus datos, los días no ✓ se asocian con un ánimo más alto."
        else -> "En tus datos no se ve una diferencia clara."
    }
    return "$datos $lectura"
}
