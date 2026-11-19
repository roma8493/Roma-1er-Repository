package com.romtracker.app

import com.romtracker.app.data.Agua
import com.romtracker.app.logic.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class AguaTest {
    private val inicio = LocalDate.of(2026, 1, 1)
    private fun dia(n: Int) = inicio.plusDays((n - 1).toLong())
    private fun agua(n: Int, vasos: Int, meta: Int = 8) = Agua(dia(n).toEpochDay(), vasos, meta)

    @Test fun llegarALaMetaCumple() {
        assertTrue(cumplidoAgua(8, 8))
        assertTrue(cumplidoAgua(10, 8))
        assertFalse(cumplidoAgua(7, 8))
        assertFalse(cumplidoAgua(0, 8))
    }

    @Test fun ajustarVasosNoBajaDeCero() {
        assertEquals(3, ajustarVasos(2, 1))
        assertEquals(1, ajustarVasos(2, -1))
        assertEquals(0, ajustarVasos(0, -1))
    }

    @Test fun registrosMarcanCumplidoNoFuiYSinDato() {
        val r = registrosDeAgua(listOf(agua(1, 8), agua(2, 5)))
        assertEquals(true, r[dia(1)])
        assertEquals(false, r[dia(2)])
        assertNull(r[dia(3)])
    }

    @Test fun cambiarLaMetaNoReinterpretaElPasado() {
        // Día 1: 6 vasos con meta 6 (✓). Día 2: 6 vasos con meta 8 (no ✓).
        val r = registrosDeAgua(listOf(agua(1, 6, meta = 6), agua(2, 6, meta = 8)))
        assertEquals(true, r[dia(1)])
        assertEquals(false, r[dia(2)])
    }

    @Test fun metaActualEsLaDelUltimoDiaOElDefecto() {
        assertEquals(8, metaActual(emptyList()))
        assertEquals(10, metaActual(listOf(agua(1, 8, meta = 8), agua(3, 4, meta = 10), agua(2, 8, meta = 9))))
    }

    @Test fun consistenciaYRachaReutilizadas() {
        // Día 1 ✓, día 2 no ✓, día 3 ✓ (hoy). Racha = 1 porque el día 2 la cortó.
        val r = registrosDeAgua(listOf(agua(1, 8), agua(2, 5), agua(3, 9)))
        assertEquals(Consistencia(2, 3), consistencia(inicio, dia(3), r))
        assertEquals(1, rachaActual(inicio, dia(3), r))
    }

    @Test fun rachaSumaDiasSeguidosYHoySinRegistroNoLaCorta() {
        val r = registrosDeAgua(listOf(agua(1, 8), agua(2, 8), agua(3, 8)))
        assertEquals(3, rachaActual(inicio, dia(3), r))
        assertEquals(3, rachaActual(inicio, dia(4), r))
    }
}
