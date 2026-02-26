package net.iesochoa.paulaboixvilella.tfgv1.ui.presentation.signUpScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.iesochoa.paulaboixvilella.tfgv1.data.db.DatabaseProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import net.iesochoa.paulaboixvilella.tfgv1.data.model.UserProfileEntity

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = DatabaseProvider.provideDatabase(application)
    private val profileDao = db.userProfileDao()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun signUp(
        email: String,
        password: String,
        name: String,
        surname: String,
        phone: String,
        address: String
    ) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        val normalizedEmail = email.trim().lowercase()

        auth.createUserWithEmailAndPassword(normalizedEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val uid = task.result?.user?.uid
                    if (uid == null) {
                        _uiState.value = SignUpUiState(
                            isLoading = false,
                            errorMessage = "No se pudo obtener el UID del usuario"
                        )
                        return@addOnCompleteListener
                    }

                    // 1️⃣ Guardar en ROOM
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            val profile = UserProfileEntity(
                                uid = uid,
                                name = name,
                                surname = surname,
                                phone = phone,
                                address = address,
                                profileImageUrl = ""
                            )
                            profileDao.saveProfile(profile)
                        }
                    }

                    // 2️⃣ Guardar en FIRESTORE (IMPORTANTE)
                    val userData = mapOf(
                        "uid" to uid,
                        "email" to normalizedEmail,
                        "name" to name,
                        "surname" to surname,
                        "phone" to phone,
                        "address" to address,
                        "profilePictureUrl" to ""
                    )

                    firestore.collection("users")
                        .document(uid)
                        .set(userData)
                        .addOnSuccessListener {
                            _uiState.value = SignUpUiState(
                                isLoading = false,
                                success = true
                            )
                        }
                        .addOnFailureListener { e ->
                            _uiState.value = SignUpUiState(
                                isLoading = false,
                                errorMessage = "Error al guardar en Firestore: ${e.message}"
                            )
                        }

                } else {
                    _uiState.value = SignUpUiState(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "Error al registrarse"
                    )
                }
            }
    }
}



