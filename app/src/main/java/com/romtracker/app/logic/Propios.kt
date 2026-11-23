package com.romtracker.app.logic

import com.romtracker.app.data.HabitoPropio
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

/** Emoji por defecto de un hábito que quiero hacer. */
const val ICONO_HACER = "🌱"

/** Emoji por defecto de un hábito que quiero dejar. */
const val ICONO_DEJAR = "🚫"

/** Emoji del hábito: el elegido, o el de por defecto según el tipo si está vacío. */
fun iconoDe(propio: HabitoPropio): String =
    propio.icono.trim().ifEmpty { if (propio.tipo == TipoPropio.DEJAR) ICONO_DEJAR else ICONO_HACER }

/** Emojis sugeridos en el selector (el usuario también puede escribir otro). */
val EMOJIS_SUGERIDOS: List<String> = listOf(
    "💪", "🥗", "💧", "📚", "🚭", "😴", "🏃", "🧘", "🚶", "🚴", "🏊", "🍎",
    "🥦", "🍵", "☕", "🍬", "🍺", "📱", "🎮", "🛌", "🧠", "✍️", "🎸", "🧹",
    "🦷", "🙏", "🌅", "💊", "🌱", "🚫",
)

/** Deja solo el primer símbolo del texto escrito (un emoji puede ocupar varios caracteres). Vacío si no hay nada. */
fun primerEmoji(texto: String): String {
    val t = texto.trim()
    if (t.isEmpty()) return ""
    val it = java.text.BreakIterator.getCharacterInstance()
    it.setText(t)
    return t.substring(0, it.next())
}

/** R16 (apilar hábitos): texto de la clave ancla cuando el hábito se apila sobre otro. Se lee "Después de mi hábito «X»". */
fun anclaDeHabito(nombre: String): String = "mi hábito «${nombre.trim()}»"

/** Nombres de los hábitos propios que se ofrecen como ancla (sin el hábito [excluirId], si se está editando uno). */
fun nombresParaAncla(propios: List<HabitoPropio>, excluirId: Long? = null): List<String> =
    propios.filter { it.id != excluirId && it.nombre.isNotBlank() }.map { it.nombre.trim() }
