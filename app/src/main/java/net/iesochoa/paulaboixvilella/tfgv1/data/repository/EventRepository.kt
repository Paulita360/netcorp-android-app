package net.iesochoa.paulaboixvilella.tfgv1.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import net.iesochoa.paulaboixvilella.tfgv1.data.dao.EventDao
import net.iesochoa.paulaboixvilella.tfgv1.data.model.EventEntity

class EventRepository(
    private val dao: EventDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val scope: CoroutineScope
) {

    private val eventsRef = firestore.collection("events")
    private var registration: ListenerRegistration? = null

    fun startListening(currentUserId: String) {
        if (registration != null) return

        registration = eventsRef
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                scope.launch(Dispatchers.IO) {
                    snapshot.documentChanges.forEach { change ->
                        val event = runCatching {
                            change.document.toObject(EventEntity::class.java)
                        }.getOrNull() ?: return@forEach

                        try {
                            when (change.type) {
                                DocumentChange.Type.ADDED,
                                DocumentChange.Type.MODIFIED -> dao.insertEvent(event)
                                DocumentChange.Type.REMOVED -> dao.deleteById(event.id)
                            }
                        } catch (e: Exception) {
                            Log.e("EventRepository", "Error Room al procesar evento", e)
                        }
                    }
                }
            }
    }

    fun stopListening() {
        registration?.remove()
        registration = null
    }

    suspend fun addEvent(event: EventEntity) {
        eventsRef.document(event.id).set(event).await()
        dao.insertEvent(event)
    }

    fun observeEvents(): Flow<List<EventEntity>> =
        dao.observeAllEvents()

    suspend fun removeParticipantFromEvents(uid: String) {
        try {
            val snapshot = eventsRef
                .whereArrayContains("participants", uid)
                .get()
                .await()

            for (document in snapshot.documents) {
                document.reference.update("participants", FieldValue.arrayRemove(uid)).await()

                val updatedDoc = document.reference.get().await()
                val updatedEvent = updatedDoc.toObject(EventEntity::class.java)

                if (updatedEvent != null) {
                    dao.insertEvent(updatedEvent)
                }
            }
        } catch (e: Exception) {
            Log.e("EventRepository", "Error eliminando participante", e)
        }
    }
}




