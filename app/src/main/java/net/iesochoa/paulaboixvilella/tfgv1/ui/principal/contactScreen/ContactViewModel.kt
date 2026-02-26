package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.contactScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.iesochoa.paulaboixvilella.tfgv1.data.db.DatabaseProvider
import net.iesochoa.paulaboixvilella.tfgv1.data.model.ContactEntity
import net.iesochoa.paulaboixvilella.tfgv1.data.repository.ContactRepository
import net.iesochoa.paulaboixvilella.tfgv1.data.repository.EventRepository

class ContactViewModel(application: Application) :
    AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()

    private val eventRepository = EventRepository(
        dao = DatabaseProvider.provideDatabase(application).eventDao(),
        firestore = firestore,
        scope = viewModelScope
    )

    private val repo = ContactRepository(
        dao = DatabaseProvider.provideDatabase(application).contactDao(),
        firestore = firestore,
        eventRepository = eventRepository
    )

    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState: StateFlow<ContactUiState> = _uiState

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                contacts = repo.getLocalContacts()
            )
        }
    }

    fun addContactByEmail(email: String) {
        repo.addContactByEmail(
            email.trim().lowercase(),
            onSuccess = { loadContacts() },
            onError = {
                _uiState.value = _uiState.value.copy(errorMessage = it)
            }
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch {
            repo.deleteContact(contact)
            loadContacts()
        }
    }
}