package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.CalendarScreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.iesochoa.paulaboixvilella.tfgv1.data.db.DatabaseProvider
import net.iesochoa.paulaboixvilella.tfgv1.data.model.EventEntity
import net.iesochoa.paulaboixvilella.tfgv1.data.model.EventType
import net.iesochoa.paulaboixvilella.tfgv1.data.repository.EventRepository
import net.iesochoa.paulaboixvilella.tfgv1.ui.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class EventViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = EventRepository(
        dao = DatabaseProvider.provideEventDao(app),
        scope = viewModelScope
    )

    private val calendar = Calendar.getInstance()

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    val allEvents: StateFlow<List<EventEntity>> = repo.observeEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            repo.startListening(currentUser.uid)
        }

        loadMonth()

        viewModelScope.launch {
            repo.observeEvents().collect { events ->
                updateEventsForCurrentMonth(events)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        repo.stopListening()
    }

    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        loadMonth()
        updateEventsForCurrentMonth(allEvents.value)
    }

    fun previousMonth() {
        calendar.add(Calendar.MONTH, -1)
        loadMonth()
        updateEventsForCurrentMonth(allEvents.value)
    }

    private fun loadMonth() {
        val currentMonth = SimpleDateFormat(
            "MMMM yyyy",
            Locale.getDefault()
        ).format(calendar.time)

        val totalDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val days = (1..totalDays).map { CalendarDay(it) }

        _uiState.value = _uiState.value.copy(
            currentMonth = currentMonth,
            daysInMonth = days
        )
    }

    private fun updateEventsForCurrentMonth(events: List<EventEntity>) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val grouped = events
            .filter { DateUtils.isSameMonth(it.timestamp, year, month) }
            .groupBy { DateUtils.getDayOfMonth(it.timestamp) }

        _uiState.value = _uiState.value.copy(events = grouped)
    }

    fun onDateSelected(day: CalendarDay) {
        _uiState.value = _uiState.value.copy(selectedDate = day)
    }

    fun addEvent(
        title: String,
        description: String,
        timestamp: Long,
        importance: Int,
        participants: List<String>,
        creatorUid: String,
        type: EventType
    ) {
        val finalParticipants = if (creatorUid in participants) participants else participants + creatorUid

        val event = EventEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            timestamp = timestamp,
            importance = importance,
            participants = finalParticipants,
            creatorUid = creatorUid,
            type = type
        )

        viewModelScope.launch {
            try {
                Log.d("EventViewModel", "Agregando evento: $event")
                repo.addEvent(event)
                Log.d("EventViewModel", "Evento agregado correctamente: ${event.id}")
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error al agregar evento", e)
                Firebase.crashlytics.recordException(e)
            }
        }
    }

    fun getCurrentYearMonth(): Pair<Int, Int> =
        calendar.get(Calendar.YEAR) to (calendar.get(Calendar.MONTH) + 1)


    val eventsToday: Flow<List<EventEntity>> = allEvents.map { events ->
        events
            .filter { DateUtils.isToday(it.timestamp) }
            .sortedBy { it.timestamp }
    }
}





