package com.romtracker.app

import com.romtracker.app.data.Revision
import com.romtracker.app.logic.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class RevisionTest {
    private fun dia(a: Int, m: Int, d: Int) = LocalDate.of(a, m, d).toEpochDay()

    @Test fun lunesDeDevuelveElLunesDeEsaSemana() {
        val lunes = dia(2026, 11, 16)
        assertEquals(DayOfWeek.MONDAY, LocalDate.ofEpochDay(lunes).dayOfWeek)
        for (d in 0..6) assertEquals(lunes, lunesDe(lunes + d))
        assertEquals(lunes + 7, lunesDe(lunes + 7))
    }

    @Test fun lunesDeFuncionaAntesDelEpoch() {
        val f = dia(1969, 12, 31)
        assertEquals(DayOfWeek.MONDAY, LocalDate.ofEpochDay(lunesDe(f)).dayOfWeek)
    }

    @Test fun sinRevisionesNoTocaLaSemanaEnQueEmpezoElHabito() {
        val inicio = dia(2026, 11, 18) // miércoles
        assertFalse(tocaRevision(inicio, null, dia(2026, 11, 18)))
        assertFalse(tocaRevision(inicio, null, dia(2026, 11, 22))) // domingo de la misma semana
        assertTrue(tocaRevision(inicio, null, dia(2026, 11, 23))) // lunes siguiente
    }

    @Test fun conRevisionTocaAlEmpezarUnaSemanaNueva() {
        val semana = dia(2026, 11, 16)
        assertFalse(tocaRevision(dia(2026, 10, 1), semana, dia(2026, 11, 22)))
        assertTrue(tocaRevision(dia(2026, 10, 1), semana, dia(2026, 11, 23)))
        assertTrue(tocaRevision(dia(2026, 10, 1), semana, dia(2026, 12, 15)))
    }

    @Test fun revisionesRecientesVanDeLaMasNuevaALaMasVieja() {
        val r = listOf(Revision(100), Revision(114), Revision(107))
        assertEquals(listOf(114L, 107L, 100L), revisionesRecientes(r).map { it.semana })
    }
}
