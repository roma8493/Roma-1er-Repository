package com.romtracker.app.logic

import com.romtracker.app.data.Automatismo
import com.romtracker.app.data.Habito
import com.romtracker.app.data.HabitoPropio
import com.romtracker.app.data.Registro
import com.romtracker.app.data.TipoPropio
import com.romtracker.app.data.claveRegistroPropio

/** Un hábito que hasta 1.1.1 era fijo (Gym o Comida) y desde 1.2.0 es un hábito propio más. */
data class FijoViejo(val clave: String, val nombre: String, val icono: String)

/** Hábitos fijos de las versiones anteriores a 1.2.0. Solo se usan para convertir datos viejos. */
object FijosViejos {
    const val GYM = "GYM"
    const val COMIDA = "COMIDA"
    val TODOS = listOf(FijoViejo(GYM, "Gym", "💪"), FijoViejo(COMIDA, "Comida", "🥗"))

    /** Comida nunca tuvo ancla ni "por qué": se le dan unos neutros, editables después. */
    const val ANCLA_COMIDA = "mi comida del día"
    const val PORQUE_COMIDA = "Comer sano me hace bien"
}

/** Datos de una copia que la conversión puede cambiar. */
data class DatosConvertibles(
    val habitos: List<Habito>,
    val propios: List<HabitoPropio>,
    val registros: List<Registro>,
    val automatismos: List<Automatismo>,
)

/**
 * Convierte Gym y Comida en hábitos propios HACER. Gym toma ancla, "por qué" y versión mínima del perfil.
 * Se convierte un fijo si tiene fila en `habito` o algún registro/check; su inicio es el de la fila
 * (o su primer registro). Los ids nuevos siguen al mayor id propio existente.
 * Registros y checks pasan a la clave `PROPIO_<id>`; las filas GYM/COMIDA de `habito` se quitan.
 */
fun convertirFijos(
    datos: DatosConvertibles, claveAnclaPerfil: String, porQuePerfil: String, versionMinimaPerfil: String,
): DatosConvertibles {
    var siguienteId = (datos.propios.maxOfOrNull { it.id } ?: 0L) + 1
    var propios = datos.propios
    var registros = datos.registros
    var automatismos = datos.automatismos
    for (fijo in FijosViejos.TODOS) {
        val fila = datos.habitos.firstOrNull { it.tipo == fijo.clave }
        val fechas = registros.filter { it.habito == fijo.clave }.map { it.fecha } +
            automatismos.filter { it.habito == fijo.clave }.map { it.fecha }
        if (fila == null && fechas.isEmpty()) continue
        val inicio = fila?.fechaInicio ?: fechas.min()
        val id = siguienteId++
        val esGym = fijo.clave == FijosViejos.GYM
        propios = propios + HabitoPropio(
            id, fijo.nombre,
            if (esGym) claveAnclaPerfil else FijosViejos.ANCLA_COMIDA,
            if (esGym) porQuePerfil else FijosViejos.PORQUE_COMIDA,
            TipoPropio.HACER, inicio,
            if (esGym) versionMinimaPerfil else "",
            fijo.icono,
        )
        val nueva = claveRegistroPropio(id)
        registros = registros.map { if (it.habito == fijo.clave) it.copy(habito = nueva) else it }
        automatismos = automatismos.map { if (it.habito == fijo.clave) it.copy(habito = nueva) else it }
    }
    val claves = FijosViejos.TODOS.map { it.clave }.toSet()
    return DatosConvertibles(datos.habitos.filter { it.tipo !in claves }, propios, registros, automatismos)
}
