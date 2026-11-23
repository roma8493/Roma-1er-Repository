package com.romtracker.app

import com.romtracker.app.data.Automatismo
import com.romtracker.app.data.Habito
import com.romtracker.app.data.HabitoPropio
import com.romtracker.app.data.Registro
import com.romtracker.app.data.TipoHabito
import com.romtracker.app.data.TipoPropio
import com.romtracker.app.logic.DatosConvertibles
import com.romtracker.app.logic.convertirFijos
import org.junit.Assert.assertEquals
import org.junit.Test

class ConversionFijosTest {
    private fun convertir(d: DatosConvertibles) = convertirFijos(d, "salir del trabajo", "Energía", "20 min")

    @Test fun sinFijosNoCambiaNada() {
        val d = DatosConvertibles(listOf(Habito(TipoHabito.AGUA, 5)), emptyList(), emptyList(), emptyList())
        assertEquals(d, convertir(d))
    }

    @Test fun elIdNuevoSigueAlMayorExistente() {
        val leer = HabitoPropio(7, "Leer", "a", "b", TipoPropio.HACER, 1)
        val r = convertir(DatosConvertibles(listOf(Habito("GYM", 10)), listOf(leer), emptyList(), emptyList()))
        assertEquals(listOf(7L, 8L), r.propios.map { it.id })
    }

    @Test fun comidaRecibeAnclaYPorQueNeutros() {
        val r = convertir(DatosConvertibles(listOf(Habito("COMIDA", 10)), emptyList(), emptyList(), emptyList()))
        val c = r.propios.single()
        assertEquals("Comida", c.nombre)
        assertEquals("mi comida del día", c.claveAncla)
        assertEquals("Comer sano me hace bien", c.porQue)
        assertEquals("", c.versionMinima)
    }

    @Test fun sinFilaDeHabitoUsaElPrimerRegistro() {
        val regs = listOf(Registro("GYM", 30, true), Registro("GYM", 25, false))
        val r = convertir(DatosConvertibles(emptyList(), emptyList(), regs, emptyList()))
        assertEquals(25L, r.propios.single().fechaInicio)
        assertEquals(setOf("PROPIO_1"), r.registros.map { it.habito }.toSet())
    }

    @Test fun losRegistrosDeOtrosHabitosNoSeTocan() {
        val regs = listOf(Registro("PROPIO_1", 1, true), Registro("GYM", 2, true))
        val autos = listOf(Automatismo("AGUA", 3, 1, 1, 1, 1), Automatismo("GYM", 4, 2, 2, 2, 2))
        val leer = HabitoPropio(1, "Leer", "a", "b", TipoPropio.HACER, 1)
        val r = convertir(DatosConvertibles(listOf(Habito("GYM", 2)), listOf(leer), regs, autos))
        assertEquals(listOf(Registro("PROPIO_1", 1, true), Registro("PROPIO_2", 2, true)), r.registros)
        assertEquals(listOf(Automatismo("AGUA", 3, 1, 1, 1, 1), Automatismo("PROPIO_2", 4, 2, 2, 2, 2)), r.automatismos)
    }
}
