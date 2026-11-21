package com.romtracker.app.data

import com.romtracker.app.logic.MAX_ITEMS_CHECKLIST
import com.romtracker.app.logic.itemsPorDefecto
import com.romtracker.app.logic.marcasDeChecklistVieja
import org.json.JSONArray
import org.json.JSONObject

/** Todo lo que guarda la app, para la copia de seguridad. */
data class Copia(
    val perfil: Perfil,
    val habitos: List<Habito>,
    val registros: List<Registro>,
    val checklists: List<Checklist>,
    val aguas: List<Agua> = emptyList(),
    val animos: List<Animo> = emptyList(),
    val habitosPropios: List<HabitoPropio> = emptyList(),
    val checklistItems: List<ChecklistItem> = emptyList(),
    val checklistMarcas: List<ChecklistMarca> = emptyList(),
)

/** El archivo no es una copia válida de RomTracker. [message] es apto para mostrar al usuario. */
class CopiaInvalida(message: String) : Exception(message)

object CopiaJson {
    /** 1 = 0.2 (sin agua). 2 = agrega "aguas". 3 = agrega la meta de agua en el perfil. 4 = agrega "animos". 5 = agrega "habitosPropios". 6 = agrega "checklistItems" y "checklistMarcas". 7 = agrega "tema" en el perfil. Las versiones anteriores se siguen leyendo. */
    const val VERSION = 7
    private const val APP = "RomTracker"

    fun aTexto(c: Copia): String {
        val raiz = JSONObject()
        raiz.put("app", APP)
        raiz.put("version", VERSION)
        raiz.put("perfil", JSONObject().apply {
            put("nombre", c.perfil.nombre)
            put("horaSalidaMinutos", c.perfil.horaSalidaMinutos)
            put("claveAncla", c.perfil.claveAncla)
            put("porQue", c.perfil.porQue)
            put("metaAgua", c.perfil.metaAgua)
            put("tema", TemaPerfil.nombre(c.perfil.tema))
        })
        raiz.put("habitos", JSONArray().also { a ->
            c.habitos.forEach { a.put(JSONObject().put("tipo", it.tipo).put("fechaInicio", it.fechaInicio)) }
        })
        raiz.put("registros", JSONArray().also { a ->
            c.registros.forEach {
                a.put(JSONObject().put("habito", it.habito).put("fecha", it.fecha).put("cumplido", it.cumplido))
            }
        })
        raiz.put("checklists", JSONArray().also { a ->
            c.checklists.forEach {
                a.put(
                    JSONObject().put("paraFecha", it.paraFecha)
                        .put("bolso", it.bolso).put("comida", it.comida).put("hora", it.hora)
                )
            }
        })
        raiz.put("aguas", JSONArray().also { a ->
            c.aguas.forEach {
                a.put(JSONObject().put("fecha", it.fecha).put("vasos", it.vasos).put("meta", it.meta))
            }
        })
        raiz.put("animos", JSONArray().also { a ->
            c.animos.forEach { a.put(JSONObject().put("fecha", it.fecha).put("valor", it.valor)) }
        })
        raiz.put("habitosPropios", JSONArray().also { a ->
            c.habitosPropios.forEach {
                a.put(
                    JSONObject().put("id", it.id).put("nombre", it.nombre).put("claveAncla", it.claveAncla)
                        .put("porQue", it.porQue).put("tipo", it.tipo).put("fechaInicio", it.fechaInicio)
                )
            }
        })
        raiz.put("checklistItems", JSONArray().also { a ->
            c.checklistItems.forEach { a.put(JSONObject().put("id", it.id).put("texto", it.texto).put("orden", it.orden)) }
        })
        raiz.put("checklistMarcas", JSONArray().also { a ->
            c.checklistMarcas.forEach {
                a.put(JSONObject().put("paraFecha", it.paraFecha).put("itemId", it.itemId).put("hecho", it.hecho))
            }
        })
        return raiz.toString(2)
    }

