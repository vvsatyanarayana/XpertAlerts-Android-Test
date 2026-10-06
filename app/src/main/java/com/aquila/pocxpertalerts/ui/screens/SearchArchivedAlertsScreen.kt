package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchArchivedAlertsScreen(
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    var startDate by remember {
        mutableStateOf("")
    }

    var endDate by remember {
        mutableStateOf("")
    }

    var viewBy by remember {
        mutableStateOf("All")
    }

    var deviceStatus by remember {
        mutableStateOf("All")
    }

    var emailStatus by remember {
        mutableStateOf("All")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Search Archived Alerts",
                        fontWeight = FontWeight.Bold
                    )
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
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Search Filters",
                fontWeight = FontWeight.Bold
            )

            SearchDropdown(
                label = "View By",
                selectedValue = viewBy,
                options = listOf(
                    "All",
                    "Unread",
                    "Read"
                ),
                onValueSelected = {
                    viewBy = it
                }
            )

            SearchDropdown(
                label = "Device Status",
                selectedValue = deviceStatus,
                options = listOf(
                    "All",
                    "Sent",
                    "Not Sent"
                ),
                onValueSelected = {
                    deviceStatus = it
                }
            )

            SearchDropdown(
                label = "Email Status",
                selectedValue = emailStatus,
                options = listOf(
                    "All",
                    "Sent",
                    "Not Sent"
                ),
                onValueSelected = {
                    emailStatus = it
                }
            )

            OutlinedTextField(
                value = startDate,
                onValueChange = {
                    startDate = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Start Date")
                },
                placeholder = {
                    Text("DD/MM/YYYY")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = endDate,
                onValueChange = {
                    endDate = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("End Date")
                },
                placeholder = {
                    Text("DD/MM/YYYY")
                },
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    onClick = onSearchClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text("Search")
                }

                OutlinedButton(
                    onClick = {
                        viewBy = "All"
                        deviceStatus = "All"
                        emailStatus = "All"
                        startDate = ""
                        endDate = ""
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
            }
        }
    }
}

@Composable
private fun SearchDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedButton(
            onClick = {
                expanded = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$label: $selectedValue"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { option ->

                DropdownMenuItem(
                    text = {
                        Text(option)
                    },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,

)
@Composable
fun SearchArchivedAlertsScreenPreview() {
    XpertAlertsTheme {
        SearchArchivedAlertsScreen(
            onBackClick = {},
            onSearchClick = {}
        )
    }
}