package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.ProfileScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.iesochoa.paulaboixvilella.tfgv1.data.db.DatabaseProvider
import net.iesochoa.paulaboixvilella.tfgv1.data.model.UserProfileEntity

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val profileDao = DatabaseProvider.provideDatabase(application).userProfileDao()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        observeProfile()
        syncProfileFromFirebase()
    }

    fun observeProfile() {
        val uid = auth.currentUser?.uid ?: return

        profileDao.observeProfile(uid)
            .onEach { profile ->
                _uiState.value = ProfileUiState(
                    profile = profile,
                    isLoading = profile == null,
                    errorMessage = if (profile == null) "Perfil no encontrado" else null
                )
            }
            .launchIn(viewModelScope)
    }

    fun syncProfileFromFirebase() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val profile = UserProfileEntity(
                        uid = uid,
                        name = doc.getString("name") ?: "",
                        surname = doc.getString("surname") ?: "",
                        phone = doc.getString("phone") ?: "",
                        address = doc.getString("address") ?: "",
                        rol = doc.getString("rol") ?: "user",
                        profileImageUrl = doc.getString("profileImageUrl") ?: ""
                    )

                    viewModelScope.launch { profileDao.saveProfile(profile) }
                }
            }
            .addOnFailureListener { e ->
                val current = _uiState.value.profile
                _uiState.value = ProfileUiState(
                    profile = current,
                    isLoading = false,
                    errorMessage = "Error al sincronizar Firebase: ${e.message}"
                )
            }
    }

    fun logout() {
        auth.signOut()
    }
}
