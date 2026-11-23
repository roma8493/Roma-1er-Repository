package com.romtracker.app.logic

import com.romtracker.app.data.Checklist
import com.romtracker.app.data.ChecklistItem
import com.romtracker.app.data.ChecklistMarca

const val MAX_ITEMS_CHECKLIST = 6
const val MIN_ITEMS_CHECKLIST = 1
const val LARGO_MAX_ITEM = 60

/** Los 3 ítems de siempre: (id, texto, orden). Sus ids son fijos porque la migración 5→6 los usa para copiar las marcas viejas. */
val ITEMS_POR_DEFECTO = listOf(
    Triple(1L, "Bolso del gym listo", 1),
    Triple(2L, "Comida preparada", 2),
    Triple(3L, "Decidí a qué hora voy", 3),
)

/** Ítems neutros para instalaciones nuevas (no hablan de gym ni de comida). */
val ITEMS_NEUTROS = listOf(
    Triple(1L, "Dejé lista la ropa de mañana", 1),
    Triple(2L, "Dejé preparado lo que voy a necesitar", 2),
    Triple(3L, "Decidí a qué hora empiezo", 3),
)

fun itemsNeutros(): List<ChecklistItem> = ITEMS_NEUTROS.map { (id, texto, orden) -> ChecklistItem(id, texto, orden) }

fun puedeAgregarItem(cantidadActual: Int) = cantidadActual < MAX_ITEMS_CHECKLIST

fun puedeBorrarItem(cantidadActual: Int) = cantidadActual > MIN_ITEMS_CHECKLIST

/** Sin espacios sobrantes y con tope de largo; vacío = no válido. */
fun limpiarTextoItem(texto: String): String = texto.trim().take(LARGO_MAX_ITEM)

fun itemsPorDefecto(): List<ChecklistItem> = ITEMS_POR_DEFECTO.map { (id, texto, orden) -> ChecklistItem(id, texto, orden) }

/** Marcas equivalentes a las 3 casillas fijas de la tabla vieja (para leer copias anteriores a la versión 6). */
fun marcasDeChecklistVieja(c: Checklist): List<ChecklistMarca> = listOf(
    ChecklistMarca(c.paraFecha, 1, c.bolso),
    ChecklistMarca(c.paraFecha, 2, c.comida),
    ChecklistMarca(c.paraFecha, 3, c.hora),
)

/** ¿Está hecho el ítem ese día? Sin marca = no hecho. */
fun estaHecho(marcas: List<ChecklistMarca>, itemId: Long): Boolean =
    marcas.firstOrNull { it.itemId == itemId }?.hecho ?: false