    /** Lanza [CopiaInvalida] si el texto no es una copia completa y válida. */
    fun deTexto(texto: String): Copia {
        try {
            val raiz = JSONObject(texto)
            if (raiz.optString("app") != APP) throw CopiaInvalida("Este archivo no es una copia de RomTracker.")
            val version = raiz.getInt("version")
            if (version > VERSION) {
                throw CopiaInvalida("Esta copia es de una versión más nueva de la app. Actualizá RomTracker e intentá de nuevo.")
            }
            val p = raiz.getJSONObject("perfil")
            val perfil = Perfil(
                nombre = p.getString("nombre"),
                horaSalidaMinutos = p.getInt("horaSalidaMinutos"),
                claveAncla = p.getString("claveAncla"),
                porQue = p.getString("porQue"),
                // Las copias anteriores a la versión 3 no traen la meta: se usa la de por defecto.
                metaAgua = p.optInt("metaAgua", 8),
            // Las copias anteriores a la versión 7 no traen el tema: se usa el del sistema.
            tema = TemaPerfil.deNombre(p.optString("tema", "SISTEMA")),
            )
            val habitos = raiz.getJSONArray("habitos").objetos().map {
                val tipo = it.getString("tipo")
                if (tipo != TipoHabito.GYM && tipo != TipoHabito.COMIDA && tipo != TipoHabito.AGUA) {
                    throw CopiaInvalida("La copia tiene un hábito desconocido.")
                }
                Habito(tipo, it.getLong("fechaInicio"))
            }
            // Las copias anteriores a la versión 5 no traen "habitosPropios": se leen como lista vacía.
            val propios = raiz.optJSONArray("habitosPropios")?.objetos()?.map {
                val tipo = it.getString("tipo")
                if (!TipoPropio.valido(tipo)) throw CopiaInvalida("La copia tiene un hábito propio con tipo desconocido.")
                HabitoPropio(
                    it.getLong("id"), it.getString("nombre"), it.getString("claveAncla"),
                    it.getString("porQue"), tipo, it.getLong("fechaInicio"),
                )
            } ?: emptyList()
            val clavesPropias = propios.map { claveRegistroPropio(it.id) }.toSet()
            val registros = raiz.getJSONArray("registros").objetos().map {
                val h = it.getString("habito")
                if (h != TipoHabito.GYM && h != TipoHabito.COMIDA && h !in clavesPropias) {
                    throw CopiaInvalida("La copia tiene un registro desconocido.")
                }
                Registro(h, it.getLong("fecha"), it.getBoolean("cumplido"))
            }
            val checklists = raiz.getJSONArray("checklists").objetos().map {
                Checklist(it.getLong("paraFecha"), it.getBoolean("bolso"), it.getBoolean("comida"), it.getBoolean("hora"))
            }
            // Las copias 0.2 no traen "aguas": se leen como lista vacía.
            val aguas = raiz.optJSONArray("aguas")?.objetos()?.map {
                Agua(it.getLong("fecha"), it.getInt("vasos"), it.getInt("meta"))
            } ?: emptyList()
            // Las copias anteriores a la versión 4 no traen "animos": se leen como lista vacía.
            val animos = raiz.optJSONArray("animos")?.objetos()?.map {
                val v = it.getInt("valor")
                if (v !in 1..5) throw CopiaInvalida("La copia tiene un ánimo fuera de rango.")
                Animo(it.getLong("fecha"), v)
            } ?: emptyList()
            // Las copias anteriores a la versión 6 no traen la checklist editable: se siembran los 3 ítems de
            // siempre y sus marcas salen de las 3 casillas viejas.
            val itemsJson = raiz.optJSONArray("checklistItems")
            val items = itemsJson?.objetos()?.map {
                ChecklistItem(it.getLong("id"), it.getString("texto"), it.getInt("orden"))
            } ?: itemsPorDefecto()
            if (items.size > MAX_ITEMS_CHECKLIST) throw CopiaInvalida("La copia tiene demasiados ítems de checklist.")
            val idsItems = items.map { it.id }.toSet()
            val marcas = raiz.optJSONArray("checklistMarcas")?.objetos()?.map {
                val id = it.getLong("itemId")
                if (id !in idsItems) throw CopiaInvalida("La copia tiene una marca de checklist desconocida.")
                ChecklistMarca(it.getLong("paraFecha"), id, it.getBoolean("hecho"))
            } ?: if (itemsJson == null) checklists.flatMap { marcasDeChecklistVieja(it) } else emptyList()
            return Copia(perfil, habitos, registros, checklists, aguas, animos, propios, items, marcas)
        } catch (e: CopiaInvalida) {
            throw e
        } catch (e: Exception) {
            // JSON roto, campos que faltan o con tipo equivocado
            throw CopiaInvalida("No pude leer el archivo: no parece una copia de RomTracker.")
        }
    }

    private fun JSONArray.objetos(): List<JSONObject> = List(length()) { getJSONObject(it) }
}
