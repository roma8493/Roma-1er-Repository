package com.romtracker.app

import com.romtracker.app.data.Automatismo
import com.romtracker.app.logic.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutomatismoTest {
    @Test fun puntajeEsElPromedioDeLasCuatroRespuestas() {
        assertEquals(3.5, puntajeAutomatismo(Automatismo("GYM", 100, 5, 4, 3, 2)), 0.0001)
        assertEquals(1.0, puntajeAutomatismo(Automatismo("GYM", 100, 1, 1, 1, 1)), 0.0001)
        assertEquals(5.0, puntajeAutomatismo(Automatismo("GYM", 100, 5, 5, 5, 5)), 0.0001)
    }

    @Test fun sinChecksTocaA28DiasDelInicioNoAntes() {
        assertFalse(tocaCheckAutomatismo(1000, null, 1027))
        assertTrue(tocaCheckAutomatismo(1000, null, 1028))
        assertTrue(tocaCheckAutomatismo(1000, null, 1100))
    }

    @Test fun conCheckPrevioCuentaDesdeElUltimoNoDesdeElInicio() {
        assertFalse(tocaCheckAutomatismo(1000, 1030, 1057))
        assertTrue(tocaCheckAutomatismo(1000, 1030, 1058))
    }

    @Test fun habitosQueTocanUsaElUltimoCheckDeCadaHabito() {
        val habitos = listOf("GYM" to 1000L, "COMIDA" to 1000L, "PROPIO_1" to 1020L)
        val checks = listOf(
            Automatismo("GYM", 1005, 3, 3, 3, 3),
            Automatismo("GYM", 1030, 3, 3, 3, 3),
            Automatismo("COMIDA", 1000, 2, 2, 2, 2),
        )
        // Hoy = 1040: GYM (último 1030) no toca; COMIDA (último 1000) sí; PROPIO_1 (inicio 1020) no.
        assertEquals(listOf("COMIDA"), habitosQueTocan(habitos, checks, 1040))
        // Hoy = 1048: PROPIO_1 ya cumplió 28 días.
        assertEquals(listOf("COMIDA", "PROPIO_1"), habitosQueTocan(habitos, checks, 1048))
    }

    @Test fun sinHabitosNoTocaNada() = assertTrue(habitosQueTocan(emptyList(), emptyList(), 5000).isEmpty())

    @Test fun checksDeHabitoFiltraYOrdenaPorFecha() {
        val checks = listOf(
            Automatismo("GYM", 1030, 4, 4, 4, 4),
            Automatismo("COMIDA", 1000, 2, 2, 2, 2),
            Automatismo("GYM", 1002, 3, 3, 3, 3),
        )
        assertEquals(listOf(1002L, 1030L), checksDeHabito("GYM", checks).map { it.fecha })
    }
}
