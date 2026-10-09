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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
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
// SEARCH ALERTS SCREEN
// =========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAlertsScreen(
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {

    // =====================================================
    // STATE
    // =====================================================

    var startDate by remember {
        mutableStateOf("")
    }

    var endDate by remember {
        mutableStateOf("")
    }

    var keyword by remember {
        mutableStateOf("")
    }

    var viewBy by remember {
        mutableStateOf("All")
    }

    var application by remember {
        mutableStateOf("All")
    }

    var alertDefinition by remember {
        mutableStateOf("All")
    }


    // =====================================================
    // VALIDATION
    // =====================================================

    var dateError by remember {
        mutableStateOf(false)
    }


    Scaffold(

        containerColor = ScreenBackground,

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Search Alerts",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = XpertPurple
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = XpertPurple
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
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

            verticalArrangement = Arrangement.Top
        ) {


            // =================================================
            // DESCRIPTION
            // =================================================

            Text(
                text = "Find alerts using the filters below.",
                fontSize = 14.sp,
                color = GrayText
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // =================================================
            // SEARCH CARD
            // =================================================

            Card(

                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {


                    // =========================================
                    // START DATE
                    // =========================================

                    OutlinedTextField(

                        value = startDate,

                        onValueChange = {
                            startDate = it
                            dateError = false
                        },

                        modifier = Modifier.fillMaxWidth(),

                        label = {
                            Text("Start date")
                        },

                        placeholder = {
                            Text("DD/MM/YYYY")
                        },

                        trailingIcon = {

                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Start date"
                            )
                        },

                        singleLine = true
                    )


                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )


                    // =========================================
                    // END DATE
                    // =========================================

                    OutlinedTextField(

                        value = endDate,

                        onValueChange = {
                            endDate = it
                            dateError = false
                        },

                        modifier = Modifier.fillMaxWidth(),

                        label = {
                            Text("End date")
                        },

                        placeholder = {
                            Text("DD/MM/YYYY")
                        },

                        trailingIcon = {

                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "End date"
                            )
                        },

                        isError = dateError,

                        supportingText = {

                            if (dateError) {

                                Text(
                                    text = "End date must be greater than or equal to start date.",
                                    color = Color.Red
                                )
                            }
                        },

                        singleLine = true
                    )


                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )


                    // =========================================
                    // KEYWORD
                    // =========================================

                    OutlinedTextField(

                        value = keyword,

                        onValueChange = {
                            keyword = it
                        },

                        modifier = Modifier.fillMaxWidth(),

                        label = {
                            Text("Keyword")
                        },

                        placeholder = {
                            Text("Enter keyword")
                        },

                        singleLine = true
                    )


                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )


                    // =========================================
                    // VIEW BY
                    // =========================================

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


                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )


                    // =========================================
                    // APPLICATION
                    // =========================================

                    SearchDropdown(

                        label = "Application",

                        selectedValue = application,

                        options = listOf(
                            "All",
                            "Application 1",
                            "Application 2"
                        ),

                        onValueSelected = {
                            application = it
                        }
                    )


                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )


                    // =========================================
                    // ALERT DEFINITION
                    // =========================================

                    SearchDropdown(

                        label = "Alert Definition",

                        selectedValue = alertDefinition,

                        options = listOf(
                            "All",
                            "System Alert",
                            "Important Update",
                            "New Information"
                        ),

                        onValueSelected = {
                            alertDefinition = it
                        }
                    )


                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )


                    // =========================================
                    // SEARCH BUTTON
                    // =========================================

                    Button(

                        onClick = {

                            /*
                             * TEMPORARY VALIDATION
                             *
                             * Real date parsing and validation
                             * will be handled properly when the
                             * search ViewModel is added.
                             */

                            if (
                                startDate.isNotBlank() &&
                                endDate.isNotBlank() &&
                                endDate < startDate
                            ) {

                                dateError = true

                            } else {

                                dateError = false

                                onSearchClick()
                            }
                        },

                        modifier = Modifier.fillMaxWidth(),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = XpertOrange
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = "Search",
                            fontSize = 15.sp
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )


                    // =========================================
                    // RESET BUTTON
                    // =========================================

                    OutlinedButton(

                        onClick = {

                            startDate = ""
                            endDate = ""
                            keyword = ""
                            viewBy = "All"
                            application = "All"
                            alertDefinition = "All"
                            dateError = false
                        },

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "Reset",
                            color = XpertPurple
                        )
                    }
                }
            }
        }
    }
}


// =========================================================
// SEARCH DROPDOWN
// =========================================================

@OptIn(ExperimentalMaterial3Api::class)
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


    ExposedDropdownMenuBox(

        expanded = expanded,

        onExpandedChange = {
            expanded = !expanded
        }
    ) {

        OutlinedTextField(

            value = selectedValue,

            onValueChange = {},

            readOnly = true,

            label = {
                Text(label)
            },

            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },

            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    ExposedDropdownMenuAnchorType.PrimaryNotEditable
                ),

            singleLine = true
        )


        ExposedDropdownMenu(

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


// =========================================================
// PREVIEW
// =========================================================

@Preview(
    showBackground = true,
    showSystemUi = true,

)
@Composable
fun SearchAlertsScreenPreview() {

    XpertAlertsTheme {

        SearchAlertsScreen(

            onBackClick = {},

            onSearchClick = {}
        )
    }
}