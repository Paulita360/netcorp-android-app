package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.CalendarScreen

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import net.iesochoa.paulaboixvilella.tfgv1.data.model.ContactEntity
import net.iesochoa.paulaboixvilella.tfgv1.data.model.EventEntity
import net.iesochoa.paulaboixvilella.tfgv1.data.model.EventType
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.contactScreen.ContactViewModel
import net.iesochoa.paulaboixvilella.tfgv1.ui.utils.DateUtils
import net.iesochoa.paulaboixvilella.tfgv1.ui.utils.ViewModelFactory
import kotlin.collections.find
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    currentUserUid: String
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context.applicationContext as Application) }
    val viewModel: EventViewModel = viewModel(factory = factory)

    val contactViewModel: ContactViewModel = viewModel()
    val contactUiState by contactViewModel.uiState.collectAsState()
    val contacts = contactUiState.contacts

    val calendarUiState by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = viewModel::previousMonth) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "Mes anterior"
                            )
                        }

                        Text(calendarUiState.currentMonth)

                        IconButton(onClick = viewModel::nextMonth) {
                            Icon(
                                Icons.Default.ArrowBackIosNew,
                                contentDescription = "Mes siguiente"
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (calendarUiState.selectedDate != null) {
                        showDialog = true
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar evento")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            CalendarGrid(
                daysInMonth = calendarUiState.daysInMonth,
                events = calendarUiState.events,
                onDateSelected = viewModel::onDateSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            calendarUiState.selectedDate?.let { day ->
                val eventsForDay = calendarUiState.events[day.day] ?: emptyList()

                EventList(
                    events = eventsForDay,
                    contacts = contacts,
                    currentUserUid = currentUserUid,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (showDialog && calendarUiState.selectedDate != null) {
            AddEventDialog(
                onDismiss = { showDialog = false },
                onAdd = { title, description, time, importance, participants ->

                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser == null) {
                        Toast.makeText(context, "No estás autenticado", Toast.LENGTH_SHORT).show()
                        return@AddEventDialog
                    }

                    val (year, month) = viewModel.getCurrentYearMonth()
                    val (hour, minute) = time.split(":").map { it.toInt() }

                    val timestamp = DateUtils.buildTimestamp(
                        year = year,
                        month = month - 1,
                        day = calendarUiState.selectedDate!!.day,
                        hour = hour,
                        minute = minute
                    )

                    viewModel.addEvent(
                        title = title,
                        description = description,
                        timestamp = timestamp,
                        importance = importance,
                        participants = participants,
                        creatorUid = currentUser.uid,
                        type = EventType.EVENT
                    )

                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun CalendarGrid(
    daysInMonth: List<CalendarDay>,
    events: Map<Int, List<EventEntity>>,
    onDateSelected: (CalendarDay) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(daysInMonth) { day ->
            val hasEvents =
                events[day.day]?.isNotEmpty() == true
            CalendarDayCell(day, hasEvents, onDateSelected)
        }
    }
}

@Composable
fun CalendarDayCell(day: CalendarDay, hasEvents: Boolean, onDateSelected: (CalendarDay) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            .clickable { onDateSelected(day) }) {
        Text(
            text = day.day.toString(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 6.dp)
        )
        if (hasEvents) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color.Red, CircleShape)
            )
        }
    }
}

@Composable
fun EventList(
    events: List<EventEntity>,
    contacts: List<ContactEntity>,
    currentUserUid: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(events) { event ->
            EventItem(
                event = event,
                contacts = contacts,
                currentUserUid = currentUserUid
            )
        }
    }
}

@Composable
fun EventItem(
    event: EventEntity,
    contacts: List<ContactEntity>,
    currentUserUid: String?
) {

    val participantNames = event.participants.mapNotNull { uid ->
        when {
            uid == currentUserUid -> "Tú"
            else -> contacts.find { it.firebaseUid == uid }?.name
        }
    }

    val color = when (event.importance) {
        1 -> Color(0xFF4CAF50)   
        2 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(8.dp)
            )
    ) {

        Box(
            modifier = Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(color, RoundedCornerShape(4.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = event.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = DateUtils.formatHour(event.timestamp),
                style = MaterialTheme.typography.labelSmall
            )

            if (participantNames.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Convocados:",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = participantNames.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}