package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange

@Composable
fun AboutScreen(
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {

        // -------------------------
        // Top Bar
        // -------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(69.dp)
                .background(XpertOrange)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier

                    .padding(start = 2.dp)
                    .padding(top = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = White
                )
            }

            Text(
                text = "About",
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.Center)

            )
        }

        // -------------------------
        // Main Content
        // -------------------------
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.xpertalerts_splash_logo
                ),
                contentDescription = "Xpert Alerts Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
                    .height(180.dp),
                contentScale = ContentScale.Fit
            )
        }

        // -------------------------
        // Bottom Information
        // -------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(49.dp)
                .background(XpertOrange),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "App Version : v1.6",
                color = White,
                fontSize = 11.sp
            )

            Text(
                text = "Copyright © 2020 Aquila Software Inc.",
                color = White,
                fontSize = 11.sp
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,

    )
@Composable
private fun AboutScreenPreview() {
    XpertAlertsTheme {
        AboutScreen()
    }
}