package net.iesochoa.paulaboixvilella.tfgv1.ui.presentation.loginScreen

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import net.iesochoa.paulaboixvilella.tfgv1.ui.utils.isValidEmail

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {

        if (!isValidEmail(email)) {
            _uiState.value = LoginUiState(
                errorMessage = "Email no válido"
            )
            return
        }

        if (password.isBlank()) {
            _uiState.value = LoginUiState(
                errorMessage = "La contraseña no puede estar vacía"
            )
            return
        }

        _uiState.value = LoginUiState(isLoading = true)

        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = LoginUiState(success = true)
                } else {
                    _uiState.value = LoginUiState(
                        errorMessage = mapFirebaseLoginError(task.exception)
                    )
                }
            }
    }

    private fun mapFirebaseLoginError(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthInvalidUserException ->
                "No existe una cuenta con este email"
            is FirebaseAuthInvalidCredentialsException ->
                "Contraseña incorrecta"
            else ->
                "Error al iniciar sesión"
        }
    }
}



