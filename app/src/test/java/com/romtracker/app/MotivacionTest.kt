package com.romtracker.app

import com.romtracker.app.logic.Fase
import com.romtracker.app.logic.cruzoFase
import com.romtracker.app.logic.falloAyer
import com.romtracker.app.logic.mensajeCelebracion
import com.romtracker.app.logic.textoAvisoFase
import com.romtracker.app.logic.textoNuncaDosVeces
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class MotivacionTest {
    private val inicio = LocalDate.of(2026, 1, 1)
    private val hoy = LocalDate.of(2026, 1, 10)

    @Test fun falloAyer_siAyerFueNo() {
        assertTrue(falloAyer(inicio, hoy, mapOf(hoy.minusDays(1) to false)))
    }

    @Test fun falloAyer_noSiAyerFueSiOSinDato() {
        assertFalse(falloAyer(inicio, hoy, mapOf(hoy.minusDays(1) to true)))
        assertFalse(falloAyer(inicio, hoy, emptyMap()))
    }

    @Test fun falloAyer_noSiAyerEsAnteriorAlInicio() {
        assertFalse(falloAyer(hoy, hoy, mapOf(hoy.minusDays(1) to false)))
    }

    @Test fun nuncaDosVeces_textos() {
        assertEquals("Un día no define nada. Mañana es lo que importa.", textoNuncaDosVeces(false, false))
        assertEquals("Ayer no se dio. Hoy es el día que importa.", textoNuncaDosVeces(true, null))
        assertNull(textoNuncaDosVeces(true, true))
        assertNull(textoNuncaDosVeces(false, null))
    }

    @Test fun celebracion_hitoYNormal() {
        assertEquals("¡7 días seguidos! Eso se nota 🎉", mensajeCelebracion(7, false))
        assertEquals("¡Qué bueno que lo hiciste!", mensajeCelebracion(4, false))
        assertEquals("Un día más sin hacerlo. ¡Bien!", mensajeCelebracion(4, true))
    }

    @Test fun cruzoFase_soloEnLaSemanaDelCambio() {
        assertNull(cruzoFase(89))
        assertEquals(Fase.CONSOLIDACION, cruzoFase(90))
        assertEquals(Fase.CONSOLIDACION, cruzoFase(96))
        assertNull(cruzoFase(97))
        assertNull(cruzoFase(269))
        assertEquals(Fase.AUTOMATICO, cruzoFase(270))
        assertEquals(Fase.AUTOMATICO, cruzoFase(276))
        assertNull(cruzoFase(277))
    }

    @Test fun avisoFase_sinCulpaNi21Dias() {
        val textos = listOf(
            textoAvisoFase(Fase.CONSOLIDACION), textoAvisoFase(Fase.AUTOMATICO),
            textoNuncaDosVeces(false, false), textoNuncaDosVeces(true, null),
            mensajeCelebracion(7, false), mensajeCelebracion(2, true),
        )
        textos.forEach {
            assertNotNull(it)
            assertFalse(it!!.contains("21"))
            assertFalse(it.contains("debés"))
        }
        assertNull(textoAvisoFase(Fase.ADQUISICION))
    }
}
