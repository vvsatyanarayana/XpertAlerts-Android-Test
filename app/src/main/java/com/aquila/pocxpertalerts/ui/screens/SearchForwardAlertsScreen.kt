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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchForwardAlertsScreen(
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    var selectedAlertType by remember {
        mutableStateOf("All")
    }

    var alertTypeExpanded by remember {
        mutableStateOf(false)
    }

    var selectedStatus by remember {
        mutableStateOf("All")
    }

    var userEmail by remember {
        mutableStateOf("")
    }

    val alertTypes = listOf(
        "All",
        "System Alert",
        "Important Update",
        "New Information"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Search Forward Alerts")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Search Criteria",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Alert Type
            ExposedDropdownMenuBox(
                expanded = alertTypeExpanded,
                onExpandedChange = {
                    alertTypeExpanded = !alertTypeExpanded
                }
            ) {

                OutlinedTextField(
                    value = selectedAlertType,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Forward Alert")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = alertTypeExpanded
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = alertTypeExpanded,
                    onDismissRequest = {
                        alertTypeExpanded = false
                    }
                ) {

                    alertTypes.forEach { alertType ->

                        DropdownMenuItem(
                            text = {
                                Text(alertType)
                            },
                            onClick = {
                                selectedAlertType = alertType
                                alertTypeExpanded = false
                            }
                        )
                    }
                }
            }

            // Status
            Text(
                text = "Status",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Column {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    RadioButton(
                        selected = selectedStatus == "All",
                        onClick = {
                            selectedStatus = "All"
                        }
                    )

                    Text("All")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    RadioButton(
                        selected = selectedStatus == "Active",
                        onClick = {
                            selectedStatus = "Active"
                        }
                    )

                    Text("Active")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    RadioButton(
                        selected = selectedStatus == "Inactive",
                        onClick = {
                            selectedStatus = "Inactive"
                        }
                    )

                    Text("Inactive")
                }
            }

            // User / Email
            OutlinedTextField(
                value = userEmail,
                onValueChange = {
                    userEmail = it
                },
                label = {
                    Text("User / Email")
                },
                placeholder = {
                    Text("Enter user or email")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Map placeholder
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Location",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedButton(
                    onClick = {
                        // Google Maps integration will be added later.
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Map"
                    )

                    Spacer(
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Text("Select Location on Map")
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Map selection will be connected during integration.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedButton(
                    onClick = {
                        selectedAlertType = "All"
                        selectedStatus = "All"
                        userEmail = ""
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }

                Button(
                    onClick = onSearchClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Search")
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,

    )
@Composable
private fun SearchForwardAlertsScreenPreview() {
    SearchForwardAlertsScreen(
        onBackClick = {},
        onSearchClick = {}
    )
}