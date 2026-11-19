package com.romtracker.app

import com.romtracker.app.logic.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class ProgresoTest {
    private val inicio = LocalDate.of(2026, 1, 1)
    private fun dia(n: Int) = inicio.plusDays((n - 1).toLong())

    @Test fun diaUnoEsElDiaDeInicio() = assertEquals(1, diaDelHabito(inicio, inicio))

    @Test fun limitesDia90() {
        assertEquals(Fase.ADQUISICION, estadoDeFase(89).fase)
        assertEquals(Fase.CONSOLIDACION, estadoDeFase(90).fase)
        assertEquals(Fase.CONSOLIDACION, estadoDeFase(91).fase)
    }

    @Test fun limitesDia270() {
        assertEquals(Fase.CONSOLIDACION, estadoDeFase(269).fase)
        assertEquals(Fase.AUTOMATICO, estadoDeFase(270).fase)
        assertEquals(Fase.AUTOMATICO, estadoDeFase(271).fase)
    }

    @Test fun diasParaSiguienteFase() {
        assertEquals(56, estadoDeFase(34).diasParaSiguiente)
        assertEquals(170, estadoDeFase(100).diasParaSiguiente)
        assertEquals(1, estadoDeFase(89).diasParaSiguiente)
        assertEquals(180, estadoDeFase(90).diasParaSiguiente)
        assertNull(estadoDeFase(270).diasParaSiguiente)
    }

    @Test fun faseSegunFechas() {
        assertEquals(Fase.ADQUISICION, estadoDeFase(diaDelHabito(inicio, dia(89))).fase)
        assertEquals(Fase.CONSOLIDACION, estadoDeFase(diaDelHabito(inicio, dia(90))).fase)
        assertEquals(Fase.AUTOMATICO, estadoDeFase(diaDelHabito(inicio, dia(270))).fase)
    }

    @Test fun noFuiNoReiniciaLaFase() {
        val registros = (1..60).associate { dia(it) to (it % 2 == 0) } // muchos "no fui"
        val hoy = dia(60)
        assertEquals(60, diaDelHabito(inicio, hoy))
        assertEquals(Fase.ADQUISICION, estadoDeFase(diaDelHabito(inicio, hoy)).fase)
        assertEquals(Consistencia(30, 60), consistencia(inicio, hoy, registros))
    }

    @Test fun sinDatoCuentaEnTotalPeroNoComoCumplido() {
        val registros = mapOf(dia(1) to true, dia(3) to true) // día 2 sin dato
        assertEquals(Consistencia(2, 3), consistencia(inicio, dia(3), registros))
    }

    @Test fun consistenciaIgnoraRegistrosFueraDeRango() {
        val registros = mapOf(inicio.minusDays(1) to true, dia(1) to true, dia(10) to true)
        assertEquals(Consistencia(1, 5), consistencia(inicio, dia(5), registros))
    }

    @Test fun rachaCuentaHastaHoy() {
        val registros = mapOf(dia(1) to false, dia(2) to true, dia(3) to true, dia(4) to true)
        assertEquals(3, rachaActual(inicio, dia(4), registros))
    }

    @Test fun hoySinRegistroNoCortaLaRacha() {
        val registros = mapOf(dia(1) to true, dia(2) to true)
        assertEquals(2, rachaActual(inicio, dia(3), registros))
    }

    @Test fun noFuiCortaLaRacha() {
        val registros = mapOf(dia(1) to true, dia(2) to false)
        assertEquals(0, rachaActual(inicio, dia(2), registros))
    }

    @Test fun sinDatoCortaLaRacha() {
        val registros = mapOf(dia(1) to true, dia(3) to true) // día 2 sin dato
        assertEquals(1, rachaActual(inicio, dia(3), registros))
    }

    @Test fun rachaNoPasaDelInicio() {
        val registros = mapOf(inicio.minusDays(1) to true, dia(1) to true)
        assertEquals(1, rachaActual(inicio, dia(1), registros))
    }
}
