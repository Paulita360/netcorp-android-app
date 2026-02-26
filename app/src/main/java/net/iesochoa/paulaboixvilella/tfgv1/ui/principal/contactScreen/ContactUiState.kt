package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.contactScreen

import net.iesochoa.paulaboixvilella.tfgv1.data.model.ContactEntity

data class ContactUiState(
    val contacts: List<ContactEntity> = emptyList(),
    val errorMessage: String? = null
)