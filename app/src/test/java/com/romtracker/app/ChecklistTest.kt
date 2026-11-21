package com.romtracker.app

import com.romtracker.app.data.Checklist
import com.romtracker.app.data.ChecklistMarca
import com.romtracker.app.logic.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChecklistTest {
    @Test fun topeDeSeisItems() {
        assertTrue(puedeAgregarItem(5))
        assertFalse(puedeAgregarItem(6))
    }

    @Test fun nuncaSeBorraElUltimo() {
        assertTrue(puedeBorrarItem(2))
        assertFalse(puedeBorrarItem(1))
    }

    @Test fun textoSeLimpiaYVacioNoEsValido() {
        assertEquals("Agua en la mochila", limpiarTextoItem("  Agua en la mochila \n"))
        assertEquals("", limpiarTextoItem("   "))
        assertEquals(LARGO_MAX_ITEM, limpiarTextoItem("x".repeat(200)).length)
    }

    @Test fun losTresItemsPorDefectoTienenIdsFijos() {
        val items = itemsPorDefecto()
        assertEquals(listOf(1L, 2L, 3L), items.map { it.id })
        assertEquals(listOf("Bolso del gym listo", "Comida preparada", "Decidí a qué hora voy"), items.map { it.texto })
    }

    @Test fun checklistViejaSeConvierteEnTresMarcas() {
        val marcas = marcasDeChecklistVieja(Checklist(20002, bolso = true, comida = false, hora = true))
        assertEquals(
            listOf(ChecklistMarca(20002, 1, true), ChecklistMarca(20002, 2, false), ChecklistMarca(20002, 3, true)),
            marcas,
        )
    }

    @Test fun sinMarcaEsNoHecho() {
        val marcas = listOf(ChecklistMarca(20002, 1, true))
        assertTrue(estaHecho(marcas, 1))
        assertFalse(estaHecho(marcas, 2))
    }
}
