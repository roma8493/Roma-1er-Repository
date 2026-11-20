package com.romtracker.app

import com.romtracker.app.data.Registro
import com.romtracker.app.data.TipoPropio
import com.romtracker.app.data.claveRegistroPropio
import com.romtracker.app.logic.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PropiosTest {
    @Test fun hacerTildaSiLoHizo() {
        assertTrue(cumplidoPropio(TipoPropio.HACER, true))
        assertFalse(cumplidoPropio(TipoPropio.HACER, false))
    }

    @Test fun dejarTildaSiNoLoHizo() {
        assertTrue(cumplidoPropio(TipoPropio.DEJAR, false))
        assertFalse(cumplidoPropio(TipoPropio.DEJAR, true))
    }

    @Test fun lasDosConversionesSonInversas() {
        for (tipo in listOf(TipoPropio.HACER, TipoPropio.DEJAR)) for (r in listOf(true, false)) {
            assertEquals(r, hizoLaAccionPropio(tipo, cumplidoPropio(tipo, r)))
        }
    }

    @Test fun registrosSoloTomaLosDelHabito() {
        val todos = listOf(
            Registro(claveRegistroPropio(1), 20000, true),
            Registro(claveRegistroPropio(2), 20000, false),
            Registro("GYM", 20001, true),
        )
        assertEquals(mapOf(LocalDate.ofEpochDay(20000) to true), registrosDePropio(1, todos))
        assertEquals(mapOf(LocalDate.ofEpochDay(20000) to false), registrosDePropio(2, todos))
    }

    @Test fun rachaDeDejarCuentaLosDiasQueNoLoHizo() {
        val inicio = LocalDate.ofEpochDay(20000)
        val hoy = inicio.plusDays(3)
        val regs = (0..3).associate { inicio.plusDays(it.toLong()) to cumplidoPropio(TipoPropio.DEJAR, false) }
        assertEquals(4, rachaActual(inicio, hoy, regs))
        assertEquals(Consistencia(4, 4), consistencia(inicio, hoy, regs))
    }

    @Test fun recaerCortaLaRachaPeroNoLaFase() {
        val inicio = LocalDate.ofEpochDay(20000)
        val hoy = inicio.plusDays(4)
        val regs = mapOf(
            inicio to true, inicio.plusDays(1) to true, inicio.plusDays(2) to cumplidoPropio(TipoPropio.DEJAR, true),
            inicio.plusDays(3) to true, hoy to true,
        )
        assertEquals(2, rachaActual(inicio, hoy, regs))
        assertEquals(diaDelHabito(inicio, hoy), 5)
    }
}
