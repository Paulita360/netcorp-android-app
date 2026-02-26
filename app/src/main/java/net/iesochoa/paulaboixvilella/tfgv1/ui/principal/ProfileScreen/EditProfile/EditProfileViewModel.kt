package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.ProfileScreen.EditProfile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import net.iesochoa.paulaboixvilella.tfgv1.data.db.DatabaseProvider
import net.iesochoa.paulaboixvilella.tfgv1.data.model.UserProfileEntity

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val profileDao = DatabaseProvider.provideDatabase(application).userProfileDao()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(EditProfileUiState(isLoading = true))
    val uiState: StateFlow<EditProfileUiState> = _uiState

    init {
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            profileDao.getProfile(uid)?.let { profile ->
                _uiState.value = _uiState.value.copy(
                    name = profile.name,
                    surname = profile.surname,
                    phone = profile.phone,
                    address = profile.address,
                    profileImageUrl = profile.profileImageUrl,
                    isLoading = false
                )
            } ?: run { _uiState.value = _uiState.value.copy(isLoading = false) }
        }
    }

    fun onNameChange(value: String) { _uiState.value = _uiState.value.copy(name = value) }
    fun onSurnameChange(value: String) { _uiState.value = _uiState.value.copy(surname = value) }
    fun onPhoneChange(value: String) { _uiState.value = _uiState.value.copy(phone = value) }
    fun onAddressChange(value: String) { _uiState.value = _uiState.value.copy(address = value) }
    fun onImageSelected(path: String) { _uiState.value = _uiState.value.copy(profileImageUrl = path) }

    fun saveProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val profile = UserProfileEntity(
                uid = uid,
                name = _uiState.value.name,
                surname = _uiState.value.surname,
                phone = _uiState.value.phone,
                address = _uiState.value.address,
                profileImageUrl = _uiState.value.profileImageUrl
            )

            try {
                profileDao.saveProfile(profile)

                firestore.collection("users").document(uid)
                    .set(profile)
                    .addOnSuccessListener {
                        _uiState.value = _uiState.value.copy(isLoading = false, success = true)
                    }
                    .addOnFailureListener { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error al guardar: ${e.message}"
                        )
                    }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al guardar: ${e.message}"
                )
            }
        }
    }
}

