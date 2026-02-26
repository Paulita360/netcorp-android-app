package net.iesochoa.paulaboixvilella.tfgv1.data.repository

import android.content.Context
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import net.iesochoa.paulaboixvilella.tfgv1.data.dao.MessageDao
import net.iesochoa.paulaboixvilella.tfgv1.data.model.ChatMessage
import net.iesochoa.paulaboixvilella.tfgv1.data.model.MessageEntity
import net.iesochoa.paulaboixvilella.tfgv1.ui.utils.NotificationHelper

class ChatRepository(
    private val dao: MessageDao,
    private val firestore: FirebaseFirestore
) {

    private var messagesListener: ListenerRegistration? = null
    private val notifiedMessageIds = mutableSetOf<String>()

    fun observeMessages(chatId: String): Flow<List<MessageEntity>> =
        dao.observeMessages(chatId)

    fun listenToChat(
        chatId: String,
        currentUserUid: String,
        context: Context
    ) {
        messagesListener?.remove()

        var isFirstLoad = true

        messagesListener = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) return@addSnapshotListener

                val newMessages = mutableListOf<MessageEntity>()

                for (change in snapshot.documentChanges) {

                    if (change.type != DocumentChange.Type.ADDED) continue

                    val doc = change.document
                    val messageId = doc.id

                    val sender = doc.getString("senderId") ?: continue
                    val text = doc.getString("text") ?: continue
                    val timestamp = doc.getLong("timestamp") ?: continue
                    val readBy = doc.get("readBy") as? List<*> ?: emptyList<Any>()

                    val message = MessageEntity(
                        messageId = messageId,
                        chatId = chatId,
                        senderUid = sender,
                        text = text,
                        timestamp = timestamp,
                        isRead = currentUserUid in readBy
                    )

                    newMessages.add(message)

                    if (
                        !isFirstLoad &&
                        sender != currentUserUid &&
                        !notifiedMessageIds.contains(messageId)
                    ) {
                        notifiedMessageIds.add(messageId)

                        NotificationHelper.showNotification(
                            context = context,
                            title = "Nuevo mensaje",
                            message = text,
                            notificationId = messageId.hashCode()
                        )
                    }
                }

                isFirstLoad = false

                CoroutineScope(Dispatchers.IO).launch {
                    dao.insertMessages(newMessages)
                }
            }
    }


    fun sendMessage(chatId: String, message: ChatMessage) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(message)
    }

    fun setTyping(chatId: String, uid: String, isTyping: Boolean) {
        firestore.collection("chats")
            .document(chatId)
            .set(
                mapOf("typing.$uid" to isTyping),
                SetOptions.merge()
            )
    }

    fun removeListener() {
        messagesListener?.remove()
        messagesListener = null
        notifiedMessageIds.clear()
    }
}


