
package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.ui.components.AppSearchDropdown
import com.aquila.pocxpertalerts.ui.components.AppSearchTextField
import com.aquila.pocxpertalerts.ui.components.AppTopBar
import com.aquila.pocxpertalerts.ui.components.SearchScreenTopBar
import com.aquila.pocxpertalerts.ui.theme.PlaceholderColor
import com.aquila.pocxpertalerts.ui.theme.SearchButtonColor
import com.aquila.pocxpertalerts.ui.theme.SearchButtonTextColor
import com.aquila.pocxpertalerts.ui.theme.SearchScreenBackground
import com.aquila.pocxpertalerts.ui.theme.SearchTextColor

data class ApplicationOption(
    val id: String,
    val name: String
)

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
        mutableIntStateOf(initialSearchActive)
    }

    val dropdownApplications = remember(applications) {
        listOf(ApplicationOption("0", "All Applications")) +
                applications.filter { it.id != "0" }
    }

    val selectedApplication = dropdownApplications.firstOrNull {
        it.id == selectedApplicationId
    } ?: dropdownApplications.first()

    LaunchedEffect(applications, initialApplicationId) {
        if (
            dropdownApplications.none {
                it.id == selectedApplicationId
            }
        ) {
            selectedApplicationId =
                if (
                    dropdownApplications.any {
                        it.id == initialApplicationId
                    }
                ) {
                    initialApplicationId
                } else {
                    "0"
                }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SearchScreenBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            SearchScreenTopBar(
                title = "Search Subscriptions",
                onBackClick = onBackClick,
                onResetClick = {
                    alertName = ""
                    selectedApplicationId = "0"
                    selectedStatus = 1
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SearchScreenBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Alert Name
            AppSearchTextField(
                value = alertName,
                onValueChange = { alertName = it },
                placeholder = "Enter alert name"
            )

            // Application
            AppSearchDropdown(
                label = "Application",
                selectedValue = if (selectedApplicationId == "0") {
                    ""
                } else {
                    selectedApplication.name
                },
                options = dropdownApplications.map { it.name },
                placeholder = "Select Application",
                onValueSelected = { selectedName ->
                    selectedApplicationId =
                        dropdownApplications.firstOrNull {
                            it.name == selectedName
                        }?.id ?: "0"
                },
                enabled = !isApplicationsLoading
            )

            if (isApplicationsLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        color = SearchButtonColor,
                        strokeWidth = 2.dp
                    )

                    Text(
                        text = "Loading applications...",
                        modifier = Modifier.padding(start = 10.dp),
                        color = PlaceholderColor,
                        fontSize = 13.sp
                    )
                }
            }

            if (applicationsError != null) {
                Text(
                    text = applicationsError,
                    color = SearchTextColor,
                    fontSize = 13.sp
                )

                TextButton(onClick = onRetryApplications) {
                    Text(
                        text = "Retry",
                        color = SearchButtonColor
                    )
                }
            }

            // Subscription status
            Text(
                text = "Status",
                color = SearchTextColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

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

            Spacer(modifier = Modifier.height(4.dp))

            // Search
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
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SearchButtonColor,
                    contentColor = SearchButtonTextColor
                )
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )

                Text(
                    text = "Search",
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
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
            .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = SearchButtonColor,
                unselectedColor = PlaceholderColor
            )
        )

        Text(
            text = label,
            color = SearchTextColor,
            fontSize = 14.sp
        )
    }
}
