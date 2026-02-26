package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.ProfileScreen

import net.iesochoa.paulaboixvilella.tfgv1.data.model.UserProfileEntity

data class ProfileUiState(
    val profile: UserProfileEntity? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

