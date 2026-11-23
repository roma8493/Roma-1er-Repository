package com.romtracker.app.logic

import java.time.LocalDate

/** R13: ¿el día de ayer quedó marcado como "no" (✓ resuelto = false)? Sin dato o anterior al inicio no cuenta. */
fun falloAyer(inicio: LocalDate, hoy: LocalDate, registros: Registros): Boolean {
    val ayer = hoy.minusDays(1)
    return !ayer.isBefore(inicio) && registros[ayer] == false
}

/**
 * R13 "Nunca falles dos veces": frase para la tarjeta de Hoy, o null si no corresponde.
 * [hoy]: ✓ ya resuelto de hoy (null = sin registro). Tono de invitación, sin culpa (R8).
 */
fun textoNuncaDosVeces(falloAyer: Boolean, hoy: Boolean?): String? = when {
    hoy == false -> "Un día no define nada. Mañana es lo que importa."
    hoy == null && falloAyer -> "Ayer no se dio. Hoy es el día que importa."
    else -> null
}

/** Rachas que merecen una celebración especial (R14). */
val RACHAS_CELEBRADAS = listOf(3, 7, 14, 30, 60, 100, 200, 365)

/**
 * R14 recompensa inmediata: mensaje breve al marcar ✓.
 * [racha]: racha actual ya incluyendo hoy. [dejar]: hábito que se quiere dejar.
 */
fun mensajeCelebracion(racha: Int, dejar: Boolean): String = when {
    racha in RACHAS_CELEBRADAS -> "¡$racha días seguidos! Eso se nota 🎉"
    dejar -> "Un día más sin hacerlo. ¡Bien!"
    else -> "¡Qué bueno que lo hiciste!"
}

/** Días, desde el cambio de fase, en que se muestra el aviso de R15 (sin guardar nada en el celular). */
const val DIAS_AVISO_FASE = 7

/** R15: fase recién alcanzada (día 90 → Consolidación, día 270 → Automático) durante [DIAS_AVISO_FASE] días; si no, null. */
fun cruzoFase(dia: Int): Fase? = when (dia) {
    in DIA_CONSOLIDACION until DIA_CONSOLIDACION + DIAS_AVISO_FASE -> Fase.CONSOLIDACION
    in DIA_AUTOMATICO until DIA_AUTOMATICO + DIAS_AVISO_FASE -> Fase.AUTOMATICO
    else -> null
}

/** Texto del aviso de cambio de fase (R15), en tono de invitación. */
fun textoAvisoFase(fase: Fase): String? = when (fase) {
    Fase.CONSOLIDACION -> "Llegaste a la fase Consolidación 🌿 Lo que venís repitiendo ya está echando raíces."
    Fase.AUTOMATICO -> "Llegaste a la fase Automático 🎉 Tu cerebro ya lo hace casi solo."
    Fase.ADQUISICION -> null
}
