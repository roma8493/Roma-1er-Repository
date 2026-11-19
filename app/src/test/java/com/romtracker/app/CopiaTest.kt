package com.romtracker.app

import com.romtracker.app.data.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class CopiaTest {
    private val copia = Copia(
        perfil = Perfil(nombre = "Ana \"Test\" ñ", horaSalidaMinutos = 1020, claveAncla = "salir del trabajo", porQue = "Tener energía\ny dormir mejor"),
        habitos = listOf(Habito(TipoHabito.GYM, 20000), Habito(TipoHabito.COMIDA, 20003)),
        registros = listOf(
            Registro(TipoHabito.GYM, 20000, true),
            Registro(TipoHabito.GYM, 20001, false),
            Registro(TipoHabito.COMIDA, 20003, true),
        ),
        checklists = listOf(Checklist(20002, bolso = true, comida = false, hora = true)),
    )

    private fun invalida(texto: String): String {
        try {
            CopiaJson.deTexto(texto)
        } catch (e: CopiaInvalida) {
            return e.message ?: ""
        }
        fail("Debía rechazar: $texto")
        return ""
    }

    @Test fun idaYVueltaConservaTodo() = assertEquals(copia, CopiaJson.deTexto(CopiaJson.aTexto(copia)))

    @Test fun copiaSinRegistrosNiChecklistsEsValida() {
        val vacia = copia.copy(registros = emptyList(), checklists = emptyList())
        assertEquals(vacia, CopiaJson.deTexto(CopiaJson.aTexto(vacia)))
    }

    @Test fun textoQueNoEsJsonSeRechaza() {
        assertTrue(invalida("esto no es json").isNotBlank())
        assertTrue(invalida("").isNotBlank())
    }

    @Test fun jsonDeOtraAppSeRechaza() {
         invalida("""{"app":"OtraApp","version":1}""")
    }

    @Test fun versionMasNuevaSeRechazaConMensaje() {
        val texto = CopiaJson.aTexto(copia).replace("\"version\": ${CopiaJson.VERSION}", "\"version\": 99")
        assertTrue(invalida(texto).contains("más nueva"))
    }

    @Test fun idaYVueltaConservaAgua() {
        val conAgua = copia.copy(
            habitos = copia.habitos + Habito(TipoHabito.AGUA, 20005),
            aguas = listOf(Agua(20005, 8, 8), Agua(20006, 3, 6)),
        )
        assertEquals(conAgua, CopiaJson.deTexto(CopiaJson.aTexto(conAgua)))
    }

    @Test fun copiaViejaSinAguaSeRestauraConAguaVacia() {
        // Una copia 0.2 (versión 1) no trae "aguas".
        val raiz = org.json.JSONObject(CopiaJson.aTexto(copia))
        raiz.remove("aguas")
        raiz.put("version", 1)
        val leida = CopiaJson.deTexto(raiz.toString())
        assertEquals(copia, leida)
        assertTrue(leida.aguas.isEmpty())
    }

    @Test fun faltanCamposSeRechaza() {
        invalida("""{"app":"RomTracker","version":1}""")
    }

    @Test fun habitoDesconocidoSeRechaza() {
        val texto = CopiaJson.aTexto(copia).replace("\"COMIDA\"", "\"OTRO\"")
        invalida(texto)
    }
}
