package com.aquila.pocxpertalerts.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.data.repository.SubscriptionSaveRequest
import com.aquila.pocxpertalerts.viewmodel.SubscriptionsViewModel

private val SubscriptionOrange = Color(0xFFF47716)
private val SubscriptionGreen = Color(0xFF008F54)
private val SubscriptionBackground = Color(0xFFFAFAFA)
private val SubscriptionBorder = Color(0xFFB8B8B8)
private val SubscriptionText = Color(0xFF292929)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailsScreen(
    subscriptionId: String,
    viewModel: SubscriptionsViewModel,
    userId: String,
    userTypeId: String,
    sessionId: String,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current

    SideEffect {
        (view.context as? Activity)?.window?.statusBarColor =
            SubscriptionOrange.toArgb()
    }

    var applicationId by remember(subscriptionId) {
        mutableStateOf("")
    }

    var alertDefinitionId by remember(subscriptionId) {
        mutableStateOf("")
    }

    var alertName by remember(subscriptionId) {
        mutableStateOf("")
    }

    var email by remember(subscriptionId) {
        mutableStateOf("")
    }

    var messageSubject by remember(subscriptionId) {
        mutableStateOf("")
    }

    var messageBody by remember(subscriptionId) {
        mutableStateOf("")
    }

    var threshold by remember(subscriptionId) {
        mutableStateOf("0")
    }

    var sortOrder by remember(subscriptionId) {
        mutableStateOf("0")
    }

    var deviceEnabled by remember(subscriptionId) {
        mutableStateOf(true)
    }

    var mailEnabled by remember(subscriptionId) {
        mutableStateOf(false)
    }

    var jobType by remember(subscriptionId) {
        mutableStateOf("Alert Def")
    }

    var active by remember(subscriptionId) {
        mutableStateOf(true)
    }

    var formInitialized by remember(subscriptionId) {
        mutableStateOf(subscriptionId == "new")
    }

    var validationMessage by remember(subscriptionId) {
        mutableStateOf<String?>(null)
    }

    // Show a popup when the ViewModel detects a duplicate.
    if (uiState.duplicateMessage != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.clearMessages()
            },
            title = {
                Text("Please review your information")
            },
            text = {
                Text(
                    uiState.duplicateMessage
                        ?: "Record already exists."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearMessages()
                    }
                ) {
                    Text(
                        text = "OK",
                        color = SubscriptionOrange
                    )
                }
            }
        )
    }

    val selectedApplication = uiState.applications.firstOrNull {
        it.id == applicationId
    }

    val selectedDefinition = uiState.alertDefinitions.firstOrNull {
        it.id == alertDefinitionId
    }

    // Load applications and subscription details when the screen opens.
    LaunchedEffect(
        subscriptionId,
        userId,
        userTypeId,
        sessionId
    ) {
        viewModel.clearMessages()

        viewModel.loadApplications(
            userId = userId,
            userTypeId = userTypeId,
            sessionId = sessionId
        )

        if (subscriptionId != "new") {
            viewModel.loadSubscriptionDetails(
                userSubId = subscriptionId,
                sessionId = sessionId
            )
        }
    }

    // Populate the form when editing an existing subscription.


    LaunchedEffect(
        subscriptionId,
        uiState.selectedSubscription,
        uiState.subscriptions
    ) {
        val details = uiState.selectedSubscription

        if (
            subscriptionId != "new" &&
            details != null &&
            details.userSubId.toString() == subscriptionId &&
            !formInitialized
        ) {
            val subscriptionFromList = uiState.subscriptions.firstOrNull {
                it.userSubId.toString() == subscriptionId
            }

            applicationId = details.applicationId.toString()
            alertDefinitionId = details.alertDefId.toString()

            // Alert name: details first, then list fallback.
            alertName = details.alertName
                ?.takeIf { it.isNotBlank() }
                ?: subscriptionFromList?.alertName
                    ?.takeIf { it.isNotBlank() }
                        ?: ""

            // Actual email address: exclude notification flag values.
            val detailsEmail = details.emailId
                ?.trim()
                ?.takeIf {
                    it.isNotEmpty() &&
                            !it.equals("Yes", ignoreCase = true) &&
                            !it.equals("No", ignoreCase = true)
                }

            val listEmail = subscriptionFromList?.emailId
                ?.trim()
                ?.takeIf {
                    it.isNotEmpty() &&
                            !it.equals("Yes", ignoreCase = true) &&
                            !it.equals("No", ignoreCase = true)
                }

            email = detailsEmail ?: listEmail ?: ""

            // Mail checkbox: use the notification flag.
            val mailFlag = details.emailEnabled
                ?: subscriptionFromList?.emailEnabled

            mailEnabled = when {
                mailFlag.equals("Yes", ignoreCase = true) -> true
                mailFlag.equals("No", ignoreCase = true) -> false
                mailFlag == "1" -> true
                mailFlag == "0" -> false
                else -> false
            }

            messageSubject = details.subject.orEmpty()
            messageBody = details.body.orEmpty()
            threshold = details.threshold.toString()
            sortOrder = details.sortOrder.toString()

            active = details.active == 1

            deviceEnabled = details.device?.let {
                it == "1" ||
                        it.equals("true", ignoreCase = true) ||
                        it.equals("yes", ignoreCase = true)
            } ?: true

            jobType = if (details.aggregateJobsId > 0L) {
                "Aggregate Job"
            } else {
                "Alert Def"
            }

            formInitialized = true

            android.util.Log.d(
                "SubscriptionDetails",
                "Subscription ID: $subscriptionId, " +
                        "Alert name: ${details.alertName}, " +
                        "Email address available: ${email.isNotBlank()}, " +
                        "Mail enabled: $mailEnabled"
            )
        }
    }



    // Load alert definitions for the selected application.
    LaunchedEffect(
        applicationId,
        userId,
        sessionId
    ) {
        if (
            applicationId.isNotBlank() &&
            applicationId != "0"
        ) {
            viewModel.loadAlertDefinitions(
                applicationId = applicationId,
                alertFlag = "1",
                userId = userId,
                sessionId = sessionId
            )
        }
    }

    // Re-select the existing alert definition when options load.
    LaunchedEffect(
        subscriptionId,
        uiState.selectedSubscription,
        uiState.alertDefinitions
    ) {
        val details = uiState.selectedSubscription

        if (
            subscriptionId != "new" &&
            details != null &&
            details.userSubId.toString() == subscriptionId
        ) {
            val matchingDefinition =
                uiState.alertDefinitions.firstOrNull {
                    it.id == details.alertDefId.toString()
                }

            if (matchingDefinition != null) {
                alertDefinitionId = matchingDefinition.id
            }
        }
    }

    // Return to the list only after the save succeeds.
    LaunchedEffect(uiState.saveMessage) {
        if (uiState.saveMessage != null) {
            onSaveSuccess()
        }
    }

    Scaffold(
        containerColor = SubscriptionBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Subscription Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SubscriptionOrange,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (subscriptionId == "new") {
                    "Create Subscription"
                } else {
                    "Edit Subscription"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SubscriptionText
            )

            if (
                uiState.isLoading ||
                uiState.isLoadingApplications
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    ),
                    color = SubscriptionOrange
                )
            }

            // Application dropdown.
            SubscriptionDropdown(
                value = selectedApplication?.name
                    ?: "Select Application",
                options = uiState.applications,
                optionLabel = { it.name },
                onSelected = { selected ->
                    if (applicationId != selected.id) {
                        applicationId = selected.id
                        alertDefinitionId = ""
                        validationMessage = null
                    }
                },
                enabled = !uiState.isLoadingApplications
            )

            // Job type.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                RadioButton(
                    selected = jobType == "Alert Def",
                    onClick = {
                        jobType = "Alert Def"
                        validationMessage = null
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = SubscriptionOrange
                    )
                )

                Text(
                    text = "Alert Definition",
                    fontSize = 12.sp,
                    color = SubscriptionText
                )

                RadioButton(
                    selected = jobType == "Aggregate Job",
                    onClick = {
                        jobType = "Aggregate Job"
                        validationMessage = null
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = SubscriptionOrange
                    )
                )

                Text(
                    text = "Aggregate Job",
                    fontSize = 12.sp,
                    color = SubscriptionText
                )
            }

            // Alert Definition dropdown.
            SubscriptionDropdown(
                value = selectedDefinition?.name
                    ?: "Select Alert Definition",
                options = uiState.alertDefinitions,
                optionLabel = { it.name },
                onSelected = { selected ->
                    alertDefinitionId = selected.id
                    validationMessage = null
                },
                enabled = applicationId.isNotBlank() &&
                        applicationId != "0" &&
                        !uiState.isLoadingDefinitions
            )

            if (uiState.isLoadingDefinitions) {
                Text(
                    text = "Loading alert definitions...",
                    fontSize = 12.sp,
                    color = SubscriptionText
                )
            }

            SubscriptionTextField(
                value = alertName,
                onValueChange = {
                    alertName = it
                },
                placeholder = "Alert Name"
            )

            SubscriptionTextField(
                value = email,
                onValueChange = { newEmail ->
                    email = newEmail
                },
                placeholder = "Email"
            )

            SubscriptionTextField(
                value = messageSubject,
                onValueChange = {
                    messageSubject = it
                },
                placeholder = "Message Subject"
            )

            SubscriptionTextField(
                value = threshold,
                onValueChange = {
                    threshold = it
                },
                placeholder = "Threshold"
            )

            SubscriptionTextField(
                value = sortOrder,
                onValueChange = {
                    sortOrder = it
                },
                placeholder = "Sort Order"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activate",
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    color = SubscriptionText
                )

                Switch(
                    checked = active,
                    onCheckedChange = {
                        active = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SubscriptionGreen,
                        checkedBorderColor = SubscriptionGreen
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = deviceEnabled,
                    onCheckedChange = {
                        deviceEnabled = it
                    }
                )

                Text(
                    text = "Device",
                    fontSize = 12.sp
                )

                Checkbox(
                    checked = mailEnabled,
                    onCheckedChange = {
                        mailEnabled = it
                    }
                )

                Text(
                    text = "Mail",
                    fontSize = 12.sp
                )
            }

            if (mailEnabled) {
                SubscriptionTextField(
                    value = messageBody,
                    onValueChange = {
                        messageBody = it
                    },
                    placeholder = "Message Body",
                    minLines = 3
                )
            }

            validationMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            uiState.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = {
                    val app = selectedApplication
                    val definition = selectedDefinition

                    validationMessage = when {
                        userId.isBlank() ||
                                sessionId.isBlank() ->
                            "User or session information is missing."

                        app == null ->
                            "Please select an application."

                        definition == null ->
                            "Please select an alert definition."

                        alertName.isBlank() ->
                            "Please enter an alert name."

                        else -> null
                    }

                    if (validationMessage == null) {
                        viewModel.saveSubscription(
                            SubscriptionSaveRequest(
                                userSubId = if (
                                    subscriptionId == "new"
                                ) {
                                    "0"
                                } else {
                                    subscriptionId
                                },
                                alertName = alertName.trim(),
                                applicationId = app!!.id,
                                alertDefId = definition!!.id,
                                userId = userId,
                                mailId = if (mailEnabled) {
                                    email.trim()
                                } else {
                                    ""
                                },
                                messageBody = messageBody,
                                subject = messageSubject,
                                alertFlag = "1",
                                threshold = threshold
                                    .toIntOrNull()
                                    ?.toString() ?: "0",
                                sortOrder = sortOrder
                                    .toIntOrNull()
                                    ?.toString() ?: "0",
                                active = if (active) "1" else "0",
                                sessionId = sessionId
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SubscriptionOrange,
                    contentColor = Color.White
                ),
                enabled = !uiState.isSaving &&
                        !uiState.isLoading &&
                        !uiState.isLoadingApplications &&
                        !uiState.isLoadingDefinitions
            ) {
                Text(
                    text = if (uiState.isSaving) {
                        "Saving..."
                    } else {
                        "Save"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
    }
}

@Composable
private fun SubscriptionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min = if (minLines > 1) 84.dp else 48.dp
            ),
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 12.sp,
                color = Color.Gray
            )
        },
        singleLine = minLines == 1,
        minLines = minLines,
        shape = RoundedCornerShape(26.dp),
        colors = androidx.compose.material3
            .OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SubscriptionGreen,
                unfocusedBorderColor = SubscriptionBorder,
                disabledBorderColor = SubscriptionBorder,
                focusedTextColor = SubscriptionText,
                unfocusedTextColor = SubscriptionText,
                cursorColor = SubscriptionGreen,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
        textStyle = TextStyle(
            fontSize = 12.sp,
            color = SubscriptionText
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SubscriptionDropdown(
    value: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { shouldExpand ->
            if (enabled) {
                expanded = shouldExpand
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            enabled = enabled,
            modifier = Modifier
                .menuAnchor(
                    ExposedDropdownMenuAnchorType.PrimaryNotEditable
                )
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            shape = RoundedCornerShape(26.dp),
            textStyle = TextStyle(
                fontSize = 12.sp,
                color = SubscriptionText
            ),
            colors = androidx.compose.material3
                .OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SubscriptionGreen,
                    unfocusedBorderColor = SubscriptionBorder,
                    disabledBorderColor = SubscriptionBorder,
                    focusedTextColor = SubscriptionText,
                    unfocusedTextColor = SubscriptionText,
                    disabledTextColor = SubscriptionText,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.heightIn(max = 300.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = optionLabel(option),
                            fontSize = 13.sp
                        )
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