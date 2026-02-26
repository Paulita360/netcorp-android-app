package net.iesochoa.paulaboixvilella.tfgv1.ui.presentation.signUpScreen

data class SignUpUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
)