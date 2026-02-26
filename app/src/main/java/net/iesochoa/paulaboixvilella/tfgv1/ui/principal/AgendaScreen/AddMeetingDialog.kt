package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.AgendaScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.CalendarScreen.ImportanceChip
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.contactScreen.ContactViewModel

@Composable
fun AddMeetingDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, description: String, date: String, time: String, participants: List<String>, importance: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var participants by remember { mutableStateOf(emptyList<String>()) }
    var importance by remember { mutableStateOf(2) }

    val contactViewModel: ContactViewModel = viewModel()
    val uiState by contactViewModel.uiState.collectAsState()
    val contacts = uiState.contacts

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva reunión") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Fecha (YYYY-MM-DD)") })
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Hora (HH:MM)") })

                Text("Importancia")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ImportanceChip("Baja", 1, importance) { importance = 1 }
                    ImportanceChip("Media", 2, importance) { importance = 2 }
                    ImportanceChip("Alta", 3, importance) { importance = 3 }
                }

                Text("Participantes")
                ParticipantSelector(
                    contacts = contacts,
                    selected = participants,
                    onSelectionChange = { participants = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, description, date, time, participants, importance)
                    }
                }
            ) { Text("Crear") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}