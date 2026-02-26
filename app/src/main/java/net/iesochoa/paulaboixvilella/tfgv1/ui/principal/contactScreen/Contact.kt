package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.contactScreen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import net.iesochoa.paulaboixvilella.tfgv1.data.model.ContactEntity
import net.iesochoa.paulaboixvilella.tfgv1.ui.navigation.Screen
import net.iesochoa.paulaboixvilella.tfgv1.ui.principal.homeScreen.BottomBar
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    onBack: () -> Unit,
    viewModel: ContactViewModel = viewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var contactToDelete by remember { mutableStateOf<ContactEntity?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Contactos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar contacto")
            }
        },
        bottomBar = {
            BottomBar(
                selected = "contacts",
                onHomeClick = { navController.navigate(Screen.Home.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            uiState.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn {
                items(uiState.contacts) { contact ->
                    ContactItem(
                        contact = contact,
                        onClick = {
                            navController.navigate(Screen.Chat.withUid(contact.firebaseUid))
                        },
                        onDeleteClick = {
                            contactToDelete = contact
                        }
                    )
                    HorizontalDivider()
                }
            }
        }

        if (showDialog) {
            AddContactDialog(
                onDismiss = {
                    viewModel.clearError()
                    showDialog = false
                },
                onAdd = { email ->
                    viewModel.addContactByEmail(email)
                    showDialog = false
                }
            )
        }

        contactToDelete?.let { contact ->
            AlertDialog(
                onDismissRequest = { contactToDelete = null },
                title = { Text("Eliminar contacto") },
                text = {
                    Text("¿Estás seguro de que quieres eliminar a ${contact.name}? También se eliminará de los eventos en los que participe.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteContact(contact)
                            contactToDelete = null
                        }
                    ) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { contactToDelete = null }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

    }
}

@Composable
fun ContactItem(
    contact: ContactEntity,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(contact.name) },
        supportingContent = { Text(contact.email) },
        leadingContent = {
            AsyncImage(
                model = contact.profilePictureUrl.ifBlank {
                    "https://via.placeholder.com/50"
                },
                contentDescription = "Foto de contacto",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        },
        trailingContent = {
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar contacto",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
