
package com.aquila.pocxpertalerts.ui.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import com.aquila.pocxpertalerts.viewmodel.ForwardApplicationOption
import com.aquila.pocxpertalerts.viewmodel.ForwardUserOption

private val SearchForwardOrange = Color(0xFFED741C)
private val SearchForwardPurple = Color(0xFF673AB7)
private val SearchForwardBorder = Color(0xFF999999)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchForwardAlertsScreen(
    onBackClick: () -> Unit,
    onSearchClick: (
        applicationId: String,
        userName: String,
        email: String,
        active: Int
    ) -> Unit,
    applications: List<ForwardApplicationOption> = emptyList(),
    users: List<ForwardUserOption> = emptyList(),
    isLoadingApplications: Boolean = false,
    isLoadingUsers: Boolean = false,
    onApplicationSelected: (ForwardApplicationOption?) -> Unit = {},
    onResetClick: () -> Unit = {}
) {
    var selectedApplication by remember {
        mutableStateOf<ForwardApplicationOption?>(null)
    }
    var applicationMenuExpanded by remember {
        mutableStateOf(false)
    }

    var selectedUser by remember {
        mutableStateOf<ForwardUserOption?>(null)
    }
    var userMenuExpanded by remember {
        mutableStateOf(false)
    }

    var email by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Yes") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Search Forward Alerts",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            selectedApplication = null
                            selectedUser = null
                            email = ""
                            selectedStatus = "Yes"
                            applicationMenuExpanded = false
                            userMenuExpanded = false
                            onApplicationSelected(null)
                            onResetClick()
                        }
                    ) {
                        Text(
                            text = "Reset",
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SearchForwardOrange
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Application dropdown
            ExposedDropdownMenuBox(
                expanded = applicationMenuExpanded,
                onExpandedChange = {
                    applicationMenuExpanded = !applicationMenuExpanded
                }
            ) {
                OutlinedTextField(
                    value = selectedApplication?.name.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Application") },
                    placeholder = { Text("Select Application") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = applicationMenuExpanded
                        )
                    },
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SearchForwardPurple,
                        unfocusedBorderColor = SearchForwardBorder,
                        focusedLabelColor = SearchForwardPurple,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = applicationMenuExpanded,
                    onDismissRequest = {
                        applicationMenuExpanded = false
                    }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Applications") },
                        onClick = {
                            selectedApplication = null
                            selectedUser = null
                            applicationMenuExpanded = false
                            onApplicationSelected(null)
                        }
                    )

                    applications.forEach { application ->
                        DropdownMenuItem(
                            text = { Text(application.name) },
                            onClick = {
                                selectedApplication = application
                                selectedUser = null
                                applicationMenuExpanded = false
                                onApplicationSelected(application)
                            }
                        )
                    }
                }
            }

            if (isLoadingApplications) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = SearchForwardOrange
                )
            }

            // User dropdown
            ExposedDropdownMenuBox(
                expanded = userMenuExpanded,
                onExpandedChange = {
                    userMenuExpanded = !userMenuExpanded
                }
            ) {
                OutlinedTextField(
                    value = selectedUser?.name.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("User") },
                    placeholder = { Text("- Select User -") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = userMenuExpanded
                        )
                    },
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SearchForwardPurple,
                        unfocusedBorderColor = SearchForwardBorder,
                        focusedLabelColor = SearchForwardPurple,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = userMenuExpanded,
                    onDismissRequest = {
                        userMenuExpanded = false
                    }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Users") },
                        onClick = {
                            selectedUser = null
                            userMenuExpanded = false
                        }
                    )

                    users.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.name) },
                            onClick = {
                                selectedUser = user
                                userMenuExpanded = false
                            }
                        )
                    }
                }
            }

            if (isLoadingUsers) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = SearchForwardOrange
                )
            }

            // User email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("User Email") },
                placeholder = { Text("User Email") },
                singleLine = true,
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SearchForwardPurple,
                    unfocusedBorderColor = SearchForwardBorder,
                    focusedLabelColor = SearchForwardPurple,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Active status
            Text(
                text = "Active",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Yes", "No", "All").forEach { option ->
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == option,
                            onClick = {
                                selectedStatus = option
                            }
                        )
                        Text(option)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Search button
            Button(
                onClick = {
                    val active = when (selectedStatus) {
                        "Yes" -> 1
                        "No" -> 0
                        else -> 2
                    }

                    val displayedName = selectedUser?.name.orEmpty()

                    val searchName = if (displayedName.contains(",")) {
                        val parts = displayedName.split(",", limit = 2)
                        "${parts[1].trim()} ${parts[0].trim()}"
                    } else {
                        displayedName.trim()
                    }

                    onSearchClick(
                        selectedApplication?.id ?: "0",
                        searchName,
                        email.trim(),
                        active
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SearchForwardOrange
                )
            ) {
                Text(
                    text = "Search",
                    color = Color.White
                )
            }
        }
    }
}
