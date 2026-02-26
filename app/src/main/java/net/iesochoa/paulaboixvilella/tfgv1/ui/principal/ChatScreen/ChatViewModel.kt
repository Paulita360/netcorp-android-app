package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.ChatScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.iesochoa.paulaboixvilella.tfgv1.data.db.DatabaseProvider
import net.iesochoa.paulaboixvilella.tfgv1.data.model.ChatMessage
import net.iesochoa.paulaboixvilella.tfgv1.data.repository.ChatRepository
import net.iesochoa.paulaboixvilella.tfgv1.ui.utils.ChatSessionManager

class ChatViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val dao = DatabaseProvider
        .provideDatabase(application)
        .messageDao()

    private val repo = ChatRepository(
        dao = dao,
        firestore = FirebaseFirestore.getInstance()
    )

    private val currentUid get() = auth.currentUser!!.uid

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    fun startChat(otherUid: String) {

        val id = listOf(currentUid, otherUid)
            .sorted()
            .joinToString("_")

        _uiState.update { it.copy(chatId = id) }

        repo.listenToChat(id, currentUid, getApplication())

        viewModelScope.launch {
            repo.observeMessages(id).collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }

        FirebaseFirestore.getInstance()
            .collection("chats")
            .document(id)
            .addSnapshotListener { snapshot, _ ->

                val typing = snapshot?.get("typing") as? Map<*, *> ?: return@addSnapshotListener

                val isTyping =
                    typing.filterKeys { it != currentUid }
                        .values
                        .firstOrNull() as? Boolean ?: false

                _uiState.update { it.copy(isOtherTyping = isTyping) }
            }
    }

    fun onMessageChange(text: String) {
        _uiState.update { it.copy(currentMessage = text) }
        setTyping(text.isNotBlank())
    }

    fun sendMessage() {
        val chatId = _uiState.value.chatId ?: return
        val text = _uiState.value.currentMessage
        if (text.isBlank()) return

        val message = ChatMessage(
            senderId = currentUid,
            text = text,
            timestamp = System.currentTimeMillis(),
            readBy = listOf(currentUid)
        )

        viewModelScope.launch {
            repo.sendMessage(chatId, message)
            repo.setTyping(chatId, currentUid, false)
        }

        _uiState.update { it.copy(currentMessage = "") }
    }

    private fun setTyping(isTyping: Boolean) {
        val chatId = _uiState.value.chatId ?: return
        repo.setTyping(chatId, currentUid, isTyping)
    }

    override fun onCleared() {
        super.onCleared()
        ChatSessionManager.currentChatId = null
        repo.removeListener()
    }
}




