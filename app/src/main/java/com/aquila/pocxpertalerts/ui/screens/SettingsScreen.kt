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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.viewmodel.settings.SettingsViewModel


// ------------------------------------------------------------
// PREMIUM XPERT ALERTS COLORS
// ------------------------------------------------------------

private val RoyalPurple = Color(0xFF4B248C)
private val DeepPurple = Color(0xFF321461)
private val LightPurple = Color(0xFFF3EFFA)

private val XpertOrange = Color(0xFFED741C)
private val LightOrange = Color(0xFFFFF2E9)

private val SuccessGreen = Color(0xFF008F5A)
private val LightGreen = Color(0xFFE9F7F1)

private val Background = Color(0xFFF7F5FA)
private val White = Color.White

private val TextDark = Color(0xFF211A2B)
private val TextGray = Color(0xFF77727D)
private val BorderGray = Color(0xFFD8D3DF)


// ------------------------------------------------------------
// SETTINGS SCREEN
// ------------------------------------------------------------

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        // ====================================================
        // PREMIUM HEADER
        // ====================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .background(
                    XpertOrange
                )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(48.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = White,
                        modifier = Modifier.size(25.dp)
                    )
                }


                Column(
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .weight(1f)
                ) {

                    Text(
                        text = "Alerts Management",
                        color = White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Server Configuration",
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 12.sp
                    )
                }
            }
        }


        // ====================================================
        // CONTENT
        // ====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
        ) {

            Spacer(
                modifier = Modifier.height(24.dp)
            )


            // =================================================
            // INTRODUCTION
            // =================================================

            Text(
                text = "Web Service",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "Configure the server used by Xpert Alerts.",
                fontSize = 13.sp,
                color = TextGray
            )


            Spacer(
                modifier = Modifier.height(22.dp)
            )


            // =================================================
            // SERVER CARD
            // =================================================

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {

                    // -----------------------------------------
                    // CARD HEADER
                    // -----------------------------------------

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    LightPurple,
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Server",
                                tint = RoyalPurple,
                                modifier = Modifier.size(22.dp)
                            )
                        }


                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )


                        Column {

                            Text(
                                text = "Server URL",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Text(
                                text = "Xpert Alerts Web Service",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )


                    // -----------------------------------------
                    // URL FIELD
                    // -----------------------------------------

                    OutlinedTextField(

                        value = uiState.webServiceUrl,

                        onValueChange = { value ->

                            viewModel.updateWebServiceUrl(value)

                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),

                        singleLine = true,

                        leadingIcon = {

                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "URL",
                                tint = RoyalPurple,
                                modifier = Modifier.size(21.dp)
                            )
                        },

                        placeholder = {

                            Text(
                                text = "Enter web service URL",
                                color = TextGray,
                                fontSize = 13.sp
                            )
                        },

                        shape = RoundedCornerShape(14.dp),

                        colors = androidx.compose.material3
                            .OutlinedTextFieldDefaults.colors(

                                focusedBorderColor =
                                    RoyalPurple,

                                unfocusedBorderColor =
                                    BorderGray,

                                focusedContainerColor =
                                    Color.White,

                                unfocusedContainerColor =
                                    Color.White,

                                cursorColor =
                                    RoyalPurple
                            )
                    )


                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )


                    // -----------------------------------------
                    // TEST + SAVE
                    // -----------------------------------------

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        // =====================================
                        // TEST
                        // =====================================



                            Button(
                                onClick = {
                                    viewModel.startTest()
                                },

                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),

                            enabled =
                                !uiState.isTesting,

                            shape =
                                RoundedCornerShape(14.dp),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        XpertOrange,
                                    contentColor =
                                        White
                                )
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Wifi,
                                contentDescription =
                                    "Test",
                                modifier =
                                    Modifier.size(19.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(7.dp)
                            )

                            Text(
                                text =
                                    if (uiState.isTesting) {
                                        "Testing..."
                                    } else {
                                        "Test"
                                    },
                                fontSize = 14.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }


                        // =====================================
                        // SAVE
                        // =====================================

                        Button(

                            onClick = {

                                viewModel.saveUrl()
                            },

                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),

                            shape =
                                RoundedCornerShape(14.dp),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        XpertOrange,
                                    contentColor =
                                        White
                                )
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Save,
                                contentDescription =
                                    "Save",
                                modifier =
                                    Modifier.size(19.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(7.dp)
                            )

                            Text(
                                text = "Save",
                                fontSize = 14.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }
            }


            // =================================================
            // ERROR
            // =================================================

            if (uiState.errorMessage != null) {

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            Color(0xFFFFEEEE)
                    )
                ) {

                    Text(
                        text =
                            uiState.errorMessage ?: "",
                        color =
                            Color(0xFFC62828),
                        fontSize = 13.sp,
                        modifier =
                            Modifier.padding(14.dp)
                    )
                }
            }


            // =================================================
            // TEST SUCCESS
            // =================================================

            if (uiState.testSuccess) {

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LightGreen
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Wifi,
                            contentDescription =
                                "Connected",
                            tint =
                                SuccessGreen,
                            modifier =
                                Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(10.dp)
                        )

                        Text(
                            text =
                                "Server connection successful",
                            color =
                                SuccessGreen,
                            fontSize = 13.sp,
                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }
            }


            // =================================================
            // SAVE SUCCESS
            // =================================================

            if (uiState.saveSuccess) {

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LightOrange
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Save,
                            contentDescription =
                                "Saved",
                            tint =
                                XpertOrange,
                            modifier =
                                Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(10.dp)
                        )

                        Text(
                            text =
                                "Server URL saved successfully",
                            color =
                                XpertOrange,
                            fontSize = 13.sp,
                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(28.dp)
            )


            // =================================================
            // FOOTER
            // =================================================

            Text(
                text = "Xpert Alerts",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalPurple
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Secure server configuration",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                color = TextGray
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