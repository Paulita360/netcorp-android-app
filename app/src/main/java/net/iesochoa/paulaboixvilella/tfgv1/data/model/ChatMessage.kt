package net.iesochoa.paulaboixvilella.tfgv1.data.model

data class ChatMessage(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val readBy: List<String> = emptyList()
)

