package com.romtracker.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.romtracker.app.data.AppDatabase
import com.romtracker.app.data.ChecklistItem
import com.romtracker.app.logic.MAX_ITEMS_CHECKLIST
import com.romtracker.app.logic.limpiarTextoItem
import com.romtracker.app.logic.puedeAgregarItem
import com.romtracker.app.logic.puedeBorrarItem
import kotlinx.coroutines.launch

/**
 * Diálogo para editar la checklist de la noche: renombrar (se guarda al tocar "Listo"),
 * borrar (con confirmación; avisa que se pierden sus marcas) y agregar (máximo 6, mínimo 1).
 * [alCerrar] se llama al terminar para que Hoy vuelva a leer los ítems.
 */
@Composable
fun EditarChecklist(db: AppDatabase, alCerrar: () -> Unit) {
    val scope = rememberCoroutineScope()
    var items by remember { mutableStateOf<List<ChecklistItem>?>(null) }
    val textos = remember { mutableStateMapOf<Long, String>() }
    var nuevo by remember { mutableStateOf("") }
    var aBorrar by remember { mutableStateOf<ChecklistItem?>(null) }

    LaunchedEffect(Unit) {
        val lista = db.checklistItemDao().todos()
        lista.forEach { textos[it.id] = it.texto }
        items = lista
    }

    suspend fun recargar() { items = db.checklistItemDao().todos() }

    val lista = items
    AlertDialog(
        onDismissRequest = {}, // se cierra solo con "Listo", para no perder renombres sin querer
        title = { Text("Checklist de la noche") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (lista == null) {
                    Text("Cargando…", color = Paleta.TextoSecundario)
                } else {
                    lista.forEach { item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CampoRom(
                                value = textos[item.id] ?: item.texto,
                                onValueChange = { textos[item.id] = it },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                            )
                            TextButton(
                                enabled = puedeBorrarItem(lista.size),
                                onClick = { aBorrar = item },
                            ) { Text("Borrar") }
                        }
                    }
                    CampoRom(
                        value = nuevo,
                        onValueChange = { nuevo = it },
                        label = { Text("Nuevo ítem") },
                        singleLine = true,
                        enabled = puedeAgregarItem(lista.size),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    BotonRom(
                        "Agregar",
                        modifier = Modifier.fillMaxWidth(),
                        destacado = false,
                        habilitado = puedeAgregarItem(lista.size) && limpiarTextoItem(nuevo).isNotEmpty(),
                    ) {
                        scope.launch {
                            if (db.agregarItemChecklist(nuevo)) nuevo = ""
                            recargar()
                        }
                    }
                    Text(
                        if (puedeAgregarItem(lista.size)) "Hasta $MAX_ITEMS_CHECKLIST ítems. Los cambios valen para todas las noches."
                        else "Llegaste al máximo de $MAX_ITEMS_CHECKLIST ítems.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Paleta.TextoSecundario,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    items?.forEach { item ->
                        val t = textos[item.id]
                        // Un texto vacío no se guarda: el ítem conserva el anterior.
                        if (t != null && limpiarTextoItem(t).isNotEmpty() && limpiarTextoItem(t) != item.texto) {
                            db.renombrarItemChecklist(item.id, t)
                        }
                    }
                    alCerrar()
                }
            }) { Text("Listo") }
        },
    )

    aBorrar?.let { item ->
        AlertDialog(
            onDismissRequest = { aBorrar = null },
            title = { Text("¿Borrar \"${item.texto}\"?") },
            text = { Text("Se quita de la lista y también de lo que marcaste en noches anteriores. No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        db.borrarItemChecklist(item.id)
                        textos.remove(item.id)
                        aBorrar = null
                        recargar()
                    }
                }) { Text("Borrar") }
            },
            dismissButton = { TextButton(onClick = { aBorrar = null }) { Text("Cancelar") } },
        )
    }
}
