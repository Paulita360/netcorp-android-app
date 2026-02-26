package net.iesochoa.paulaboixvilella.tfgv1.ui.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val data = remoteMessage.data
        val senderId = data["senderId"] ?: return
        val messageText = data["text"] ?: return
        val chatId = data["chatId"] ?: return

        if (senderId == currentUid) return

        if (ChatSessionManager.currentChatId == chatId) return

        NotificationHelper.showNotification(
            context = applicationContext,
            title = "Nuevo mensaje",
            message = messageText,
            notificationId = chatId.hashCode()
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .set(mapOf("fcmToken" to token), SetOptions.merge())
    }
}


