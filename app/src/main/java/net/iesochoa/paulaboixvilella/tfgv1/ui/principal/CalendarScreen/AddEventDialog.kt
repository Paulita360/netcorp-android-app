package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.CalendarScreen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.AgendaScreen.ParticipantSelector
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.contactScreen.ContactViewModel

@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, Int, List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") } // Formato HH:MM
    var importance by remember { mutableStateOf(1) }
    var participants by remember { mutableStateOf(emptyList<String>()) }

    val contactViewModel: ContactViewModel = viewModel()
    val uiState by contactViewModel.uiState.collectAsState()
    val contacts = uiState.contacts

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo evento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Hora (HH:MM)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Importancia")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ImportanceChip("Baja", 1, importance) { importance = 1 }
                    ImportanceChip("Media", 2, importance) { importance = 2 }
                    ImportanceChip("Alta", 3, importance) { importance = 3 }
                }

                Spacer(Modifier.height(8.dp))
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
                    if (title.isBlank()) {
                        Toast.makeText(context, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (time.isBlank()) {
                        Toast.makeText(context, "Por favor introduce la hora", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val parts = time.split(":").mapNotNull { it.toIntOrNull() }
                    if (parts.size != 2) {
                        Toast.makeText(context, "Formato de hora inválido", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val (hour, minute) = parts

                    onAdd(title, description, time, importance, participants)
                }
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ImportanceChip(text: String, level: Int, selected: Int, onClick: () -> Unit) {
    val color = when (level) {
        1 -> Color(0xFF4CAF50)
        2 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Surface(
        color = if (selected == level) color.copy(alpha = 0.3f) else Color.Transparent,
        border = BorderStroke(1.dp, color),
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Text(
            text = text,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}


