package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.CalendarScreen

import net.iesochoa.paulaboixvilella.tfgv1.data.model.EventEntity

data class CalendarUiState(
    val currentMonth: String = "",
    val daysInMonth: List<CalendarDay> = emptyList(),
    val selectedDate: CalendarDay? = null,
    val events: Map<Int, List<EventEntity>> = emptyMap()
)

data class CalendarDay(
    val day: Int,
    val isCurrentMonth: Boolean = true
)


