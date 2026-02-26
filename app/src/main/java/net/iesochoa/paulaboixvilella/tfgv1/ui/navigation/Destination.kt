package net.iesochoa.paulaboixvilella.tfgv1.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object PlaceHolder : Screen("placeholder")
    object Contacts : Screen("contacts")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Agenda : Screen("agenda")
    object Calendar : Screen("calendar")

    object Chat : Screen("chat/{otherUserUid}") {
        fun withUid(uid: String) = "chat/$uid"
    }

}

