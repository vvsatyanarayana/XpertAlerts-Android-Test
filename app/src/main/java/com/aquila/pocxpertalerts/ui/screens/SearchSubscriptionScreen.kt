
package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ApplicationOption(
    val id: String,
    val name: String
)

private val OldOrange = Color(0xFFF47716)
private val ScreenBackground = Color(0xFFFAFAFA)
private val FieldBorder = Color(0xFFAAAAAA)
private val MainText = Color(0xFF333333)
private val HintText = Color(0xFF777777)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSubscriptionScreen(
    onBackClick: () -> Unit,
    onSearchClick: (
        alertName: String,
        applicationId: String,
        searchActive: Int
    ) -> Unit,
    applications: List<ApplicationOption>,
    isApplicationsLoading: Boolean = false,
    applicationsError: String? = null,
    initialAlertName: String = "",
    initialApplicationId: String = "0",
    initialSearchActive: Int = 1,
    onRetryApplications: () -> Unit = {}
) {
    var alertName by rememberSaveable {
        mutableStateOf(initialAlertName)
    }

    var selectedApplicationId by rememberSaveable {
        mutableStateOf(initialApplicationId)
    }

    var selectedStatus by rememberSaveable {
        mutableStateOf(initialSearchActive)
    }

    var applicationExpanded by remember {
        mutableStateOf(false)
    }

    val allApplications = ApplicationOption("0", "All Applications")

    val dropdownApplications = remember(applications) {
        listOf(allApplications) + applications.filter { it.id != "0" }
    }

    val selectedApplication = dropdownApplications.firstOrNull {
        it.id == selectedApplicationId
    } ?: allApplications

    LaunchedEffect(applications, initialApplicationId) {
        if (
            applications.isNotEmpty() &&
            dropdownApplications.none { it.id == selectedApplicationId }
        ) {
            selectedApplicationId =
                if (dropdownApplications.any { it.id == initialApplicationId }) {
                    initialApplicationId
                } else {
                    "0"
                }
        }
    }

    Scaffold(
        containerColor = ScreenBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Search Subscriptions",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Text(
                        text = "Reset",
                        modifier = Modifier
                            .clickable {
                                alertName = ""
                                selectedApplicationId = "0"
                                selectedStatus = 1
                                applicationExpanded = false
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OldOrange,
                    titleContentColor = Color.White
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
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),
            verticalArrangement = Arrangement.Top
        ) {

            // Alert name field
            OutlinedTextField(
                value = alertName,
                onValueChange = { alertName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Alert Name",
                        fontSize = 14.sp,
                        color = HintText
                    )
                },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = MainText
                ),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FieldBorder,
                    unfocusedBorderColor = FieldBorder,
                    focusedTextColor = MainText,
                    unfocusedTextColor = MainText,
                    cursorColor = OldOrange,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Application dropdown
            ExposedDropdownMenuBox(
                expanded = applicationExpanded,
                onExpandedChange = {
                    applicationExpanded = !applicationExpanded
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = if (selectedApplicationId == "0") {
                        "Select Application"
                    } else {
                        selectedApplication.name
                    },
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = MainText
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select application",
                            tint = HintText
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FieldBorder,
                        unfocusedBorderColor = FieldBorder,
                        focusedTextColor = MainText,
                        unfocusedTextColor = MainText,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                ExposedDropdownMenu(
                    expanded = applicationExpanded,
                    onDismissRequest = {
                        applicationExpanded = false
                    },
                    modifier = Modifier.background(Color.White)
                ) {
                    dropdownApplications.forEach { application ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = application.name,
                                    fontSize = 14.sp
                                )
                            },
                            onClick = {
                                selectedApplicationId = application.id
                                applicationExpanded = false
                            }
                        )
                    }
                }
            }

            if (isApplicationsLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp),
                        color = OldOrange,
                        strokeWidth = 2.dp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Loading applications...",
                        fontSize = 13.sp,
                        color = HintText
                    )
                }
            }

            if (applicationsError != null) {
                Text(
                    text = applicationsError,
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.Red,
                    fontSize = 13.sp
                )

                TextButton(onClick = onRetryApplications) {
                    Text("Retry", color = OldOrange)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Status choices
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusOption(
                    label = "Yes",
                    selected = selectedStatus == 1,
                    onClick = { selectedStatus = 1 }
                )

                StatusOption(
                    label = "No",
                    selected = selectedStatus == 0,
                    onClick = { selectedStatus = 0 }
                )

                StatusOption(
                    label = "All",
                    selected = selectedStatus == 2,
                    onClick = { selectedStatus = 2 }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Search button
            Button(
                onClick = {
                    onSearchClick(
                        alertName.trim(),
                        selectedApplicationId,
                        selectedStatus
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OldOrange,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier
                        .width(18.dp)
                        .height(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Search",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun StatusOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = OldOrange,
                unselectedColor = FieldBorder
            )
        )

        Text(
            text = label,
            color = MainText,
            fontSize = 14.sp
        )
    }
}
