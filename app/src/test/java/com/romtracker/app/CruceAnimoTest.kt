package com.romtracker.app

import com.romtracker.app.logic.cruceAnimo
import com.romtracker.app.logic.diasFaltanParaCruce
import com.romtracker.app.logic.textoCruce
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CruceAnimoTest {
    private val d0 = LocalDate.of(2026, 1, 1)
    private fun dia(n: Int) = d0.plusDays(n.toLong())

    @Test fun promediosSeparadosPorCumplido() {
        val animos = mapOf(dia(0) to 5, dia(1) to 4, dia(2) to 2, dia(3) to 3)
        val regs = mapOf(dia(0) to true, dia(1) to true, dia(2) to false)
        val c = cruceAnimo(animos, regs)
        assertEquals(2, c.diasCumplido); assertEquals(4.5, c.promedioCumplido!!, 0.001)
        assertEquals(1, c.diasNoCumplido); assertEquals(2.0, c.promedioNoCumplido!!, 0.001)
    }

    @Test fun sinDatoDelHabitoNoCuenta() {
        // dia(3) tiene ánimo pero el hábito no tiene registro: no entra en ningún grupo
        val c = cruceAnimo(mapOf(dia(3) to 1), emptyMap())
        assertEquals(0, c.diasCumplido); assertEquals(0, c.diasNoCumplido)
        assertNull(c.promedioCumplido); assertNull(c.promedioNoCumplido)
    }

    @Test fun umbralDe14Dias() {
        assertEquals(14, diasFaltanParaCruce(emptyMap()))
        assertEquals(1, diasFaltanParaCruce((0 until 13).associate { dia(it) to 3 }))
        assertEquals(0, diasFaltanParaCruce((0 until 14).associate { dia(it) to 3 }))
        assertEquals(0, diasFaltanParaCruce((0 until 20).associate { dia(it) to 3 }))
    }

    @Test fun textosNeutros() {
        val mas = textoCruce(cruceAnimo(mapOf(dia(0) to 5, dia(1) to 2), mapOf(dia(0) to true, dia(1) to false)))
        assertTrue(mas.contains("se asocian con un ánimo más alto"))
        val igual = textoCruce(cruceAnimo(mapOf(dia(0) to 4, dia(1) to 4), mapOf(dia(0) to true, dia(1) to false)))
        assertTrue(igual.contains("no se ve una diferencia clara"))
        val falta = textoCruce(cruceAnimo(mapOf(dia(0) to 4), mapOf(dia(0) to true)))
        assertTrue(falta.contains("Todavía no hay"))
        listOf(mas, igual, falta).forEach { assertTrue(!it.contains("causa", ignoreCase = true)) }
    }
}
