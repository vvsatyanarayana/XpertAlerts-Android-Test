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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.aquila.pocxpertalerts.ui.components.AppTopBar
import com.aquila.pocxpertalerts.ui.components.SearchScreenTopBar
import com.aquila.pocxpertalerts.ui.theme.PlaceholderColor
import com.aquila.pocxpertalerts.ui.theme.SearchButtonColor
import com.aquila.pocxpertalerts.ui.theme.SearchButtonTextColor
import com.aquila.pocxpertalerts.ui.theme.SearchScreenBackground
import com.aquila.pocxpertalerts.ui.theme.SearchTextColor
import com.aquila.pocxpertalerts.viewmodel.ForwardApplicationOption
import com.aquila.pocxpertalerts.viewmodel.ForwardUserOption
private val ForwardFieldBorder = Color(0xFFBDBDBD)

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

    var email by remember {
        mutableStateOf("")
    }

    var selectedStatus by remember {
        mutableStateOf("Yes")
    }

    Scaffold(
        containerColor = SearchScreenBackground,
        topBar = {
            SearchScreenTopBar(
                title = "Search Forward Alerts",
                onBackClick = onBackClick,
                onResetClick = {
                    selectedApplication = null
                    selectedUser = null
                    email = ""
                    selectedStatus = "Yes"
                    applicationMenuExpanded = false
                    userMenuExpanded = false

                    onApplicationSelected(null)
                    onResetClick()
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // Application dropdown
            ExposedDropdownMenuBox(
                expanded = applicationMenuExpanded,
                onExpandedChange = {
                    applicationMenuExpanded = it
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedApplication?.name.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = {
                        Text("Application")
                    },
                    placeholder = {
                        Text(
                            text = "Select Application",
                            color = PlaceholderColor
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = applicationMenuExpanded
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = searchFieldColors(),
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
                        text = {
                            Text("All Applications")
                        },
                        onClick = {
                            selectedApplication = null
                            selectedUser = null
                            applicationMenuExpanded = false

                            onApplicationSelected(null)
                        }
                    )

                    applications.forEach { application ->
                        DropdownMenuItem(
                            text = {
                                Text(application.name)
                            },
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
                    color = SearchButtonColor
                )
            }

            // User dropdown
            ExposedDropdownMenuBox(
                expanded = userMenuExpanded,
                onExpandedChange = { userMenuExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedUser?.name.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("User") },
                    placeholder = { Text("Select User") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = userMenuExpanded
                        )
                    },
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
                    color = SearchButtonColor
                )
            }

            // User email field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                singleLine = true,
                label = {
                    Text("User Email")
                },
                placeholder = {
                    Text(
                        text = "Enter user email",
                        color = PlaceholderColor
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = searchFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            // Active status
            Text(
                text = "Active",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SearchTextColor
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Yes", "No", "All").forEach { option ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == option,
                            onClick = {
                                selectedStatus = option
                            }
                        )

                        Text(
                            text = option,
                            color = SearchTextColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SearchButtonColor,
                    contentColor = SearchButtonTextColor
                )
            ) {
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun searchFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SearchButtonColor,
        unfocusedBorderColor = ForwardFieldBorder,
        focusedLabelColor = SearchButtonColor,
        unfocusedLabelColor = SearchTextColor,
        focusedTextColor = SearchTextColor,
        unfocusedTextColor = SearchTextColor,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        cursorColor = SearchButtonColor
    )