package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
// FORWARD ALERT MODEL
// =========================================================

data class ForwardAlertItem(
    val id: String,
    val name: String,
    val recipient: String,
    val description: String
)


// =========================================================
// FORWARD ALERTS SCREEN
// =========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardAlertsScreen(
    onBackClick: () -> Unit,
    onCreateClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onSearchClick: () -> Unit
) {

    var searchText by remember {
        mutableStateOf("")
    }

    var showSearch by remember {
        mutableStateOf(false)
    }

    var selectedIds by remember {
        mutableStateOf(setOf<String>())
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }


    // =====================================================
    // MOCK DATA
    // =====================================================

    val forwardAlerts = remember {

        listOf(

            ForwardAlertItem(
                id = "1",
                name = "Operations Alerts",
                recipient = "operations@example.com",
                description = "Forward system alerts to Operations."
            ),

            ForwardAlertItem(
                id = "2",
                name = "Support Alerts",
                recipient = "support@example.com",
                description = "Forward important support notifications."
            ),

            ForwardAlertItem(
                id = "3",
                name = "Administrator Alerts",
                recipient = "admin@example.com",
                description = "Forward critical alerts to Administrator."
            )
        )
    }


    // =====================================================
    // FILTER DATA
    // =====================================================

    val filteredAlerts = forwardAlerts.filter { alert ->

        alert.name.contains(
            searchText,
            ignoreCase = true
        ) ||
                alert.recipient.contains(
                    searchText,
                    ignoreCase = true
                )
    }


    val allSelected =
        filteredAlerts.isNotEmpty() &&
                filteredAlerts.all {
                    selectedIds.contains(it.id)
                }


    Scaffold(

        modifier = Modifier.fillMaxSize(),

        containerColor =
            ScreenBackground,

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Forward Alerts",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
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

                actions = {

                    IconButton(
                        onClick  = onSearchClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Search,

                            contentDescription =
                                "Search"
                        )
                    }


                    IconButton(
                        onClick = onCreateClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Add,

                            contentDescription =
                                "Create Forward Alert"
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
                            XpertPurple,

                        actionIconContentColor =
                            XpertPurple
                    )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {


            // =================================================
            // SEARCH
            // =================================================

            if (showSearch) {

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),

                    color =
                        Color.White
                ) {

                    OutlinedTextField(

                        value =
                            searchText,

                        onValueChange = {
                            searchText = it
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 10.dp
                            ),

                        singleLine = true,

                        label = {
                            Text("Search Forward Alerts")
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Search,

                                contentDescription =
                                    "Search"
                            )
                        }
                    )
                }
            }


            // =================================================
            // SELECTION TOOLBAR
            // =================================================

            if (selectedIds.isNotEmpty()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            "${selectedIds.size} selected",

                        modifier =
                            Modifier.weight(1f),

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.Medium,

                        color =
                            DarkText
                    )


                    IconButton(
                        onClick = {
                            showDeleteDialog = true
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Delete,

                            contentDescription =
                                "Delete Selected",

                            tint =
                                XpertOrange
                        )
                    }
                }

                HorizontalDivider()
            }


            // =================================================
            // SELECT ALL
            // =================================================

            if (filteredAlerts.isNotEmpty()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .clickable {

                            selectedIds =
                                if (allSelected) {

                                    emptySet()

                                } else {

                                    filteredAlerts
                                        .map { it.id }
                                        .toSet()
                                }
                        }
                        .padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Checkbox(

                        checked =
                            allSelected,

                        onCheckedChange = {

                            selectedIds =
                                if (it) {

                                    filteredAlerts
                                        .map { alert ->
                                            alert.id
                                        }
                                        .toSet()

                                } else {

                                    emptySet()
                                }
                        }
                    )

                    Text(
                        text = "Select All",

                        fontSize =
                            14.sp,

                        color =
                            DarkText
                    )
                }

                HorizontalDivider()
            }


            // =================================================
            // LIST
            // =================================================

            if (filteredAlerts.isEmpty()) {

                ForwardAlertsEmptyState()

            } else {

                LazyColumn(

                    modifier =
                        Modifier.fillMaxSize(),

                    contentPadding =
                        PaddingValues(
                            horizontal = 16.dp,
                            vertical = 16.dp
                        ),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    item {

                        Column {

                            Text(
                                text =
                                    "Forward Alerts",

                                fontSize =
                                    24.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    DarkText
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    "Manage alerts that are forwarded to recipients.",

                                fontSize =
                                    14.sp,

                                color =
                                    GrayText
                            )
                        }
                    }


                    items(
                        items =
                            filteredAlerts,

                        key = {
                            it.id
                        }
                    ) { alert ->

                        ForwardAlertCard(

                            alert =
                                alert,

                            selected =
                                selectedIds.contains(
                                    alert.id
                                ),

                            onSelectedChange = {

                                selectedIds =
                                    if (
                                        selectedIds.contains(
                                            alert.id
                                        )
                                    ) {

                                        selectedIds -
                                                alert.id

                                    } else {

                                        selectedIds +
                                                alert.id
                                    }
                            },

                            onEditClick = {

                                onEditClick(
                                    alert.id
                                )
                            }
                        )
                    }
                }
            }
        }
    }


    // =====================================================
    // DELETE CONFIRMATION
    // =====================================================

    if (showDeleteDialog) {

        AlertDialog(

            onDismissRequest = {
                showDeleteDialog = false
            },

            icon = {

                Icon(
                    imageVector =
                        Icons.Default.Warning,

                    contentDescription =
                        "Warning",

                    tint =
                        XpertOrange
                )
            },

            title = {

                Text(
                    text =
                        "Delete Forward Alerts?"
                )
            },

            text = {

                Text(
                    text =
                        "Are you sure you want to delete the selected forward alert(s)?"
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        // UI only for now.
                        selectedIds =
                            emptySet()

                        showDeleteDialog =
                            false
                    },

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                XpertOrange
                        )
                ) {

                    Text("Delete")
                }
            },

            dismissButton = {

                OutlinedButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }
}


