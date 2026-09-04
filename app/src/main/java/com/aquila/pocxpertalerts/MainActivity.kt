package com.aquila.pocxpertalerts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.screens.HomeScreen
import com.aquila.pocxpertalerts.ui.screens.LoginScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            XpertAlertsTheme {
                XpertAlertsApp()
            }
        }
    }
}

@Composable
fun XpertAlertsApp() {

    var showSplash by remember {
        mutableStateOf(true)
    }

    var showLogin by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {

        delay(2000)

        showSplash = false
    }

    when {

        // Splash Screen
        showSplash -> {
            SplashScreen()
        }

        // Login Screen
        showLogin -> {
            LoginScreen(
                onLoginClick = { userId, password ->

                    // Login API will be added later
                    showLogin = false
                },

                onSettingsClick = {

                    // Settings screen will be added later
                }
            )
        }

        // Home Screen
        else -> {
            HomeScreen()
        }
    }
}

@Composable
fun SplashScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),

        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.xpertalerts_splash_logo
            ),

            contentDescription = "Xpert Alerts Logo",

            modifier = Modifier.fillMaxSize(),

            contentScale = ContentScale.Fit
        )
    }
}

