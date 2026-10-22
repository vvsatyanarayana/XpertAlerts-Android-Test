
package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
private val ApplicationPurple = Color(0xFF673AB7)
private val DropdownBackground = Color(0xFFF3EDF7)
private val DropdownTextColor = Color(0xFF29252F)

data class ForwardApplicationOption(
    val id: String,
    val name: String
)

data class ForwardUserOption(
    val id: String,
    val name: String,
    val email: String
)

data class ForwardAlertEditDetails(
    val forwardAlertId: String,
    val mappedUserId: String,
    val applicationId: String,
    val email: String = "",
    val sortOrder: String = "0",
    val active: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardAlertDetailsScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    emailFromApi: String? = null,
    isSaving: Boolean = false,
    saveMessage: String? = null,
    saveSucceeded: Boolean = false,
    applications: List<ForwardApplicationOption> = emptyList(),
    users: List<ForwardUserOption> = emptyList(),
    editDetails: ForwardAlertEditDetails? = null,
    onApplicationSelected: (ForwardApplicationOption) -> Unit = {},
    onUserSelected: (ForwardUserOption) -> Unit = {},
    onMapClick: (
        applicationId: String,
        mappedUserId: String,
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
    var sortOrder by remember { mutableStateOf("0") }
    var active by remember { mutableStateOf(false) }

    var applicationMenuOpen by remember { mutableStateOf(false) }
    var userMenuOpen by remember { mutableStateOf(false) }
    var userSearch by remember { mutableStateOf("") }
    var showValidation by remember { mutableStateOf(false) }

    // Restore values when editing an existing mapping.
    LaunchedEffect(editDetails, applications) {
        val details = editDetails

        if (details != null && applications.isNotEmpty()) {
            selectedApplication = applications.firstOrNull {
                it.id == details.applicationId
            }

            sortOrder = details.sortOrder
            active = details.active

            if (details.email.isNotBlank()) {
                email = details.email
            }

            selectedApplication?.let(onApplicationSelected)
        }
    }

    LaunchedEffect(editDetails, users) {
        val details = editDetails

        if (details != null && users.isNotEmpty()) {
            selectedUser = users.firstOrNull {
                it.id == details.mappedUserId
            }

            if (details.email.isNotBlank()) {
                email = details.email
            }

            sortOrder = details.sortOrder
            active = details.active
        }
    }

    // Accept only valid email values from the API.
    LaunchedEffect(emailFromApi) {
        val apiEmail = emailFromApi?.trim().orEmpty()

        if (
            apiEmail.isNotBlank() &&
            android.util.Patterns.EMAIL_ADDRESS
                .matcher(apiEmail)
                .matches()
        ) {
            email = apiEmail
            showValidation = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (editDetails != null) {
                            "Edit Forward Alert"
                        } else {
                            "Create Forward Alert"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        enabled = !isSaving
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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

            // APPLICATION
            Text(
                text = "Application",
                fontWeight = FontWeight.Medium
            )

            ExposedDropdownMenuBox(
                expanded = applicationMenuOpen,
                onExpandedChange = { expanded ->
                    if (!isSaving) {
                        applicationMenuOpen = expanded
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedApplication?.name.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    enabled = !isSaving,
                    label = {
                        Text("Application")
                    },
                    placeholder = {
                        Text("Select Application")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = applicationMenuOpen
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(50.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ApplicationPurple,
                        unfocusedBorderColor = Color(0xFF999999),
                        focusedLabelColor = ApplicationPurple,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White
                    )
                )

                ExposedDropdownMenu(
                    expanded = applicationMenuOpen,
                    onDismissRequest = {
                        applicationMenuOpen = false
                    },
                    modifier = Modifier.background(DropdownBackground)
                ) {
                    if (applications.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text("No applications available")
                            },
                            onClick = {
                                applicationMenuOpen = false
                            }
                        )
                    } else {
                        applications.forEach { application ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = application.name,
                                        color = DropdownTextColor
                                    )
                                },
                                onClick = {
                                    selectedApplication = application
                                    selectedUser = null
                                    email = ""
                                    userSearch = ""
                                    sortOrder = "0"
                                    showValidation = false
                                    applicationMenuOpen = false
                                    userMenuOpen = false

                                    onApplicationSelected(application)
                                }
                            )
                        }
                    }
                }
            }

            // USER
            Text(
                text = "User",
                fontWeight = FontWeight.Medium
            )

            ExposedDropdownMenuBox(
                expanded = userMenuOpen,
                onExpandedChange = { expanded ->
                    if (!isSaving) {
                        userMenuOpen = expanded
                        if (!expanded) userSearch = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedUser?.name.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    enabled = !isSaving,
                    label = {
                        Text("User")
                    },
                    placeholder = {
                        Text("Select User")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = userMenuOpen
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(50.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ApplicationPurple,
                        unfocusedBorderColor = Color(0xFF999999),
                        focusedLabelColor = ApplicationPurple,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White
                    )
                )

                ExposedDropdownMenu(
                    expanded = userMenuOpen,
                    onDismissRequest = {
                        userMenuOpen = false
                        userSearch = ""
                    },
                    modifier = Modifier.background(DropdownBackground)
                ) {
                    // Search field inside the dropdown.
                    OutlinedTextField(
                        value = userSearch,
                        onValueChange = {
                            userSearch = it
                        },
                        placeholder = {
                            Text("Search user")
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(50.dp)
                    )

                    val filteredUsers = users.filter { user ->
                        user.name.contains(
                            userSearch,
                            ignoreCase = true
                        ) || user.email.contains(
                            userSearch,
                            ignoreCase = true
                        )
                    }

                    if (filteredUsers.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (users.isEmpty()) {
                                        "No users available"
                                    } else {
                                        "No matching users"
                                    }
                                )
                            },
                            onClick = {
                                userMenuOpen = false
                                userSearch = ""
                            }
                        )
                    } else {
                        filteredUsers.forEach { user ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = user.name,
                                            color = DropdownTextColor,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = user.email,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                },
                                onClick = {
                                    selectedUser = user

                                    email = user.email.takeIf {
                                        android.util.Patterns.EMAIL_ADDRESS
                                            .matcher(it.trim())
                                            .matches()
                                    }.orEmpty()

                                    userMenuOpen = false
                                    userSearch = ""
                                    showValidation = false

                                    onUserSelected(user)
                                }
                            )
                        }
                    }
                }
            }

            // EMAIL ADDRESS
            Text(
                text = "Email Address",
                fontWeight = FontWeight.Medium
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    showValidation = false
                },
                enabled = !isSaving,
                placeholder = {
                    Text("Email Address")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(50.dp)
            )

            // SORT ORDER
            Text(
                text = "Sort Order",
                fontWeight = FontWeight.Medium
            )

            OutlinedTextField(
                value = sortOrder,
                onValueChange = { value ->
                    if (
                        value.length <= 3 &&
                        value.all { it.isDigit() }
                    ) {
                        sortOrder = value
                    }
                },
                enabled = !isSaving,
                placeholder = {
                    Text("Sort Order")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(50.dp)
            )

            // ACTIVATE
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Activate",
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = if (active) "Yes" else "No",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Switch(
                    checked = active,
                    onCheckedChange = {
                        active = it
                    },
                    enabled = !isSaving
                )
            }

            // VALIDATION
            if (showValidation) {
                Text(
                    text = "Please select an application and user, " +
                            "enter a valid email, and provide a sort order.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            // MAP BUTTON
            Button(
                onClick = {
                    val validEmail =
                        android.util.Patterns.EMAIL_ADDRESS
                            .matcher(email.trim())
                            .matches()

                    if (
                        selectedApplication == null ||
                        selectedUser == null ||
                        !validEmail ||
                        sortOrder.isBlank()
                    ) {
                        showValidation = true
                    } else if (!isSaving) {
                        showValidation = false

                        onMapClick(
                            selectedApplication!!.id,
                            selectedUser!!.id,
                            email.trim(),
                            sortOrder,
                            active
                        )
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CreateForwardOrange
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )

                    Text(
                        text = "Saving...",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                } else {
                    Text("Map")
                }
            }

            // API FEEDBACK
            if (!isSaving && !saveMessage.isNullOrBlank()) {
                Text(
                    text = saveMessage,
                    color = if (saveSucceeded) {
                        Color(0xFF2E7D32)
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    fontSize = 13.sp
                )
            }
        }
    }
}
