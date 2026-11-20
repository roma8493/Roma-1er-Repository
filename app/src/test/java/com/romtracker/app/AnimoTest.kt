package com.romtracker.app

import com.romtracker.app.logic.CARITAS
import com.romtracker.app.logic.NOMBRES_ANIMO
import com.romtracker.app.logic.animoEditable
import com.romtracker.app.logic.caritaDe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class AnimoTest {
    private val hoy = LocalDate.of(2026, 11, 20)

    @Test fun hayCincoCaritasConNombre() {
        assertEquals(5, CARITAS.size)
        assertEquals(5, NOMBRES_ANIMO.size)
    }

    @Test fun caritaDeValorValidoEInvalido() {
        assertEquals(CARITAS[0], caritaDe(1))
        assertEquals(CARITAS[4], caritaDe(5))
        assertNull(caritaDe(0))
        assertNull(caritaDe(6))
        assertNull(caritaDe(null))
    }

    @Test fun editableHoyYSieteDiasAtras() {
        assertTrue(animoEditable(hoy, hoy))
        assertTrue(animoEditable(hoy.minusDays(7), hoy))
    }

    @Test fun noEditableOchoDiasAtrasNiFuturo() {
        assertFalse(animoEditable(hoy.minusDays(8), hoy))
        assertFalse(animoEditable(hoy.plusDays(1), hoy))
    }
}
