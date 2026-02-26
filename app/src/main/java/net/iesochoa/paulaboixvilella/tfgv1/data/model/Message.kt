package net.iesochoa.paulaboixvilella.tfgv1.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    indices = [Index("chatId")]
)
data class MessageEntity(
    @PrimaryKey val messageId: String,
    val chatId: String,
    val senderUid: String,
    val text: String,
    val timestamp: Long,
    val isRead: Boolean
)

