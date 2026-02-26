package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.ChatScreen

import net.iesochoa.paulaboixvilella.tfgv1.data.model.MessageEntity

data class ChatUiState(
    val chatId: String? = null,
    val messages: List<MessageEntity> = emptyList(),
    val currentMessage: String = "",
    val isOtherTyping: Boolean = false
)


