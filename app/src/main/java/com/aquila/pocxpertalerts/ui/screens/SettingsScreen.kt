package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.viewmodel.settings.SettingsViewModel

private val XpertPurple = Color(0xFF4B248C)
private val BackgroundColor = Color(0xFFF7F7FA)
private val TextDark = Color(0xFF202124)
private val TextGray = Color(0xFF777777)

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(20.dp)
    ) {

        // ----------------------------------------------------
        // HEADER
        // ----------------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            IconButton(
                onClick = onBackClick
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = XpertPurple
                )
            }

            Text(
                text = "Settings",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        // ----------------------------------------------------
        // DESCRIPTION
        // ----------------------------------------------------

        Text(
            text = "Server Configuration",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Configure the Xpert Alerts web service URL",
            fontSize = 14.sp,
            color = TextGray
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // ----------------------------------------------------
        // URL LABEL
        // ----------------------------------------------------

        Text(
            text = "Web Service URL",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark
        )

        Spacer(
            modifier = Modifier.height(7.dp)
        )

        // ----------------------------------------------------
        // URL FIELD
        // ----------------------------------------------------

        OutlinedTextField(
            value = uiState.webServiceUrl,

            onValueChange = {
                viewModel.updateWebServiceUrl(it)
            },

            modifier = Modifier.fillMaxWidth(),

            singleLine = true,

            placeholder = {
                Text(
                    text = "Enter web service URL",
                    color = TextGray,
                    fontSize = 14.sp
                )
            },

            isError = uiState.errorMessage != null,

            supportingText = {

                uiState.errorMessage?.let { message ->

                    Text(
                        text = message,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ----------------------------------------------------
        // TEST CONNECTION BUTTON
        // ----------------------------------------------------

        Button(
            onClick = {
                viewModel.testUrl()
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),

            enabled = !uiState.isTesting,

            colors = ButtonDefaults.buttonColors(
                containerColor = XpertPurple
            )
        ) {

            Text(
                text = if (uiState.isTesting) {
                    "TESTING..."
                } else {
                    "TEST CONNECTION"
                },

                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        // ----------------------------------------------------
        // SUCCESS MESSAGE
        // ----------------------------------------------------

        if (uiState.isUrlValid == true) {

            Text(
                text = "Server URL is valid",
                color = Color(0xFF16833B),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // ----------------------------------------------------
        // INVALID URL
        // ----------------------------------------------------

        if (
            uiState.isUrlValid == false &&
            uiState.errorMessage == null
        ) {

            Text(
                text = "Unable to connect to server",
                color = Color.Red,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


// ------------------------------------------------------------
// PREVIEW
// ------------------------------------------------------------

@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 360,
    heightDp = 760
)
@Composable
fun SettingsScreenPreview() {

    XpertAlertsTheme {

        SettingsScreen(
            onBackClick = {}
        )
    }
}