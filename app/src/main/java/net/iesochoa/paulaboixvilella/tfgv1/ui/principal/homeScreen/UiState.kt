package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.homeScreen

import net.iesochoa.paulaboixvilella.tfgv1.data.model.ContactEntity

data class HomeUiState(
    val contacts: List<ContactEntity> = emptyList()
)