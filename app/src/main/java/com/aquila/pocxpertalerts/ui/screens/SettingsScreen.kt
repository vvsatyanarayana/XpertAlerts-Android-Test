package com.aquila.pocxpertalerts.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.components.AppScreenHeading
import com.aquila.pocxpertalerts.ui.components.AppTopBar
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.BorderGray
import com.aquila.pocxpertalerts.ui.theme.TextDark
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

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = factory
    )

    val uiState by settingsViewModel.uiState.collectAsState()

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

    SettingsScreenContent(
        url = uiState.webServiceUrl,
        isTesting = uiState.isTesting,
        isSaving = uiState.isSaving,
        onUrlChange = settingsViewModel::onUrlChanged,
        onTestClick = settingsViewModel::testUrl,
        onSaveClick = settingsViewModel::saveUrl,
        onBackClick = onBackClick
    )
}

@Composable
private fun SettingsScreenContent(
    url: String,
    isTesting: Boolean,
    isSaving: Boolean,
    onUrlChange: (String) -> Unit,
    onTestClick: () -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Common toolbar: same color, height and title typography.
        AppTopBar(
            title = "Settings",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            AppScreenHeading(
                text = "Server Configuration"
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Configure the web service address used by the app.",
                fontSize = 14.sp,
                color = TextGray
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Web Service URL",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = url,
                onValueChange = onUrlChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "https://example.com/",
                        fontSize = 14.sp,
                        color = TextGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Web service URL",
                        tint = XpertOrange
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = XpertOrange,
                    unfocusedBorderColor = BorderGray,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    cursorColor = XpertOrange,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                )
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onTestClick,
                    enabled = !isTesting && !isSaving,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = XpertGreen,
                        contentColor = White,
                        disabledContainerColor =
                            XpertGreen.copy(alpha = 0.55f),
                        disabledContentColor = White
                    )
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Test",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Button(
                    onClick = onSaveClick,
                    enabled = !isSaving && !isTesting,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = XpertOrange,
                        contentColor = White,
                        disabledContainerColor =
                            XpertOrange.copy(alpha = 0.55f),
                        disabledContentColor = White
                    )
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Settings - Phone",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=852dp,dpi=440"
)
@Composable
private fun SettingsScreenPhonePreview() {
    XpertAlertsTheme {
        SettingsScreenContent(
            url = "https://example.com/webservice/",
            isTesting = false,
            isSaving = false,
            onUrlChange = {},
            onTestClick = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}

@Preview(
    name = "Settings - Small Phone",
    showBackground = true,
    device = "spec:width=320dp,height=640dp,dpi=320"
)
@Composable
private fun SettingsScreenSmallPhonePreview() {
    XpertAlertsTheme {
        SettingsScreenContent(
            url = "https://example.com/webservice/",
            isTesting = false,
            isSaving = false,
            onUrlChange = {},
            onTestClick = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}