package com.romtracker.app.data

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
)

/** El archivo no es una copia válida de RomTracker. [message] es apto para mostrar al usuario. */
class CopiaInvalida(message: String) : Exception(message)

object CopiaJson {
    /** 1 = 0.2 (sin agua). 2 = agrega "aguas". 3 = agrega la meta de agua en el perfil. 4 = agrega "animos". Las versiones anteriores se siguen leyendo. */
    const val VERSION = 4
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
            )
            val habitos = raiz.getJSONArray("habitos").objetos().map {
                val tipo = it.getString("tipo")
                if (tipo != TipoHabito.GYM && tipo != TipoHabito.COMIDA && tipo != TipoHabito.AGUA) {
                    throw CopiaInvalida("La copia tiene un hábito desconocido.")
                }
                Habito(tipo, it.getLong("fechaInicio"))
            }
            val registros = raiz.getJSONArray("registros").objetos().map {
                val h = it.getString("habito")
                if (h != TipoHabito.GYM && h != TipoHabito.COMIDA) throw CopiaInvalida("La copia tiene un registro desconocido.")
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
            return Copia(perfil, habitos, registros, checklists, aguas, animos)
        } catch (e: CopiaInvalida) {
            throw e
        } catch (e: Exception) {
            // JSON roto, campos que faltan o con tipo equivocado
            throw CopiaInvalida("No pude leer el archivo: no parece una copia de RomTracker.")
        }
    }

    private fun JSONArray.objetos(): List<JSONObject> = List(length()) { getJSONObject(it) }
}
