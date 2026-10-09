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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailsScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var application by remember { mutableStateOf("Select Application") }
    var alertDefinition by remember { mutableStateOf("Select Alert Definition") }

    var alertName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var messageSubject by remember { mutableStateOf("") }
    var threshold by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf("") }

    var deviceEnabled by remember { mutableStateOf(true) }
    var mailEnabled by remember { mutableStateOf(false) }

    var jobType by remember { mutableStateOf("Alert Def") }
    var active by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Subscription",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Subscription Details",
                fontWeight = FontWeight.Bold
            )

            SubscriptionDropdown(
                label = "Application",
                value = application,
                options = listOf(
                    "Select Application",
                    "Xpert Application",
                    "Another Application"
                ),
                onSelected = {
                    application = it
                }
            )

            SubscriptionDropdown(
                label = "Alert Definition",
                value = alertDefinition,
                options = listOf(
                    "Select Alert Definition",
                    "System Alert",
                    "Important Update",
                    "New Information"
                ),
                onSelected = {
                    alertDefinition = it
                }
            )

            OutlinedTextField(
                value = alertName,
                onValueChange = { alertName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Alert Name") },
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                singleLine = true
            )

            OutlinedTextField(
                value = messageSubject,
                onValueChange = { messageSubject = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Message Subject") },
                singleLine = true
            )

            OutlinedTextField(
                value = threshold,
                onValueChange = { threshold = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Threshold") },
                singleLine = true
            )

            OutlinedTextField(
                value = sortOrder,
                onValueChange = { sortOrder = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Sort Order") },
                singleLine = true
            )

            Text(
                text = "Notification Options",
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = deviceEnabled,
                    onCheckedChange = { deviceEnabled = it }
                )

                Text("Device")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = mailEnabled,
                    onCheckedChange = { mailEnabled = it }
                )

                Text("Mail")
            }

            Text(
                text = "Job Type",
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = jobType == "Alert Def",
                    onClick = { jobType = "Alert Def" }
                )

                Text("Alert Def")

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                RadioButton(
                    selected = jobType == "Aggregate Job",
                    onClick = { jobType = "Aggregate Job" }
                )

                Text("Aggregate Job")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (active) "Active" else "Inactive",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Medium
                )

                Switch(
                    checked = active,
                    onCheckedChange = { active = it }
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
    }
}

@Composable
private fun SubscriptionDropdown(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$label: $value"
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
                        onSelected(option)
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
fun SubscriptionDetailsScreenPreview() {
    XpertAlertsTheme {
        SubscriptionDetailsScreen(
            onBackClick = {},
            onSaveClick = {}
        )
    }
}