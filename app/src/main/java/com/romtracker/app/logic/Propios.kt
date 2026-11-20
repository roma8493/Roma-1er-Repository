package com.romtracker.app.logic

import com.romtracker.app.data.Registro
import com.romtracker.app.data.TipoPropio
import com.romtracker.app.data.claveRegistroPropio
import java.time.LocalDate

/**
 * Convierte la respuesta Sí/No del día en ✓ / "no fui".
 * [hizoLaAccion]: respondió "Sí" a "¿lo hice?".
 * HACER ("quiero hacerlo"): ✓ = lo hice. DEJAR ("quiero dejarlo"): ✓ = ese día NO lo hice.
 */
fun cumplidoPropio(tipo: String, hizoLaAccion: Boolean): Boolean =
    if (tipo == TipoPropio.DEJAR) !hizoLaAccion else hizoLaAccion

/** Inverso de [cumplidoPropio]: qué respondió el usuario, dado el ✓ guardado. */
fun hizoLaAccionPropio(tipo: String, cumplido: Boolean): Boolean =
    if (tipo == TipoPropio.DEJAR) !cumplido else cumplido

/** Días de un hábito propio como [Registros] (el ✓ ya viene resuelto), para reutilizar fase, consistencia y racha. */
fun registrosDePropio(id: Long, todos: List<Registro>): Registros {
    val clave = claveRegistroPropio(id)
    return todos.filter { it.habito == clave }.associate { LocalDate.ofEpochDay(it.fecha) to it.cumplido }
}
