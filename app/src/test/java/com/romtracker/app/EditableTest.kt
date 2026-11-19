package com.romtracker.app

import com.romtracker.app.logic.esEditable
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class EditableTest {
    private val hoy = LocalDate.of(2026, 11, 19)
    private val inicioViejo = LocalDate.of(2026, 1, 1)

    @Test fun hoyYSieteDiasAtrasSonEditables() {
        assertTrue(esEditable(hoy, inicioViejo, hoy))
        assertTrue(esEditable(hoy.minusDays(7), inicioViejo, hoy))
    }

    @Test fun ochoDiasAtrasYFuturoNoSonEditables() {
        assertFalse(esEditable(hoy.minusDays(8), inicioViejo, hoy))
        assertFalse(esEditable(hoy.plusDays(1), inicioViejo, hoy))
    }

    @Test fun antesDelInicioNoEsEditable() {
        assertFalse(esEditable(hoy.minusDays(1), hoy, hoy))
        assertTrue(esEditable(hoy, hoy, hoy))
    }
}
