package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.theme.BorderGray
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertGreen
import com.aquila.pocxpertalerts.ui.theme.XpertOrange

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {

    // ============================================================
    // UI STATE ONLY
    // ============================================================

    var webServiceUrl by remember {
        mutableStateOf(
            "http://192.168.10.174:7002/ams"
        )
    }

    // ============================================================
    // ROOT
    // ============================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {

        // ========================================================
        // TOP BAR
        // ========================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(59.dp)
                .background(XpertOrange)
                .padding(horizontal = 4.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            // ----------------------------------------------------
            // BACK BUTTON
            // ----------------------------------------------------

            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(48.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = White,
                    modifier = Modifier.size(26.dp)
                )
            }

            // ----------------------------------------------------
            // TITLE
            // ----------------------------------------------------

            Text(
                text = "Alerts Management System",
                color = White,
                fontSize = 16.sp,
                modifier = Modifier.weight(2.5f)
            )
        }

        // ========================================================
        // CONTENT
        // ========================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {

            // ----------------------------------------------------
            // URL FIELD
            // ----------------------------------------------------

            Spacer(
                modifier = Modifier.height(34.dp)
            )

            OutlinedTextField(
                value = webServiceUrl,

                onValueChange = {
                    webServiceUrl = it
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),

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

            // ----------------------------------------------------
            // BUTTONS
            // ----------------------------------------------------

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // =================================================
                // TEST BUTTON
                // =================================================

                Button(
                    onClick = {
                        // UI ONLY
                    },

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

                    Text(
                        text = "Test",
                        fontSize = 14.sp
                    )
                }

                // =================================================
                // SAVE BUTTON
                // =================================================

                Button(
                    onClick = {
                        // UI ONLY
                    },

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

                    Text(
                        text = "Save",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// ============================================================
// PREVIEW
// ============================================================

@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 366,
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