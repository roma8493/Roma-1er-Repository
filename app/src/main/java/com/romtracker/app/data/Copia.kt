package com.romtracker.app.data

import com.romtracker.app.logic.DatosConvertibles
import com.romtracker.app.logic.FijosViejos
import com.romtracker.app.logic.MAX_ITEMS_CHECKLIST
import com.romtracker.app.logic.OpcionesDesencadenante
import com.romtracker.app.logic.convertirFijos
import com.romtracker.app.logic.itemsNeutros
import com.romtracker.app.logic.itemsPorDefecto
import com.romtracker.app.logic.lunesDe
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
    val automatismos: List<Automatismo> = emptyList(),
    val revisiones: List<Revision> = emptyList(),
    val antojos: List<Antojo> = emptyList(),
    val desencadenantes: List<Desencadenante> = emptyList(),
)

/** El archivo no es una copia válida de RomTracker. [message] es apto para mostrar al usuario. */
class CopiaInvalida(message: String) : Exception(message)

object CopiaJson {
    /** 1 = 0.2 (sin agua). 2 = agrega "aguas". 3 = agrega la meta de agua en el perfil. 4 = agrega "animos". 5 = agrega "habitosPropios". 6 = agrega "checklistItems" y "checklistMarcas". 7 = agrega "tema" en el perfil. 8 = agrega "versionMinima" en el perfil y en cada hábito propio. 9 = agrega "automatismos". 10 = agrega "revisiones". 11 = agrega "antojos". 12 = agrega "desencadenantes". 13 = agrega "icono" en cada hábito propio. 14 = Gym y Comida son hábitos propios (las copias 1–13 con GYM/COMIDA se convierten al leer). 15 = agrega "aguaActiva" en el perfil (las anteriores la activan si tenían agua). 16 = agrega "planSiEntonces" en cada hábito propio. Las versiones anteriores se siguen leyendo. */
    const val VERSION = 16
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
            put("versionMinima", c.perfil.versionMinima)
            put("aguaActiva", c.perfil.aguaActiva)
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
                        .put("versionMinima", it.versionMinima).put("icono", it.icono).put("planSiEntonces", it.planSiEntonces)
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
        raiz.put("automatismos", JSONArray().also { a ->
            c.automatismos.forEach {
                a.put(
                    JSONObject().put("habito", it.habito).put("fecha", it.fecha)
                        .put("r1", it.r1).put("r2", it.r2).put("r3", it.r3).put("r4", it.r4)
                )
            }
        })
        raiz.put("revisiones", JSONArray().also { a ->
            c.revisiones.forEach {
                a.put(JSONObject().put("semana", it.semana).put("barrera", it.barrera).put("cambio", it.cambio))
            }
        })
        raiz.put("antojos", JSONArray().also { a ->
            c.antojos.forEach { a.put(JSONObject().put("id", it.id).put("habito", it.habito).put("fecha", it.fecha)) }
        })
        raiz.put("desencadenantes", JSONArray().also { a ->
            c.desencadenantes.forEach {
                // Las opciones sin elegir (null) simplemente no se escriben.
                a.put(JSONObject().put("id", it.id).put("habito", it.habito).put("fecha", it.fecha)
                    .put("lugar", it.lugar).put("momento", it.momento).put("emocion", it.emocion).put("compania", it.compania))
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
            // Las copias anteriores a la versión 14 pueden traer Gym y Comida como hábitos fijos: se convierten al final.
            val viejo = version < 14
            val fijosViejos = FijosViejos.TODOS.map { it.clave }.toSet()
            val p = raiz.getJSONObject("perfil")
            val perfil = Perfil(
                nombre = p.getString("nombre"),
                horaSalidaMinutos = p.getInt("horaSalidaMinutos"),
                claveAncla = p.getString("claveAncla"),
                porQue = p.getString("porQue"),
                // Las copias anteriores a la versión 3 no traen la meta: se usa la de por defecto.
                metaAgua = p.optInt("metaAgua", 8),
            // Las copias anteriores a la versión 7 no traen el tema: se usa el claro.
            tema = TemaPerfil.deNombre(p.optString("tema", "CLARO")),
                // Las copias anteriores a la versión 8 no traen la versión mínima: queda vacía.
                versionMinima = p.optString("versionMinima", ""),
            )
            val habitos = raiz.getJSONArray("habitos").objetos().map {
                val tipo = it.getString("tipo")
                if (tipo != TipoHabito.AGUA && !(viejo && tipo in fijosViejos)) {
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
                    it.getString("porQue"), tipo, it.getLong("fechaInicio"), it.optString("versionMinima", ""),
                    // Las copias anteriores a la versión 13 no traen el icono: queda vacío (emoji por defecto).
                    it.optString("icono", ""),
                    // Las copias anteriores a la versión 16 no traen el plan "Si… entonces…": queda vacío.
                    it.optString("planSiEntonces", ""),
                )
            } ?: emptyList()
            val clavesPropias = propios.map { claveRegistroPropio(it.id) }.toSet()
            val registros = raiz.getJSONArray("registros").objetos().map {
                val h = it.getString("habito")
                if (h !in clavesPropias && !(viejo && h in fijosViejos)) {
                    throw CopiaInvalida("La copia tiene un registro desconocido.")
                }
                Registro(h, it.getLong("fecha"), it.getBoolean("cumplido"))
            }
            // Las copias anteriores a la versión 15 no traen "aguaActiva": queda activa si tenían agua.
            val aguaActiva = if (p.has("aguaActiva")) p.getBoolean("aguaActiva")
            else habitos.any { it.tipo == TipoHabito.AGUA } || (raiz.optJSONArray("aguas")?.length() ?: 0) > 0
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
            } ?: if (raiz.getJSONArray("checklists").length() > 0) itemsPorDefecto() else itemsNeutros()
            if (items.size > MAX_ITEMS_CHECKLIST) throw CopiaInvalida("La copia tiene demasiados ítems de checklist.")
            val idsItems = items.map { it.id }.toSet()
            val marcas = raiz.optJSONArray("checklistMarcas")?.objetos()?.map {
                val id = it.getLong("itemId")
                if (id !in idsItems) throw CopiaInvalida("La copia tiene una marca de checklist desconocida.")
                ChecklistMarca(it.getLong("paraFecha"), id, it.getBoolean("hecho"))
            } ?: if (itemsJson == null) checklists.flatMap { marcasDeChecklistVieja(it) } else emptyList()
            // Las copias anteriores a la versión 9 no traen "automatismos": se leen como lista vacía.
            val automatismos = raiz.optJSONArray("automatismos")?.objetos()?.map {
                val h = it.getString("habito")
                if (h != TipoHabito.AGUA && h !in clavesPropias && !(viejo && h in fijosViejos)) {
                    throw CopiaInvalida("La copia tiene un check de automatismo de un hábito desconocido.")
                }
                val r = listOf(it.getInt("r1"), it.getInt("r2"), it.getInt("r3"), it.getInt("r4"))
                if (r.any { v -> v !in 1..5 }) throw CopiaInvalida("La copia tiene una respuesta de automatismo fuera de rango.")
                Automatismo(h, it.getLong("fecha"), r[0], r[1], r[2], r[3])
            } ?: emptyList()
            // Las copias anteriores a la versión 10 no traen "revisiones": se leen como lista vacía.
            val revisiones = raiz.optJSONArray("revisiones")?.objetos()?.map {
                val semana = it.getLong("semana")
                if (semana != lunesDe(semana)) throw CopiaInvalida("La copia tiene una revisión semanal con una fecha inválida.")
                Revision(semana, it.optString("barrera", ""), it.optString("cambio", ""))
            } ?: emptyList()
            // Las copias anteriores a la versión 11 no traen "antojos": se leen como lista vacía.
            val clavesDejar = propios.filter { it.tipo == TipoPropio.DEJAR }.map { claveRegistroPropio(it.id) }.toSet()
            val antojos = raiz.optJSONArray("antojos")?.objetos()?.map {
                val h = it.getString("habito")
                if (h !in clavesDejar) throw CopiaInvalida("La copia tiene un antojo de un hábito desconocido.")
                Antojo(it.getLong("id"), h, it.getLong("fecha"))
            } ?: emptyList()
            // Las copias anteriores a la versión 12 no traen "desencadenantes": se leen como lista vacía.
            fun opcion(o: JSONObject, campo: String, validas: List<String>): String? {
                if (o.isNull(campo)) return null
                val v = o.getString(campo)
                if (v !in validas) throw CopiaInvalida("La copia tiene un desencadenante con una opción desconocida.")
                return v
            }
            val desencadenantes = raiz.optJSONArray("desencadenantes")?.objetos()?.map {
                val h = it.getString("habito")
                if (h !in clavesDejar) throw CopiaInvalida("La copia tiene un desencadenante de un hábito desconocido.")
                Desencadenante(
                    it.getLong("id"), h, it.getLong("fecha"),
                    opcion(it, "lugar", OpcionesDesencadenante.LUGAR),
                    opcion(it, "momento", OpcionesDesencadenante.MOMENTO),
                    opcion(it, "emocion", OpcionesDesencadenante.EMOCION),
                    opcion(it, "compania", OpcionesDesencadenante.COMPANIA),
                )
            } ?: emptyList()
            val conv = if (viejo) {
                convertirFijos(
                    DatosConvertibles(habitos, propios, registros, automatismos),
                    perfil.claveAncla, perfil.porQue, perfil.versionMinima,
                )
            } else DatosConvertibles(habitos, propios, registros, automatismos)
            return Copia(perfil.copy(aguaActiva = aguaActiva), conv.habitos, conv.registros, checklists, aguas, animos, conv.propios, items, marcas, conv.automatismos, revisiones, antojos, desencadenantes)
        } catch (e: CopiaInvalida) {
            throw e
        } catch (e: Exception) {
            // JSON roto, campos que faltan o con tipo equivocado
            throw CopiaInvalida("No pude leer el archivo: no parece una copia de RomTracker.")
        }
    }

    private fun JSONArray.objetos(): List<JSONObject> = List(length()) { getJSONObject(it) }
}
