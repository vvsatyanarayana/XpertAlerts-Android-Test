
package com.aquila.pocxpertalerts.ui.screens
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CreateForwardOrange = Color(0xFFED741C)

data class ForwardApplicationOption(
    val id: String,
    val name: String
)

data class ForwardUserOption(
    val id: String,
    val name: String,
    val email: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardAlertDetailsScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    emailFromApi: String? = null,
    applications: List<ForwardApplicationOption> = emptyList(),
    users: List<ForwardUserOption> = emptyList(),
    onApplicationSelected: (ForwardApplicationOption) -> Unit = {},
    onUserSelected: (ForwardUserOption) -> Unit = {},
    onMapClick: (
        applicationId: String,
        userId: String,
        email: String,
        sortOrder: String,
        active: Boolean
    ) -> Unit = { _, _, _, _, _ -> onSaveClick() }
) {
    var selectedApplication by remember {
        mutableStateOf<ForwardApplicationOption?>(null)
    }
    var selectedUser by remember {
        mutableStateOf<ForwardUserOption?>(null)
    }
    var email by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    var applicationMenuOpen by remember { mutableStateOf(false) }
    var userDialogOpen by remember { mutableStateOf(false) }
    var userSearch by remember { mutableStateOf("") }
    var showValidation by remember { mutableStateOf(false) }

    LaunchedEffect(emailFromApi) {
        if (emailFromApi != null) {
            email = emailFromApi
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Create Forward Alert",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CreateForwardOrange
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Application dropdown
            Text(
                "Application",
                fontWeight = FontWeight.Medium
            )

            Column {
                OutlinedTextField(
                    value = selectedApplication?.name.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    enabled = true,
                    placeholder = {
                        Text("- Select Application -")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                applicationMenuOpen = !applicationMenuOpen
                            }
                        ) {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Choose application"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            applicationMenuOpen = true
                        },
                    shape = RoundedCornerShape(12.dp)
                )

                if (applicationMenuOpen) {
                    AlertDialog(
                        onDismissRequest = {
                            applicationMenuOpen = false
                        },
                        title = { Text("Select Application") },
                        text = {
                            Column {
                                if (applications.isEmpty()) {
                                    Text("No applications available")
                                } else {
                                    applications.forEach { application ->
                                        TextButton(
                                            onClick = {
                                                selectedApplication =
                                                    application
                                                selectedUser = null
                                                email = ""
                                                applicationMenuOpen = false
                                                onApplicationSelected(
                                                    application
                                                )
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                application.name,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    applicationMenuOpen = false
                                }
                            ) {
                                Text("Close")
                            }
                        }
                    )
                }
            }

            // User picker
            Text(
                "User",
                fontWeight = FontWeight.Medium
            )

            OutlinedTextField(
                value = selectedUser?.name.orEmpty(),
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("- Select User -") },
                trailingIcon = {
                    IconButton(onClick = { userDialogOpen = true }) {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Choose user"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        userDialogOpen = true
                    },
                shape = RoundedCornerShape(12.dp)
            )

            // Email
            Text(
                "Email Address",
                fontWeight = FontWeight.Medium
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Sort order
            Text(
                "Sort Order",
                fontWeight = FontWeight.Medium
            )

            OutlinedTextField(
                value = sortOrder,
                onValueChange = { value ->
                    if (value.all { it.isDigit() }) {
                        sortOrder = value
                    }
                },
                placeholder = { Text("Sort Order") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Activate
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Activate",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        if (active) "Yes" else "No",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Switch(
                    checked = active,
                    onCheckedChange = { active = it }
                )
            }

            if (showValidation) {
                Text(
                    text = "Select an application and user, and enter a valid email address.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(4.dp))


            Button(
                onClick = {
                    val application = selectedApplication
                    val user = selectedUser
                    val validEmail = android.util.Patterns.EMAIL_ADDRESS
                        .matcher(email.trim())
                        .matches()

                    when {
                        application == null -> {
                            showValidation = true
                        }

                        user == null -> {
                            showValidation = true
                        }

                        !validEmail -> {
                            showValidation = true
                        }

                        else -> {
                            showValidation = false

                            android.util.Log.d(
                                "ForwardAlerts",
                                "Map clicked: applicationId=${application.id}, " +
                                        "mappedUserId=${user.id}, email=$email"
                            )

                            onMapClick(
                                application.id,
                                user.id,
                                email.trim(),
                                sortOrder,
                                active
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CreateForwardOrange
                )
            ) {
                Text(
                    text = "Map",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }

    // Searchable user-selection dialog
    if (userDialogOpen) {
        val filteredUsers = users.filter {
            it.name.contains(userSearch, ignoreCase = true) ||
                    it.email.contains(userSearch, ignoreCase = true)
        }

        AlertDialog(
            onDismissRequest = {
                userDialogOpen = false
                userSearch = ""
            },
            title = { Text("Select User") },
            text = {
                Column {
                    OutlinedTextField(
                        value = userSearch,
                        onValueChange = { userSearch = it },
                        placeholder = { Text("Search user") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))
                    Divider()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (filteredUsers.isEmpty()) {
                            Text(
                                "No users available",
                                modifier = Modifier.padding(12.dp)
                            )
                        } else {
                            filteredUsers.forEach { user ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedUser = user
                                            email = user.email
                                            userDialogOpen = false
                                            userSearch = ""
                                            showValidation = false
                                            onUserSelected(user)
                                        }
                                        .padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        user.name,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        user.email,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                Divider()
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        userDialogOpen = false
                        userSearch = ""
                    }
                ) {
                    Text("Close", color = CreateForwardOrange)
                }
            }
        )
    }
}
