package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.AgendaScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.iesochoa.paulaboixvilella.tfgv1.data.model.ContactEntity
import kotlin.collections.forEach

@Composable
fun ParticipantSelector(
    contacts: List<ContactEntity>,
    selected: List<String>,
    onSelectionChange: (List<String>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        contacts.forEach { contact ->
            val isSelected = contact.firebaseUid in selected

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val updated = if (isSelected) {
                            selected - contact.firebaseUid
                        } else {
                            selected + contact.firebaseUid
                        }
                        onSelectionChange(updated)
                    }
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = {
                        val updated = if (isSelected) {
                            selected - contact.firebaseUid
                        } else {
                            selected + contact.firebaseUid
                        }
                        onSelectionChange(updated)
                    }
                )
                Text(contact.name)
            }
        }
    }
}