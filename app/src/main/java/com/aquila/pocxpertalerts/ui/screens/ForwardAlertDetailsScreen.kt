
package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.aquila.pocxpertalerts.ui.components.AppTopBar
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange

private val ForwardDetailsGreen = Color(0xFF008F54)
private val ForwardDetailsBorder = Color(0xFFB8B8B8)

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

    // Restore existing mapping values when editing.
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

    // Auto-populate only a valid email address received from the API.
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
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AppTopBar(
                title = if (editDetails != null) {
                    "Edit Forward Alert"
                } else {
                    "Create Forward Alert"
                },
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(White)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = if (editDetails != null) {
                    "Edit Forward Alert"
                } else {
                    "Create Forward Alert"
                },
                color = TextDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Choose the application and user to configure alert forwarding.",
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            Divider(
                color = ForwardDetailsBorder.copy(alpha = 0.4f),
                thickness = 1.dp
            )

            // APPLICATION
            ForwardFieldLabel("Application")

            ForwardDetailsDropdown(
                value = selectedApplication?.name.orEmpty(),
                placeholder = "Select Application",
                expanded = applicationMenuOpen,
                enabled = !isSaving,
                onExpandedChange = {
                    applicationMenuOpen = it
                },
                options = applications,
                optionLabel = { it.name },
                emptyMessage = "No applications available",
                onSelected = { application ->
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

            // USER
            ForwardFieldLabel("User")

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
                    placeholder = {
                        Text(
                            text = "Select User",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = userMenuOpen
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .height(56.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = forwardFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = userMenuOpen,
                    onDismissRequest = {
                        userMenuOpen = false
                        userSearch = ""
                    },
                    modifier = Modifier
                        .background(White)
                        .height(300.dp)
                ) {
                    OutlinedTextField(
                        value = userSearch,
                        onValueChange = {
                            userSearch = it
                        },
                        placeholder = {
                            Text(
                                text = "Search users",
                                color = TextGray,
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = forwardFieldColors()
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
                                    text = if (users.isEmpty()) {
                                        "No users available"
                                    } else {
                                        "No matching users"
                                    },
                                    color = TextGray
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
                                            color = TextDark,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        )

                                        Spacer(
                                            modifier = Modifier.height(3.dp)
                                        )

                                        Text(
                                            text = user.email,
                                            fontSize = 12.sp,
                                            color = TextGray
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
            ForwardFieldLabel("Email Address")

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    showValidation = false
                },
                enabled = !isSaving,
                placeholder = {
                    Text(
                        text = "Enter email address",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = forwardFieldColors()
            )

            // SORT ORDER
            ForwardFieldLabel("Sort Order")

            OutlinedTextField(
                value = sortOrder,
                onValueChange = { value ->
                    if (
                        value.length <= 3 &&
                        value.all { it.isDigit() }
                    ) {
                        sortOrder = value
                        showValidation = false
                    }
                },
                enabled = !isSaving,
                placeholder = {
                    Text(
                        text = "Enter sort order",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = forwardFieldColors()
            )

            // ACTIVATE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = AppBackground,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Activate",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )

                    Text(
                        text = if (active) "Enabled" else "Disabled",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }

                Switch(
                    checked = active,
                    onCheckedChange = {
                        active = it
                    },
                    enabled = !isSaving,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White,
                        checkedTrackColor = ForwardDetailsGreen,
                        checkedBorderColor = ForwardDetailsGreen
                    )
                )
            }

            // VALIDATION
            if (showValidation) {
                Text(
                    text = "Please select an application and user, enter a valid email, and provide a sort order.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = XpertOrange,
                    contentColor = White,
                    disabledContainerColor = XpertOrange.copy(alpha = 0.5f),
                    disabledContentColor = White
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(20.dp)
                            .width(20.dp),
                        strokeWidth = 2.dp,
                        color = White
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Saving...",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = "Map",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ForwardFieldLabel(
    text: String
) {
    Text(
        text = text,
        color = TextDark,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun forwardFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = XpertOrange,
        unfocusedBorderColor = ForwardDetailsBorder.copy(alpha = 0.75f),
        disabledBorderColor = ForwardDetailsBorder.copy(alpha = 0.35f),
        focusedTextColor = TextDark,
        unfocusedTextColor = TextDark,
        disabledTextColor = TextGray,
        cursorColor = XpertOrange,
        focusedContainerColor = White,
        unfocusedContainerColor = White,
        disabledContainerColor = AppBackground
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> ForwardDetailsDropdown(
    value: String,
    placeholder: String,
    expanded: Boolean,
    enabled: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    options: List<T>,
    optionLabel: (T) -> String,
    emptyMessage: String,
    onSelected: (T) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) onExpandedChange(it)
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    color = TextGray
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .height(56.dp),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = forwardFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onExpandedChange(false)
            },
            modifier = Modifier.background(White)
        ) {
            if (options.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = emptyMessage,
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    },
                    onClick = {
                        onExpandedChange(false)
                    }
                )
            } else {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = optionLabel(option),
                                color = TextDark,
                                fontSize = 14.sp
                            )
                        },
                        onClick = {
                            onSelected(option)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}
