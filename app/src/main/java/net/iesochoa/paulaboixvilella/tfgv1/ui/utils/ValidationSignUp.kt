package net.iesochoa.paulaboixvilella.tfgv1.ui.utils

import android.util.Patterns

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPassword(password: String): Boolean {
    val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")
    return regex.matches(password)
}
