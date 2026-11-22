package com.romtracker.app

import com.romtracker.app.data.Desencadenante
import com.romtracker.app.data.claveRegistroPropio
import com.romtracker.app.logic.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DesencadenanteTest {
    private val emociones = OpcionesDesencadenante.EMOCION

    @Test fun laMasFrecuenteGana() {
        val v = listOf("Estrés", "Cansancio", "Cansancio", null)
        assertEquals("Cansancio", opcionMasFrecuente(v, emociones))
    }

    @Test fun enEmpateGanaLaPrimeraDeLaLista() {
        val v = listOf("Cansancio", "Estrés")
        assertEquals("Estrés", opcionMasFrecuente(v, emociones))
    }

    @Test fun sinDatosOSoloNulosDevuelveNull() {
        assertNull(opcionMasFrecuente(emptyList(), emociones))
        assertNull(opcionMasFrecuente(listOf(null, null), emociones))
    }

    @Test fun ignoraValoresQueNoEstanEnLasOpciones() {
        assertNull(opcionMasFrecuente(listOf("Rabia", "Rabia"), emociones))
        assertEquals("Tristeza", opcionMasFrecuente(listOf("Rabia", "Rabia", "Tristeza"), emociones))
    }

    @Test fun faltanParaPatronConUmbralTres() {
        assertEquals(3, faltanParaPatron(0))
        assertEquals(1, faltanParaPatron(2))
        assertEquals(0, faltanParaPatron(3))
        assertEquals(0, faltanParaPatron(10))
    }

    @Test fun sugerenciaSeGuiaPorEmocionYSinEmocionPorLugar() {
        assertTrue(sugerenciaEntorno("Cansancio", "Casa")!!.contains("fuera del alcance"))
        assertNotNull(sugerenciaEntorno(null, "Trabajo"))
        assertNull(sugerenciaEntorno(null, null))
    }

    @Test fun todasLasOpcionesTienenSugerencia() {
        OpcionesDesencadenante.EMOCION.forEach { assertNotNull(it, sugerenciaEntorno(it, null)) }
        OpcionesDesencadenante.LUGAR.forEach { assertNotNull(it, sugerenciaEntorno(null, it)) }
    }

    @Test fun sugerenciasSonInvitacionNuncaOrden() {
        val todas = OpcionesDesencadenante.EMOCION.map { sugerenciaEntorno(it, null)!! } +
            OpcionesDesencadenante.LUGAR.map { sugerenciaEntorno(null, it)!! }
        todas.forEach { assertTrue(it, it.startsWith("Podrías") && !it.contains("debés")) }
    }

    @Test fun desencadenantesDeFiltraPorHabito() {
        val l = listOf(
            Desencadenante(1, claveRegistroPropio(1), 20000),
            Desencadenante(2, claveRegistroPropio(2), 20000),
            Desencadenante(3, claveRegistroPropio(1), 20001),
        )
        assertEquals(2, desencadenantesDe(l, claveRegistroPropio(1)).size)
        assertEquals(0, desencadenantesDe(l, claveRegistroPropio(9)).size)
    }
}
