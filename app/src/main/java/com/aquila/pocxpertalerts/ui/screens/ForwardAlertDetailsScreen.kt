package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme

// =========================================================
// COLORS
// =========================================================

private val XpertOrange = Color(0xFFED741C)
private val XpertPurple = Color(0xFF3F237D)
private val ScreenBackground = Color(0xFFF7F7F7)
private val DarkText = Color(0xFF222222)
private val GrayText = Color(0xFF777777)


// =========================================================
// FORWARD ALERT DETAILS SCREEN
// =========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardAlertDetailsScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {

    // =====================================================
    // FORM STATE
    // =====================================================

    var forwardAlert by remember {
        mutableStateOf("")
    }

    var application by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var sortOrder by remember {
        mutableStateOf("")
    }

    var activeStatus by remember {
        mutableStateOf("Active")
    }

    var enabled by remember {
        mutableStateOf(true)
    }


    // =====================================================
    // DROPDOWN STATE
    // =====================================================

    var forwardAlertExpanded by remember {
        mutableStateOf(false)
    }

    var applicationExpanded by remember {
        mutableStateOf(false)
    }


    val forwardAlertOptions = listOf(
        "System Alert",
        "Important Update",
        "New Information"
    )

    val applicationOptions = listOf(
        "Xpert Alerts",
        "Operations",
        "Monitoring",
        "Administration"
    )


    // =====================================================
    // SCREEN
    // =====================================================

    Scaffold(

        containerColor =
            ScreenBackground,

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Create Forward Alert",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    androidx.compose.material3.IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,

                            contentDescription =
                                "Back"
                        )
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            Color.White,

                        titleContentColor =
                            XpertPurple,

                        navigationIconContentColor =
                            XpertPurple
                    )
            )
        }
    ) { innerPadding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {


            // =================================================
            // SCREEN DESCRIPTION
            // =================================================

            Text(
                text =
                    "Configure a forward alert and its recipient.",

                fontSize =
                    14.sp,

                color =
                    GrayText
            )


            // =================================================
            // FORWARD ALERT DROPDOWN
            // =================================================

            ExposedDropdownMenuBox(

                expanded =
                    forwardAlertExpanded,

                onExpandedChange = {

                    forwardAlertExpanded =
                        !forwardAlertExpanded
                }
            ) {

                OutlinedTextField(

                    value =
                        forwardAlert,

                    onValueChange = {},

                    readOnly = true,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .menuAnchor(),

                    label = {
                        Text("Forward Alert")
                    },

                    placeholder = {
                        Text("Select Forward Alert")
                    },

                    trailingIcon = {

                        ExposedDropdownMenuDefaults
                            .TrailingIcon(
                                expanded =
                                    forwardAlertExpanded
                            )
                    }
                )


                DropdownMenu(

                    expanded =
                        forwardAlertExpanded,

                    onDismissRequest = {

                        forwardAlertExpanded =
                            false
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    forwardAlertOptions.forEach { option ->

                        DropdownMenuItem(

                            text = {
                                Text(option)
                            },

                            onClick = {

                                forwardAlert =
                                    option

                                forwardAlertExpanded =
                                    false
                            }
                        )
                    }
                }
            }


            // =================================================
            // APPLICATION DROPDOWN
            // =================================================

            ExposedDropdownMenuBox(

                expanded =
                    applicationExpanded,

                onExpandedChange = {

                    applicationExpanded =
                        !applicationExpanded
                }
            ) {

                OutlinedTextField(

                    value =
                        application,

                    onValueChange = {},

                    readOnly = true,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .menuAnchor(),

                    label = {
                        Text("Application")
                    },

                    placeholder = {
                        Text("Select Application")
                    },

                    trailingIcon = {

                        ExposedDropdownMenuDefaults
                            .TrailingIcon(
                                expanded =
                                    applicationExpanded
                            )
                    }
                )


                DropdownMenu(

                    expanded =
                        applicationExpanded,

                    onDismissRequest = {

                        applicationExpanded =
                            false
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    applicationOptions.forEach { option ->

                        DropdownMenuItem(

                            text = {
                                Text(option)
                            },

                            onClick = {

                                application =
                                    option

                                applicationExpanded =
                                    false
                            }
                        )
                    }
                }
            }


            // =================================================
            // EMAIL
            // =================================================

            OutlinedTextField(

                value =
                    email,

                onValueChange = {
                    email = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true,

                label = {
                    Text("Email")
                },

                placeholder = {
                    Text("Enter email address")
                }
            )


            // =================================================
            // SORT ORDER
            // =================================================

            OutlinedTextField(

                value =
                    sortOrder,

                onValueChange = {

                    if (
                        it.all { char ->
                            char.isDigit()
                        }
                    ) {
                        sortOrder = it
                    }
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true,

                label = {
                    Text("Sort Order")
                },

                placeholder = {
                    Text("Enter sort order")
                }
            )


            // =================================================
            // ACTIVE / INACTIVE
            // =================================================

            Text(
                text =
                    "Status",

                fontSize =
                    16.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    DarkText
            )


            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically,

                    modifier =
                        Modifier.weight(1f)
                ) {

                    RadioButton(

                        selected =
                            activeStatus == "Active",

                        onClick = {
                            activeStatus =
                                "Active"
                        }
                    )

                    Text(
                        text = "Active",
                        color = DarkText
                    )
                }


                Row(
                    verticalAlignment =
                        Alignment.CenterVertically,

                    modifier =
                        Modifier.weight(1f)
                ) {

                    RadioButton(

                        selected =
                            activeStatus == "Inactive",

                        onClick = {
                            activeStatus =
                                "Inactive"
                        }
                    )

                    Text(
                        text = "Inactive",
                        color = DarkText
                    )
                }
            }


            // =================================================
            // ENABLE / DISABLE
            // =================================================

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 4.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Enable Forward Alert",

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            DarkText
                    )

                    Text(
                        text =
                            if (enabled) {
                                "Forward alert is enabled"
                            } else {
                                "Forward alert is disabled"
                            },

                        fontSize =
                            13.sp,

                        color =
                            GrayText
                    )
                }


                Switch(

                    checked =
                        enabled,

                    onCheckedChange = {
                        enabled = it
                    }
                )
            }


            // =================================================
            // MAP PLACEHOLDER
            // =================================================

            Text(
                text =
                    "Location",

                fontSize =
                    16.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    DarkText
            )


            OutlinedButton(

                onClick = {
                    // Future Google Maps integration
                },

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector =
                        Icons.Default.LocationOn,

                    contentDescription =
                        "Map"
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        "Select Location on Map"
                )
            }


            Text(
                text =
                    "Map integration will be added in a future phase.",

                fontSize =
                    12.sp,

                color =
                    GrayText
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            // =================================================
            // ACTION BUTTONS
            // =================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                OutlinedButton(

                    onClick =
                        onBackClick,

                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text("Cancel")
                }


                Button(

                    onClick =
                        onSaveClick,

                    modifier =
                        Modifier.weight(1f),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                XpertOrange
                        )
                ) {

                    Text("Save")
                }
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )
        }
    }
}


// =========================================================
// PREVIEW
// =========================================================

@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 360,
    heightDp = 760
)
@Composable
fun ForwardAlertDetailsScreenPreview() {

    XpertAlertsTheme {

        ForwardAlertDetailsScreen(

            onBackClick = {},

            onSaveClick = {}
        )
    }
}