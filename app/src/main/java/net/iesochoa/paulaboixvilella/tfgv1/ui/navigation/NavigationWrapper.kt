package net.iesochoa.paulaboixvilella.tfgv1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import net.iesochoa.paulaboixvilella.tfgv1.ui.presentation.loginScreen.LoginScreen
import net.iesochoa.paulaboixvilella.tfgv1.ui.presentation.placeHolderScreen.PlaceHolderScreen
import net.iesochoa.paulaboixvilella.tfgv1.ui.presentation.signUpScreen.SignUpScreen
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.CalendarScreen.CalendarScreen
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.ChatScreen.ChatScreen
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.AgendaScreen.AgendaScreen
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.ProfileScreen.EditProfile.EditProfileScreen
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.ProfileScreen.ProfileScreen
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.contactScreen.ContactViewModel
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.contactScreen.ContactsScreen
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.homeScreen.HomeScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val auth = FirebaseAuth.getInstance()
    val currentUserUid = auth.currentUser?.uid.orEmpty()


    NavHost(
        navController = navController,
        startDestination = Screen.PlaceHolder.route
    ) {

        // PLACEHOLDER
        composable(Screen.PlaceHolder.route) {
            PlaceHolderScreen(
                navigateToLogin = { navController.navigate(Screen.Login.route) },
                navigateToSignUp = { navController.navigate(Screen.SignUp.route) }
            )
        }

        // LOGIN
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.PlaceHolder.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // SIGN UP
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.PlaceHolder.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // HOME
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToContacts = { navController.navigate(Screen.Contacts.route) },
                onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                onNavigateToAgenda = { navController.navigate(Screen.Agenda.route) }
            )
        }

        // CONTACTS
        composable(Screen.Contacts.route) {
            val contactViewModel: ContactViewModel = viewModel()
            ContactsScreen(
                onBack = { navController.popBackStack() },
                viewModel = contactViewModel,
                navController = navController
            )
        }

        // PROFILE
        composable(Screen.Profile.route) {
            ProfileScreen(
                onEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onLogout = {
                    navController.navigate(Screen.PlaceHolder.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // EDIT PROFILE
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // MEETINGS
        composable(Screen.Agenda.route) {

            AgendaScreen(
                onBack = { navController.popBackStack() },
                currentUserUid = currentUserUid
            )
        }

        // CALENDAR
        composable(Screen.Calendar.route) {

            CalendarScreen(
                onBack = { navController.popBackStack() },
                currentUserUid = currentUserUid
            )
        }

        // CHAT
        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("otherUserUid") {
                type = NavType.StringType
            })
        ) { backStackEntry ->

            val otherUserUid =
                backStackEntry.arguments?.getString("otherUserUid")
                    ?: return@composable

            ChatScreen(
                otherUserUid = otherUserUid,
                onBack = { navController.popBackStack() }
            )
        }
    }
}


