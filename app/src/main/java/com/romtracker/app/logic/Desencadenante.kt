package com.romtracker.app.logic

import com.romtracker.app.data.Desencadenante

/** Opciones tocables de cada fila del diálogo de desencadenantes (R3). El orden desempata la "más frecuente". */
object OpcionesDesencadenante {
    val LUGAR = listOf("Casa", "Trabajo", "Calle", "Otro")
    val MOMENTO = listOf("Mañana", "Tarde", "Noche")
    val EMOCION = listOf("Estrés", "Aburrimiento", "Cansancio", "Tristeza", "Alegría", "Otra")
    val COMPANIA = listOf("Solo", "Con gente")
}

/** Con 3 o más deslices anotados se muestra el patrón. */
const val UMBRAL_PATRON = 3

/** Cuántos deslices anotados faltan para mostrar el patrón (0 si ya alcanza). */
fun faltanParaPatron(anotados: Int): Int = maxOf(0, UMBRAL_PATRON - anotados)

/**
 * Opción más repetida de una fila. Ignora los null y los valores que no están en [opciones];
 * si hay empate gana la que aparece primero en [opciones]. Devuelve null si no hay ninguna.
 */
fun opcionMasFrecuente(valores: List<String?>, opciones: List<String>): String? {
    val cuenta = opciones.associateWith { o -> valores.count { it == o } }
    val maximo = cuenta.values.maxOrNull() ?: return null
    if (maximo == 0) return null
    return opciones.first { cuenta[it] == maximo }
}

/** Desencadenantes anotados de un hábito propio. */
fun desencadenantesDe(todos: List<Desencadenante>, clave: String): List<Desencadenante> =
    todos.filter { it.habito == clave }

/**
 * Sugerencia fija de cambio de entorno (R3), redactada como invitación (R8). Se guía por la emoción más
 * frecuente; si no hay emoción, por el lugar; si no hay ninguno, null.
 */
fun sugerenciaEntorno(emocion: String?, lugar: String?): String? = when (emocion) {
    "Estrés" -> "Podrías probar una pausa corta de respiración antes de decidir, y dejar el objeto fuera del alcance."
    "Aburrimiento" -> "Podrías tener a mano una alternativa para esos momentos y dejar el objeto fuera de la vista."
    "Cansancio" -> "Podrías dejar el objeto fuera del alcance y tener a mano una alternativa."
    "Tristeza" -> "Podrías tener a mano algo que te haga bien o a alguien con quien hablar, y dejar el objeto lejos."
    "Alegría" -> "Podrías pensar de antemano qué otra forma de celebrar te gusta, y dejar el objeto fuera del alcance."
    "Otra" -> "Podrías dejar el objeto fuera del alcance y tener a mano una alternativa."
    else -> when (lugar) {
        "Casa" -> "Podrías sacar el objeto de los lugares de la casa donde más lo usás."
        "Trabajo" -> "Podrías no llevar el objeto al trabajo y tener a mano una alternativa."
        "Calle" -> "Podrías salir sin el objeto y planear qué hacer si aparece el antojo en la calle."
        "Otro" -> "Podrías pensar de antemano qué hacer cuando estés en ese lugar."
        else -> null
    }
}
