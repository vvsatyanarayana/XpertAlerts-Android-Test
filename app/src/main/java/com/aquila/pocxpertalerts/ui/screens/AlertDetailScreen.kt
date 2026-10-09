package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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


// =========================================================
// COLORS
// =========================================================

private val XpertOrange = Color(0xFFED741C)
private val XpertPurple = Color(0xFF3F237D)
private val ScreenBackground = Color(0xFFF7F7F7)
private val DarkText = Color(0xFF222222)
private val GrayText = Color(0xFF777777)
private val LightOrange = Color(0xFFFFF1E8)


// =========================================================
// ALERT DETAIL SCREEN
// =========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailScreen(
    alertId: String,
    onBackClick: () -> Unit
) {

    // =====================================================
    // TEMPORARY ACTION STATE
    // =====================================================

    var showMenu by remember {
        mutableStateOf(false)
    }

    var selectedAction by remember {
        mutableStateOf<String?>(null)
    }


    // =====================================================
    // SCREEN
    // =====================================================

    Scaffold(

        containerColor = ScreenBackground,

        topBar = {

            TopAppBar(

                // ---------------------------------------------
                // SUBJECT IN TOOLBAR
                // ---------------------------------------------

                title = {

                    Text(
                        text = "System Alert",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = XpertPurple,
                        maxLines = 1
                    )
                },


                // ---------------------------------------------
                // BACK BUTTON
                // ---------------------------------------------

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


                // ---------------------------------------------
                // DYNAMIC ACTION MENU
                // ---------------------------------------------

                actions = {

                    Box {

                        IconButton(
                            onClick = {
                                showMenu = true
                            }
                        ) {

                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Alert actions",
                                tint = XpertPurple
                            )
                        }


                        // =====================================
                        // OVERFLOW MENU
                        // =====================================

                        DropdownMenu(
                            expanded = showMenu,

                            onDismissRequest = {
                                showMenu = false
                            }
                        ) {

                            /*
                             * TEMPORARY ACTIONS
                             *
                             * Later these captions will come
                             * from getAlertButtonDetails API.
                             */


                            DropdownMenuItem(

                                text = {
                                    Text(
                                        text = "Mark as read"
                                    )
                                },

                                onClick = {

                                    showMenu = false

                                    selectedAction =
                                        "Mark as read"
                                }
                            )


                            DropdownMenuItem(

                                text = {
                                    Text(
                                        text = "Archive"
                                    )
                                },

                                onClick = {

                                    showMenu = false

                                    selectedAction =
                                        "Archive"
                                }
                            )


                            DropdownMenuItem(

                                text = {
                                    Text(
                                        text = "Forward"
                                    )
                                },

                                onClick = {

                                    showMenu = false

                                    selectedAction =
                                        "Forward"
                                }
                            )
                        }
                    }
                },


                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }

    ) { innerPadding ->


        // =====================================================
        // MAIN CONTENT
        // =====================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 18.dp
                )
        ) {


            // =================================================
            // ALERT HEADER
            // =================================================

            AlertHeader()


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // =================================================
            // MESSAGE
            // =================================================

            MessageCard()


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // =================================================
            // ALERT INFORMATION
            // =================================================

            AlertInformationCard(
                alertId = alertId
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }


    // =========================================================
    // CONFIRMATION DIALOG
    // =========================================================

    if (selectedAction != null) {

        AlertActionConfirmationDialog(

            action = selectedAction!!,

            onConfirm = {

                /*
                 * TEMPORARY
                 *
                 * Later this will call:
                 *
                 * processActions API
                 */

                selectedAction = null
            },

            onDismiss = {

                selectedAction = null
            }
        )
    }
}


// =========================================================
// ALERT HEADER
// =========================================================

@Composable
private fun AlertHeader() {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            // ---------------------------------------------
            // ICON
            // ---------------------------------------------

            Surface(

                modifier = Modifier.size(52.dp),

                shape = RoundedCornerShape(14.dp),

                color = LightOrange
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Notifications,

                        contentDescription = "Alert",

                        tint = XpertOrange,

                        modifier = Modifier.size(29.dp)
                    )
                }
            }


            Spacer(
                modifier = Modifier.width(14.dp)
            )


            // ---------------------------------------------
            // SUBJECT + DATE
            // ---------------------------------------------

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "System Alert",

                    fontSize = 20.sp,

                    fontWeight = FontWeight.Bold,

                    color = DarkText
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "Today • 10 min ago",

                    fontSize = 13.sp,

                    color = GrayText
                )
            }
        }
    }
}


// =========================================================
// MESSAGE CARD
// =========================================================

@Composable
private fun MessageCard() {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Text(
                text = "Message",

                fontSize = 17.sp,

                fontWeight = FontWeight.Bold,

                color = XpertPurple
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            // =================================================
            // FULL MESSAGE BODY
            // =================================================

            Text(

                text = """
                    New alert received from Xpert Alerts.

                    Please review this alert and take the required action if necessary.

                    This is the complete message area for the alert. When the backend is connected, the actual message content returned by the Xpert Alerts server will be displayed here.

                    The message can contain multiple paragraphs and can be longer than the preview shown on the Alerts screen.
                """.trimIndent(),

                fontSize = 15.sp,

                lineHeight = 24.sp,

                color = DarkText
            )
        }
    }
}


// =========================================================
// ALERT INFORMATION
// =========================================================

@Composable
private fun AlertInformationCard(
    alertId: String
) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Text(
                text = "Alert Information",

                fontSize = 17.sp,

                fontWeight = FontWeight.Bold,

                color = XpertPurple
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            DetailRow(
                label = "Alert ID",
                value = alertId
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            DetailRow(
                label = "Status",
                value = "Unread"
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            DetailRow(
                label = "Received",
                value = "Today"
            )
        }
    }
}


// =========================================================
// DETAIL ROW
// =========================================================

@Composable
private fun DetailRow(
    label: String,
    value: String
) {

    Row(

        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,

            modifier = Modifier.weight(1f),

            fontSize = 14.sp,

            color = GrayText
        )


        Text(
            text = value,

            fontSize = 14.sp,

            fontWeight = FontWeight.Medium,

            color = DarkText
        )
    }
}


// =========================================================
// ACTION CONFIRMATION DIALOG
// =========================================================

@Composable
private fun AlertActionConfirmationDialog(
    action: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    AlertDialog(

        onDismissRequest = onDismiss,

        icon = {

            Icon(
                imageVector = Icons.Default.Warning,

                contentDescription = "Confirmation",

                tint = XpertOrange,

                modifier = Modifier.size(32.dp)
            )
        },

        title = {

            Text(
                text = "Confirm Action",

                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Text(
                text = "Are you sure you want to \"$action\" this alert?"
            )
        },

        confirmButton = {

            TextButton(
                onClick = onConfirm
            ) {

                Text(
                    text = "Confirm",
                    color = XpertOrange
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    text = "Cancel"
                )
            }
        }
    )
}


// =========================================================
// PREVIEW
// =========================================================

@Preview(
    showBackground = true,
    showSystemUi = true,

)
@Composable
fun AlertDetailScreenPreview() {

    com.aquila.pocxpertalerts.ui.XpertAlertsTheme {

        AlertDetailScreen(
            alertId = "1",
            onBackClick = {}
        )
    }
}