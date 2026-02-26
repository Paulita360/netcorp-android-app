package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.ProfileScreen.EditProfile

data class EditProfileUiState(
    val name: String = "",
    val surname: String = "",
    val phone: String = "",
    val address: String = "",
    val profileImageUrl: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
)

