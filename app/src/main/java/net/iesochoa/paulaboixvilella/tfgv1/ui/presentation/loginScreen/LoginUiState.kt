package net.iesochoa.paulaboixvilella.tfgv1.ui.presentation.loginScreen

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
)

