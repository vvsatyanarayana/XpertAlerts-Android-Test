package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme

private val ArchivedPurple = Color(0xFF6A4BBC)
private val ArchivedOrange = Color(0xFFFF8A00)
private val ArchivedDarkText = Color(0xFF222222)
private val ArchivedGrayText = Color(0xFF777777)

data class ArchivedAlertItem(
    val id: String,
    val subject: String,
    val message: String,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchivedScreen(
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAlertClick: (String) -> Unit
) {
    val archivedAlerts = listOf(
        ArchivedAlertItem(
            id = "archived_1",
            subject = "System Alert",
            message = "Previous system alert has been archived.",
            time = "Yesterday"
        ),
        ArchivedAlertItem(
            id = "archived_2",
            subject = "System Alert",
            message = "System maintenance notification.",
            time = "2 days ago"
        ),
        ArchivedAlertItem(
            id = "archived_3",
            subject = "Important Update",
            message = "Previous important update notification.",
            time = "3 days ago"
        ),
        ArchivedAlertItem(
            id = "archived_4",
            subject = "New Information",
            message = "Archived information update.",
            time = "5 days ago"
        )
    )

    val groupedAlerts = archivedAlerts.groupBy { it.subject }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Archived Alerts",
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
                },
                actions = {
                    IconButton(
                        onClick = onSearchClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Archived Alerts",
                            tint = ArchivedPurple
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
        ) {

            ArchivedInfoBar()

            if (groupedAlerts.isEmpty()) {
                ArchivedEmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    groupedAlerts.forEach { (subject, alerts) ->

                        item(
                            key = "archived_subject_$subject"
                        ) {
                            Text(
                                text = subject.uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ArchivedPurple,
                                modifier = Modifier.padding(
                                    top = 8.dp,
                                    bottom = 2.dp
                                )
                            )
                        }

                        items(
                            items = alerts,
                            key = { it.id }
                        ) { alert ->

                            ArchivedAlertCard(
                                alert = alert,
                                onClick = {
                                    onAlertClick(alert.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchivedInfoBar() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = ArchivedPurple.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Text(
                text = "Archive Information",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ArchivedDarkText
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Last Archived On",
                        fontSize = 12.sp,
                        color = ArchivedGrayText
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = "Not available",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ArchivedDarkText
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Last Purged On",
                        fontSize = 12.sp,
                        color = ArchivedGrayText
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = "Not available",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ArchivedDarkText
                    )
                }
            }
        }
    }
}

@Composable
private fun ArchivedAlertCard(
    alert: ArchivedAlertItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            SurfaceAlertIcon()

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = alert.message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ArchivedDarkText
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = alert.time,
                    fontSize = 12.sp,
                    color = ArchivedOrange,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SurfaceAlertIcon() {
    Box(
        modifier = Modifier
            .size(46.dp)
            .background(
                color = ArchivedOrange.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.ic_alert_list_bell
            ),
            contentDescription = "Archived Alert",
            modifier = Modifier
                .padding(9.dp)
                .size(28.dp)
        )
    }
}

@Composable
private fun ArchivedEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No records found",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ArchivedDarkText
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "There are no archived alerts.",
                fontSize = 14.sp,
                color = ArchivedGrayText
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,

)
@Composable
fun ArchivedScreenPreview() {
    XpertAlertsTheme {
        ArchivedScreen(
            onBackClick = {},
            onSearchClick = {},
            onAlertClick = {}
        )
    }
}