package com.romtracker.app.logic

import com.romtracker.app.data.Revision

/** epochDay del lunes de la semana que contiene a [fecha] (epochDay). El epochDay 0 fue jueves, 3 días después del lunes. */
fun lunesDe(fecha: Long): Long = fecha - Math.floorMod(fecha + 3, 7L)

/**
 * ¿Toca la revisión semanal (R9)? Toca cuando empezó una semana nueva (lunes) desde la última revisión o, si nunca
 * hizo una, desde la semana en que empezó su primer hábito ([inicio]). Una persona que empieza hoy no recibe la
 * invitación hasta el lunes siguiente. Todo en epochDay.
 */
fun tocaRevision(inicio: Long, ultimaSemana: Long?, hoy: Long): Boolean =
    lunesDe(hoy) > (ultimaSemana ?: lunesDe(inicio))

/** Revisiones de la más nueva a la más vieja (para la lista de revisiones anteriores). */
fun revisionesRecientes(revisiones: List<Revision>): List<Revision> = revisiones.sortedByDescending { it.semana }
