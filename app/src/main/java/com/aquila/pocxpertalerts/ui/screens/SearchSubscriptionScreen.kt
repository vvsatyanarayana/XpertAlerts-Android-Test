package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSubscriptionScreen(
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    var alertName by remember { mutableStateOf("") }
    var selectedApplication by remember { mutableStateOf("All Applications") }
    var applicationExpanded by remember { mutableStateOf(false) }

    var selectedStatus by remember { mutableStateOf("Yes") }

    val applications = listOf(
        "All Applications",
        "System Alerts",
        "Xpert Alerts",
        "Vehicle Alerts"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Search Subscriptions",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {

                        Text(
                            text = "Reset",
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .padding(vertical = 12.dp)
                                .clickable {
                                    alertName = ""
                                    selectedApplication = "All Applications"
                                    selectedStatus = "Yes"
                                },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.Top
        ) {

            // Alert Name
            OutlinedTextField(
                value = alertName,
                onValueChange = { alertName = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Alert Name")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Application
            ExposedDropdownMenuBox(
                expanded = applicationExpanded,
                onExpandedChange = {
                    applicationExpanded = !applicationExpanded
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = selectedApplication,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = {
                        Text("Application")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = applicationExpanded
                        )
                    }
                )

                ExposedDropdownMenu(
                    expanded = applicationExpanded,
                    onDismissRequest = {
                        applicationExpanded = false
                    }
                ) {
                    applications.forEach { application ->

                        DropdownMenuItem(
                            text = {
                                Text(application)
                            },
                            onClick = {
                                selectedApplication = application
                                applicationExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Yes / No / All
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedStatus == "Yes",
                        onClick = {
                            selectedStatus = "Yes"
                        }
                    )

                    Text("Yes")
                }

                Spacer(modifier = Modifier.padding(horizontal = 6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedStatus == "No",
                        onClick = {
                            selectedStatus = "No"
                        }
                    )

                    Text("No")
                }

                Spacer(modifier = Modifier.padding(horizontal = 6.dp))

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
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Search button
            Button(
                onClick = onSearchClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                Text("Search")
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SearchSubscriptionScreenPreview() {
    XpertAlertsTheme {
        SearchSubscriptionScreen()
    }
}