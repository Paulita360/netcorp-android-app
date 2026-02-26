package net.iesochoa.paulaboixvilella.tfgv1.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.iesochoa.paulaboixvilella.tfgv1.data.dao.ContactDao
import net.iesochoa.paulaboixvilella.tfgv1.data.model.ContactEntity

class ContactRepository(
    private val dao: ContactDao,
    private val firestore: FirebaseFirestore,
    private val eventRepository: EventRepository
) {

    suspend fun getLocalContacts(): List<ContactEntity> =
        dao.getAllContacts()

    fun addContactByEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onError("El usuario no existe")
                    return@addOnSuccessListener
                }

                val doc = result.documents.first()

                val contact = ContactEntity(
                    firebaseUid = doc.id,
                    name = doc.getString("name") ?: "Usuario",
                    email = doc.getString("email") ?: email,
                    profilePictureUrl = doc.getString("profilePictureUrl") ?: ""
                )

                CoroutineScope(Dispatchers.IO).launch {
                    dao.insertContact(contact)
                    onSuccess()
                }
            }
            .addOnFailureListener {
                onError("Error al buscar usuario")
            }
    }

    suspend fun deleteContact(contact: ContactEntity) {
        dao.deleteContact(contact)
        eventRepository.removeParticipantFromEvents(contact.firebaseUid)
    }
}

