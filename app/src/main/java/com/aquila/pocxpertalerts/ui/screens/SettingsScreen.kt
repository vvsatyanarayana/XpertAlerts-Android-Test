
package com.aquila.pocxpertalerts.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.theme.BorderGray
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertGreen
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import com.aquila.pocxpertalerts.viewmodel.SettingsViewModel
import com.aquila.pocxpertalerts.viewmodel.SettingsViewModelFactory

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext

    val settingsDataStore = remember(context) {
        SettingsDataStore(context)
    }

    val factory = remember(settingsDataStore) {
        SettingsViewModelFactory(settingsDataStore)
    }

    val viewModel: SettingsViewModel = viewModel(
        factory = factory
    )

    val uiState by viewModel.uiState.collectAsState()

    // Display messages as popup Toasts
    // Display messages as popup Toasts
    // Display messages as popup Toasts
    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            val displayMessage = message
                .replace(Regex("\\s*\\(HTTP\\s*\\d+\\)"), "")
                .replace(Regex("\\s*\\d{3}\\s+OK"), "")
                .trim()

            if (displayMessage.isNotBlank()) {
                Toast.makeText(
                    context,
                    displayMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {

        // Top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(69.dp)
                .background(XpertOrange)
        ) {
            // Back button slightly lower
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 4.dp)
                    .offset(y = 5.dp)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = White,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Title centered across the full toolbar
            Text(
                text = "Alerts Management System",
                color = White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 56.dp)
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Spacer(
                modifier = Modifier.height(34.dp)
            )

            // Server URL field
            OutlinedTextField(
                value = uiState.webServiceUrl,
                onValueChange = viewModel::onUrlChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = TextGray
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Server URL",
                        tint = BorderGray,
                        modifier = Modifier.size(18.dp)
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = androidx.compose.material3
                    .OutlinedTextFieldDefaults
                    .colors(
                        focusedBorderColor = BorderGray,
                        unfocusedBorderColor = BorderGray,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        cursorColor = XpertOrange
                    )
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // Test and Save buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.testUrl()
                    },
                    enabled = !uiState.isTesting &&
                            !uiState.isSaving,
                    modifier = Modifier
                        .weight(1f)
                        .height(35.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = XpertGreen,
                        contentColor = White
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (uiState.isTesting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Test",
                            fontSize = 14.sp
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.saveUrl()
                    },
                    enabled = !uiState.isSaving &&
                            !uiState.isTesting,
                    modifier = Modifier
                        .weight(1f)
                        .height(35.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = XpertOrange,
                        contentColor = White
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save",
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // No message Text is needed here.
            // Messages are displayed through Toast popups.
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SettingsScreenPreview() {
    XpertAlertsTheme {
        SettingsScreen(
            onBackClick = {}
        )
    }
}
