package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.AgendaScreen

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import net.iesochoa.paulaboixvilella.tfgv1.data.model.EventType
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.CalendarScreen.EventList
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.CalendarScreen.EventViewModel
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.contactScreen.ContactViewModel
import net.iesochoa.paulaboixvilella.tfgv1.ui.utils.DateUtils
import net.iesochoa.paulaboixvilella.tfgv1.ui.utils.ViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    onBack: () -> Unit,
    currentUserUid: String
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context.applicationContext as Application) }
    val viewModel: EventViewModel = viewModel(factory = factory)

    val contactViewModel: ContactViewModel = viewModel()
    val contactState by contactViewModel.uiState.collectAsState()
    val contacts = contactState.contacts

    val todayEvents by viewModel.eventsToday.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Agenda") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva reunión")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                text = "Eventos de hoy",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(12.dp))

            if (todayEvents.isEmpty()) {
                Text("No hay eventos para hoy")
            } else {
                EventList(
                    events = todayEvents,
                    contacts = contacts,
                    currentUserUid = currentUserUid,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (showDialog) {
            AddMeetingDialog(
                onDismiss = { showDialog = false },
                onAdd = { title, description, date, time, participants, importance ->
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser == null) {
                        Toast.makeText(context, "No estás autenticado", Toast.LENGTH_SHORT).show()
                        return@AddMeetingDialog
                    }

                    val dateParts = date.split("-").mapNotNull { it.toIntOrNull() }
                    val timeParts = time.split(":").mapNotNull { it.toIntOrNull() }
                    if (dateParts.size == 3 && timeParts.size == 2) {
                        val timestamp = DateUtils.buildTimestamp(
                            year = dateParts[0],
                            month = dateParts[1] - 1,
                            day = dateParts[2],
                            hour = timeParts[0],
                            minute = timeParts[1]
                        )

                        viewModel.addEvent(
                            title = title,
                            description = description.ifBlank { "" },
                            timestamp = timestamp,
                            importance = importance,
                            participants = participants,
                            creatorUid = currentUser.uid,
                            type = EventType.MEETING
                        )
                        showDialog = false
                    }
                }
            )
        }
    }
}


