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
        raiz.getJSONObject("perfil").remove("metaAgua")
        raiz.put("version", 1)
        val leida = CopiaJson.deTexto(raiz.toString())
        assertEquals(copia, leida)
        assertTrue(leida.aguas.isEmpty())
        assertEquals(8, leida.perfil.metaAgua)
    }

    @Test fun idaYVueltaConservaLaMetaDeAgua() {
        val conMeta = copia.copy(perfil = copia.perfil.copy(metaAgua = 10))
        assertEquals(10, CopiaJson.deTexto(CopiaJson.aTexto(conMeta)).perfil.metaAgua)
    }

    @Test fun idaYVueltaConservaElTema() {
        for (tema in listOf(TemaPerfil.CLARO, TemaPerfil.OSCURO)) {
            val c = copia.copy(perfil = copia.perfil.copy(tema = tema))
            assertEquals(tema, CopiaJson.deTexto(CopiaJson.aTexto(c)).perfil.tema)
        }
    }

    @Test fun copiaViejaSinTemaSeRestauraConTemaClaro() {
        val raiz = org.json.JSONObject(CopiaJson.aTexto(copia.copy(perfil = copia.perfil.copy(tema = TemaPerfil.OSCURO))))
        raiz.getJSONObject("perfil").remove("tema")
        raiz.put("version", 6)
        val leida = CopiaJson.deTexto(raiz.toString())
        assertEquals(TemaPerfil.CLARO, leida.perfil.tema)
        assertEquals(copia, leida)
    }

    @Test fun temaSistemaDeLaCopia081SeLeeComoClaro() {
        assertEquals(TemaPerfil.CLARO, TemaPerfil.deNombre("SISTEMA"))
        assertEquals(TemaPerfil.CLARO, TemaPerfil.deNombre("VIOLETA"))
        assertEquals(TemaPerfil.CLARO, TemaPerfil.deNombre(null))
        assertEquals(TemaPerfil.OSCURO, TemaPerfil.deNombre("OSCURO"))
    }

    @Test fun idaYVueltaConservaLaVersionMinima() {
        val c = copia.copy(
            perfil = copia.perfil.copy(versionMinima = "solo 20 min de gym"),
            habitosPropios = listOf(propio.copy(versionMinima = "solo 5 min")),
            registros = copia.registros + Registro(claveRegistroPropio(3), 20010, true),
        )
        val leida = CopiaJson.deTexto(CopiaJson.aTexto(c))
        assertEquals(c, leida)
        assertEquals("solo 20 min de gym", leida.perfil.versionMinima)
        assertEquals("solo 5 min", leida.habitosPropios.single().versionMinima)
    }

    @Test fun copiaViejaSinVersionMinimaSeRestauraConTextoVacio() {
        val c = copia.copy(
            habitosPropios = listOf(propio),
            registros = copia.registros + Registro(claveRegistroPropio(3), 20010, true),
        )
        val raiz = org.json.JSONObject(CopiaJson.aTexto(c))
        raiz.getJSONObject("perfil").remove("versionMinima")
        raiz.getJSONArray("habitosPropios").getJSONObject(0).remove("versionMinima")
        raiz.put("version", 7)
        val leida = CopiaJson.deTexto(raiz.toString())
        assertEquals("", leida.perfil.versionMinima)
        assertEquals("", leida.habitosPropios.single().versionMinima)
        assertEquals(c, leida)
    }

    @Test fun faltanCamposSeRechaza() {
        invalida("""{"app":"RomTracker","version":1}""")
    }

    @Test fun habitoDesconocidoSeRechaza() {
        val texto = CopiaJson.aTexto(copia).replace("\"COMIDA\"", "\"OTRO\"")
        invalida(texto)
    }

    @Test fun idaYVueltaConservaAnimo() {
        val conAnimo = copia.copy(animos = listOf(Animo(20005, 1), Animo(20006, 5)))
        assertEquals(conAnimo, CopiaJson.deTexto(CopiaJson.aTexto(conAnimo)))
    }

    @Test fun copiaViejaSinAnimoSeRestauraConAnimoVacio() {
        val raiz = org.json.JSONObject(CopiaJson.aTexto(copia))
        raiz.remove("animos")
        raiz.put("version", 3)
        val leida = CopiaJson.deTexto(raiz.toString())
        assertEquals(copia, leida)
        assertTrue(leida.animos.isEmpty())
    }

    @Test fun animoFueraDeRangoSeRechaza() {
        val conAnimo = copia.copy(animos = listOf(Animo(20005, 3)))
        invalida(CopiaJson.aTexto(conAnimo).replace("\"valor\": 3", "\"valor\": 9"))
    }

    private val propio = HabitoPropio(3, "No scrollear", "al acostarme", "Dormir mejor", TipoPropio.DEJAR, 20010)

    @Test fun idaYVueltaConservaHabitosPropios() {
        val conPropios = copia.copy(
            habitosPropios = listOf(propio),
            registros = copia.registros + Registro(claveRegistroPropio(3), 20010, true),
        )
        assertEquals(conPropios, CopiaJson.deTexto(CopiaJson.aTexto(conPropios)))
    }

    @Test fun copiaViejaSinPropiosSeRestauraConListaVacia() {
        val raiz = org.json.JSONObject(CopiaJson.aTexto(copia))
        raiz.remove("habitosPropios")
        raiz.put("version", 4)
        val leida = CopiaJson.deTexto(raiz.toString())
        assertEquals(copia, leida)
        assertTrue(leida.habitosPropios.isEmpty())
    }

    @Test fun tipoPropioDesconocidoSeRechaza() {
        val texto = CopiaJson.aTexto(copia.copy(habitosPropios = listOf(propio))).replace("\"DEJAR\"", "\"XXX\"")
        invalida(texto)
    }

    @Test fun registroDeHabitoPropioInexistenteSeRechaza() {
        invalida(CopiaJson.aTexto(copia.copy(registros = copia.registros + Registro(claveRegistroPropio(99), 20010, true))))
    }

    @Test fun idaYVueltaConservaChecklistEditable() {
        val conItems = copia.copy(
            checklistItems = listOf(ChecklistItem(1, "Bolso del gym listo", 1), ChecklistItem(7, "Agua en la mochila", 2)),
            checklistMarcas = listOf(ChecklistMarca(20002, 1, true), ChecklistMarca(20002, 7, false)),
        )
        assertEquals(conItems, CopiaJson.deTexto(CopiaJson.aTexto(conItems)))
    }

    @Test fun copiaViejaSinChecklistEditableSiembraLosTresItemsYSusMarcas() {
        // Copia versión 5: sin "checklistItems" ni "checklistMarcas".
        val texto = CopiaJson.aTexto(copia)
            .let { org.json.JSONObject(it).apply { remove("checklistItems"); remove("checklistMarcas"); put("version", 5) }.toString() }
        val leida = CopiaJson.deTexto(texto)
        assertEquals(listOf(1L, 2L, 3L), leida.checklistItems.map { it.id })
        assertEquals(
            listOf(ChecklistMarca(20002, 1, true), ChecklistMarca(20002, 2, false), ChecklistMarca(20002, 3, true)),
            leida.checklistMarcas,
        )
    }

    @Test fun marcaDeItemInexistenteSeRechaza() {
        val mala = copia.copy(
            checklistItems = listOf(ChecklistItem(1, "Uno", 1)),
            checklistMarcas = listOf(ChecklistMarca(20002, 99, true)),
        )
        assertTrue(invalida(CopiaJson.aTexto(mala)).isNotBlank())
    }

    @Test fun idaYVueltaConservaAutomatismos() {
        val propio = HabitoPropio(3, "Leer", "después de cenar", "Aprender", TipoPropio.HACER, 20000)
        val conChecks = copia.copy(
            habitosPropios = listOf(propio),
            automatismos = listOf(Automatismo(TipoHabito.GYM, 20028, 3, 4, 2, 5), Automatismo(claveRegistroPropio(3), 20030, 1, 1, 2, 2)),
        )
        assertEquals(conChecks, CopiaJson.deTexto(CopiaJson.aTexto(conChecks)))
    }

    @Test fun copiaViejaSinAutomatismosSeRestauraConListaVacia() {
        val texto = org.json.JSONObject(CopiaJson.aTexto(copia)).apply { remove("automatismos"); put("version", 8) }.toString()
        assertTrue(CopiaJson.deTexto(texto).automatismos.isEmpty())
    }

    @Test fun automatismoFueraDeRangoSeRechaza() {
        val mala = copia.copy(automatismos = listOf(Automatismo(TipoHabito.GYM, 20028, 3, 6, 2, 5)))
        assertTrue(invalida(CopiaJson.aTexto(mala)).isNotBlank())
    }

    @Test fun automatismoDeHabitoDesconocidoSeRechaza() {
        val mala = copia.copy(automatismos = listOf(Automatismo(claveRegistroPropio(99), 20028, 3, 3, 3, 3)))
        assertTrue(invalida(CopiaJson.aTexto(mala)).isNotBlank())
    }

    private val lunes = java.time.LocalDate.of(2026, 11, 16).toEpochDay()

    @Test fun idaYVueltaConservaRevisiones() {
        val conRevision = copia.copy(revisiones = listOf(Revision(lunes, "Cansancio", "Dejar el bolso listo"), Revision(lunes + 7, "", "")))
        assertEquals(conRevision, CopiaJson.deTexto(CopiaJson.aTexto(conRevision)))
    }

    @Test fun copiaViejaSinRevisionesSeRestauraConListaVacia() {
        val texto = org.json.JSONObject(CopiaJson.aTexto(copia)).apply { remove("revisiones"); put("version", 9) }.toString()
        assertTrue(CopiaJson.deTexto(texto).revisiones.isEmpty())
    }

    @Test fun revisionConSemanaQueNoEsLunesSeRechaza() {
        val mala = copia.copy(revisiones = listOf(Revision(lunes + 2, "x", "y")))
        assertTrue(invalida(CopiaJson.aTexto(mala)).isNotBlank())
    }

    private val propioDejar = HabitoPropio(1, "Fumar", "Después del café", "Salud", TipoPropio.DEJAR, 20000)
    private val propioHacer = HabitoPropio(2, "Leer", "Antes de dormir", "Calma", TipoPropio.HACER, 20000)

    @Test fun idaYVueltaConservaAntojos() {
        val c = copia.copy(
            habitosPropios = listOf(propioDejar),
            antojos = listOf(Antojo(1, claveRegistroPropio(1), 20100), Antojo(2, claveRegistroPropio(1), 20100)),
        )
        assertEquals(c, CopiaJson.deTexto(CopiaJson.aTexto(c)))
    }

    @Test fun copiaViejaSinAntojosSeRestauraConListaVacia() {
        val texto = org.json.JSONObject(CopiaJson.aTexto(copia)).apply { remove("antojos"); put("version", 10) }.toString()
        assertTrue(CopiaJson.deTexto(texto).antojos.isEmpty())
    }

    @Test fun antojoDeHabitoDesconocidoOQueNoEsDejarSeRechaza() {
        val desconocido = copia.copy(antojos = listOf(Antojo(1, claveRegistroPropio(99), 20100)))
        assertTrue(invalida(CopiaJson.aTexto(desconocido)).isNotBlank())
        val hacer = copia.copy(habitosPropios = listOf(propioHacer), antojos = listOf(Antojo(1, claveRegistroPropio(2), 20100)))
        assertTrue(invalida(CopiaJson.aTexto(hacer)).isNotBlank())
    }

    @Test fun idaYVueltaConservaDesencadenantesConOpcionesVacias() {
        val c = copia.copy(
            habitosPropios = listOf(propioDejar),
            desencadenantes = listOf(
                Desencadenante(1, claveRegistroPropio(1), 20100, "Casa", "Noche", "Estrés", "Solo"),
                Desencadenante(2, claveRegistroPropio(1), 20101, null, null, "Cansancio", null),
            ),
        )
        assertEquals(c, CopiaJson.deTexto(CopiaJson.aTexto(c)))
    }

    @Test fun copiaViejaSinDesencadenantesSeRestauraConListaVacia() {
        val texto = org.json.JSONObject(CopiaJson.aTexto(copia)).apply { remove("desencadenantes"); put("version", 11) }.toString()
        assertTrue(CopiaJson.deTexto(texto).desencadenantes.isEmpty())
    }

    @Test fun desencadenanteDeHabitoDesconocidoOQueNoEsDejarSeRechaza() {
        val desconocido = copia.copy(desencadenantes = listOf(Desencadenante(1, claveRegistroPropio(99), 20100)))
        assertTrue(invalida(CopiaJson.aTexto(desconocido)).isNotBlank())
        val hacer = copia.copy(habitosPropios = listOf(propioHacer), desencadenantes = listOf(Desencadenante(1, claveRegistroPropio(2), 20100)))
        assertTrue(invalida(CopiaJson.aTexto(hacer)).isNotBlank())
    }

    @Test fun desencadenanteConOpcionDesconocidaSeRechaza() {
        val mala = copia.copy(habitosPropios = listOf(propioDejar), desencadenantes = listOf(Desencadenante(1, claveRegistroPropio(1), 20100, lugar = "Luna")))
        assertTrue(invalida(CopiaJson.aTexto(mala)).isNotBlank())
    }
}
