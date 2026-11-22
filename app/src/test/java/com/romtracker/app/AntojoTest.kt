package com.romtracker.app

import com.romtracker.app.data.Antojo
import com.romtracker.app.data.claveRegistroPropio
import com.romtracker.app.logic.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class AntojoTest {
    private fun dia(a: Int, m: Int, d: Int) = LocalDate.of(a, m, d).toEpochDay()
    private val hoy = dia(2026, 11, 19) // jueves; el lunes fue el 16
    private fun a(id: Long, fecha: Long) = Antojo(0, claveRegistroPropio(id), fecha)

    @Test fun cuentaLosDeHoySoloDelHabito() {
        val l = listOf(a(1, hoy), a(1, hoy), a(1, hoy - 1), a(2, hoy))
        assertEquals(2, antojosDeHoy(l, 1, hoy))
        assertEquals(1, antojosDeHoy(l, 2, hoy))
        assertEquals(0, antojosDeHoy(l, 3, hoy))
    }

    @Test fun laSemanaVaDesdeElLunesHastaHoy() {
        val lunes = dia(2026, 11, 16)
        val l = listOf(a(1, lunes), a(1, hoy), a(1, lunes - 1), a(1, hoy + 1))
        assertEquals(2, antojosDeLaSemana(l, 1, hoy))
    }

    @Test fun elDomingoLaSemanaTodaviaEsLaDelLunesAnterior() {
        val domingo = dia(2026, 11, 22)
        val l = listOf(a(1, dia(2026, 11, 16)), a(1, dia(2026, 11, 15)))
        assertEquals(1, antojosDeLaSemana(l, 1, domingo))
    }

    @Test fun totalesCuentaTodoElHistorial() {
        val l = listOf(a(1, hoy), a(1, hoy - 30), a(1, hoy - 100), a(2, hoy))
        assertEquals(3, antojosTotales(l, 1))
        assertEquals(0, antojosTotales(emptyList(), 1))
    }

    @Test fun sumarYRestarNuncaBajaDeCero() {
        assertEquals(1, conteoTrasCambio(0, 1))
        assertEquals(2, conteoTrasCambio(3, -1))
        assertEquals(0, conteoTrasCambio(0, -1))
    }
}
