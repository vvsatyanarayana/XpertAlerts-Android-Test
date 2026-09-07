package com.aquila.pocxpertalerts.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aquila.pocxpertalerts.ui.screens.HomeScreen
import com.aquila.pocxpertalerts.ui.screens.LoginScreen
import com.aquila.pocxpertalerts.ui.screens.SettingsScreen

sealed class Screen(val route: String) {

    data object Login : Screen("login")

    data object Home : Screen("home")

    data object Settings : Screen("settings")
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        // ----------------------------------------------------
        // LOGIN
        // ----------------------------------------------------

        composable(Screen.Login.route) {

            LoginScreen(

                onLoginClick = { userId, password ->

                    navController.navigate(Screen.Home.route) {

                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },

                onSettingsClick = {

                    navController.navigate(Screen.Settings.route)
                }
            )
        }


        // ----------------------------------------------------
        // HOME
        // ----------------------------------------------------

        composable(Screen.Home.route) {

            HomeScreen()
        }


        // ----------------------------------------------------
        // SETTINGS
        // ----------------------------------------------------

        composable(Screen.Settings.route) {

            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}