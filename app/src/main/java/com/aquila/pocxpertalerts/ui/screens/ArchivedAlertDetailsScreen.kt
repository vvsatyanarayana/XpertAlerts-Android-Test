
package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DetailsOrange = Color(0xFFED741C)
private val DetailsGray = Color(0xFF757575)

@Composable
fun ArchivedAlertDetailsScreen(
    alert: ArchivedAlertUi,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Orange toolbar with centered heading
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(DetailsOrange)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Archived Alert Details",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 52.dp),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Alert title
            Text(
                text = alert.title,
                color = DetailsOrange,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(color = Color.LightGray)

            // Date
            DetailField(
                label = "Date",
                value = alert.date
            )

            // Subject
            DetailField(
                label = "Subject",
                value = alert.subject
            )

            // Description
            DetailField(
                label = "Description",
                value = alert.description.ifBlank { "Not available" }
            )

            // Priority
            DetailField(
                label = "Priority",
                value = alert.priority
            )

            // Current status
            DetailField(
                label = "Current Status",
                value = alert.currentStatus.ifBlank { "Not available" }
            )

            // Application URL
            DetailField(
                label = "Application URL",
                value = alert.applicationUrl.ifBlank { "Not available" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onDeleteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DetailsOrange
                )
            ) {
                Text(
                    text = "Delete",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DetailField(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            color = DetailsGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}

// Preview uses sample content only inside Android Studio.
@Preview(
    name = "Archived Alert Details",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=873dp,dpi=420"
)
@Composable
private fun ArchivedAlertDetailsScreenPreview() {
    MaterialTheme {
        ArchivedAlertDetailsScreen(
            alert = ArchivedAlertUi(
                id = "preview-1",
                title = "Application Alert",
                date = "12/03/2026",
                subject = "Server Restarted",
                description = "The server has been restarted successfully.",
                priority = "Non Urgent",
                currentStatus = "Archived",
                applicationUrl = "https://example.com"
            ),
            onBackClick = {},
            onDeleteClick = {}
        )
    }
}