// =========================================================
// FORWARD ALERT CARD
// =========================================================

@Composable
private fun ForwardAlertCard(
    alert: ForwardAlertItem,
    selected: Boolean,
    onSelectedChange: () -> Unit,
    onEditClick: () -> Unit
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (selected) {

                        XpertOrange.copy(
                            alpha = 0.08f
                        )

                    } else {

                        Color.White
                    }
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            // =================================================
            // TOP ROW
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Checkbox(

                    checked =
                        selected,

                    onCheckedChange = {
                        onSelectedChange()
                    }
                )


                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )


                Surface(

                    modifier =
                        Modifier.size(44.dp),

                    shape =
                        RoundedCornerShape(12.dp),

                    color =
                        XpertOrange.copy(
                            alpha = 0.12f
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Search,

                        contentDescription =
                            "Forward Alert",

                        tint =
                            XpertOrange,

                        modifier =
                            Modifier
                                .padding(10.dp)
                                .size(24.dp)
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(12.dp)
                )


                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            alert.name,

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            DarkText
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            alert.recipient,

                        fontSize =
                            13.sp,

                        color =
                            XpertPurple
                    )
                }


                IconButton(
                    onClick =
                        onEditClick
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Edit,

                        contentDescription =
                            "Edit",

                        tint =
                            XpertPurple
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            // =================================================
            // DESCRIPTION
            // =================================================

            Text(
                text =
                    alert.description,

                fontSize =
                    13.sp,

                color =
                    GrayText,

                modifier =
                    Modifier.padding(
                        start = 48.dp
                    )
            )
        }
    }
}


// =========================================================
// EMPTY STATE
// =========================================================

@Composable
private fun ForwardAlertsEmptyState() {

    Box(
        modifier =
            Modifier.fillMaxSize(),

        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,

            modifier =
                Modifier.padding(24.dp)
        ) {

            Surface(

                modifier =
                    Modifier.size(80.dp),

                shape =
                    CircleShape,

                color =
                    XpertOrange.copy(
                        alpha = 0.10f
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Search,

                    contentDescription =
                        "No Forward Alerts",

                    tint =
                        XpertOrange,

                    modifier =
                        Modifier
                            .padding(22.dp)
                            .size(36.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            Text(
                text =
                    "No records found",

                fontSize =
                    21.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    DarkText
            )


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            Text(
                text =
                    "No forward alerts are available.",

                fontSize =
                    14.sp,

                color =
                    GrayText
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
fun ForwardAlertsScreenPreview() {

    XpertAlertsTheme {

        ForwardAlertsScreen(
            onBackClick = {},
            onCreateClick = {},
            onEditClick = {},
            onSearchClick = {}
        )
    }
}