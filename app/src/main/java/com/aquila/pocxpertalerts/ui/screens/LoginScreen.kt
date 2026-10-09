package com.aquila.pocxpertalerts.ui.screens

import android.provider.Settings
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.BorderGray
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import com.aquila.pocxpertalerts.viewmodel.LoginViewModel
import com.aquila.pocxpertalerts.viewmodel.LoginViewModelFactory

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSettingsClick: () -> Unit
) {

    // =========================================================
    // VIEWMODEL
    // =========================================================

    val context = LocalContext.current.applicationContext

    val settingsDataStore = remember(context) {
        SettingsDataStore(context)
    }

    val loginFactory = remember(settingsDataStore) {
        LoginViewModelFactory(settingsDataStore)
    }

    val viewModel: LoginViewModel = viewModel(
        factory = loginFactory
    )

    val uiState by viewModel.uiState.collectAsState()


    // =========================================================
    // ANDROID CONTEXT
    // =========================================================


    // =========================================================
    // DEVICE ID
    // =========================================================

    val deviceId = remember {

        Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }


    // =========================================================
    // PASSWORD VISIBILITY
    // =========================================================

    var passwordVisible by remember {
        mutableStateOf(false)
    }


    // =========================================================
    // LOGIN SUCCESS
    // =========================================================

    LaunchedEffect(uiState.loginSuccess) {

        if (uiState.loginSuccess) {

            onLoginSuccess()

            viewModel.clearLoginSuccess()
        }
    }


    // =========================================================
    // ROOT
    // =========================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {


            // =================================================
            // SETTINGS
            // =================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 56.dp,
                        end = 14.dp
                    ),

                horizontalArrangement =
                    Arrangement.End
            ) {

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(60.dp)
                ) {

                    Image(
                        painter = painterResource(
                            id = R.drawable.gear
                        ),

                        contentDescription =
                            "Settings",

                        modifier =
                            Modifier.size(33.dp)
                    )
                }
            }


            // =================================================
            // LOGO
            // =================================================

            Spacer(
                modifier =
                    Modifier.height(35.dp)
            )

            Image(
                painter = painterResource(
                    id =
                        R.drawable.xpertalerts_splash_logo
                ),

                contentDescription =
                    "Xpert Alerts Logo",

                modifier = Modifier
                    .width(400.dp)
                    .height(180.dp),

                contentScale =
                    ContentScale.Fit
            )


            // =================================================
            // SPACE AFTER LOGO
            // =================================================

            Spacer(
                modifier =
                    Modifier.height(25.dp)
            )


            // =================================================
            // USER ID
            // =================================================

            OutlinedTextField(

                value =
                    uiState.userId,

                onValueChange = {
                    viewModel.updateUserId(it)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(53.dp),

                singleLine = true,

                placeholder = {

                    Text(
                        text = "User ID",
                        fontSize = 12.sp,
                        color = TextDark
                    )
                },

                leadingIcon = {

                    Image(
                        painter = painterResource(
                            id = R.drawable.user
                        ),

                        contentDescription =
                            "User ID",

                        modifier =
                            Modifier.size(18.dp)
                    )
                },

                isError =
                    uiState.userIdError != null,

                shape =
                    RoundedCornerShape(25.dp),

                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedBorderColor =
                            BorderGray,

                        unfocusedBorderColor =
                            if (
                                uiState.userIdError != null
                            ) {
                                androidx.compose.ui.graphics.Color.Red
                            } else {
                                BorderGray
                            },

                        errorBorderColor =
                            androidx.compose.ui.graphics.Color.Red,

                        focusedContainerColor =
                            White,

                        unfocusedContainerColor =
                            White,

                        errorContainerColor =
                            White,

                        cursorColor =
                            XpertOrange
                    )
            )


            // =================================================
            // USER ID ERROR
            // =================================================

            if (uiState.userIdError != null) {

                Text(
                    text =
                        uiState.userIdError!!,

                    fontSize = 12.sp,

                    color =
                        androidx.compose.ui.graphics.Color.Red,

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 8.dp,
                            top = 4.dp
                        )
                )
            }


            // =================================================
            // SPACE
            // =================================================

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            // =================================================
            // PASSWORD
            // =================================================

            OutlinedTextField(

                value =
                    uiState.password,

                onValueChange = {
                    viewModel.updatePassword(it)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(53.dp),

                singleLine = true,

                placeholder = {

                    Text(
                        text = "Password",
                        fontSize = 12.sp,
                        color = TextDark
                    )
                },

                leadingIcon = {

                    Image(
                        painter = painterResource(
                            id = R.drawable.lock
                        ),

                        contentDescription =
                            "Password",

                        modifier =
                            Modifier.size(18.dp)
                    )
                },

                trailingIcon = {

                    IconButton(
                        onClick = {

                            passwordVisible =
                                !passwordVisible
                        }
                    ) {

                        Image(
                            painter = painterResource(
                                id =
                                    if (passwordVisible) {
                                        R.drawable.ic_eye_show
                                    } else {
                                        R.drawable.ic_eye_hide
                                    }
                            ),

                            contentDescription =
                                if (passwordVisible) {
                                    "Hide password"
                                } else {
                                    "Show password"
                                },

                            modifier =
                                Modifier.size(20.dp)
                        )
                    }
                },

                visualTransformation =
                    if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },

                isError =
                    uiState.passwordError != null,

                shape =
                    RoundedCornerShape(25.dp),

                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedBorderColor =
                            BorderGray,

                        unfocusedBorderColor =
                            if (
                                uiState.passwordError != null
                            ) {
                                androidx.compose.ui.graphics.Color.Red
                            } else {
                                BorderGray
                            },

                        errorBorderColor =
                            androidx.compose.ui.graphics.Color.Red,

                        focusedContainerColor =
                            White,

                        unfocusedContainerColor =
                            White,

                        errorContainerColor =
                            White,

                        cursorColor =
                            XpertOrange
                    )
            )


            // =================================================
            // PASSWORD ERROR
            // =================================================

            if (uiState.passwordError != null) {

                Text(
                    text =
                        uiState.passwordError!!,

                    fontSize = 12.sp,

                    color =
                        androidx.compose.ui.graphics.Color.Red,

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 8.dp,
                            top = 4.dp
                        )
                )
            }


            // =================================================
            // SERVER ERROR
            // =================================================

            if (uiState.errorMessage != null) {

                Text(
                    text =
                        uiState.errorMessage!!,

                    fontSize = 12.sp,

                    color =
                        androidx.compose.ui.graphics.Color.Red,

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 8.dp,
                            top = 8.dp
                        )
                )
            }


            // =================================================
            // SPACE BEFORE LOGIN
            // =================================================

            Spacer(
                modifier =
                    Modifier.height(34.dp)
            )


            // =================================================
            // LOGIN BUTTON
            // =================================================

            Button(

                onClick = {

                    viewModel.login(
                        deviceId = deviceId
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(53.dp),

                shape =
                    RoundedCornerShape(20.dp),

                enabled =
                    !uiState.isLoading,

                colors =
                    ButtonDefaults.buttonColors(

                        containerColor =
                            XpertOrange,

                        contentColor =
                            White,

                        disabledContainerColor =
                            XpertOrange.copy(
                                alpha = 0.6f
                            ),

                        disabledContentColor =
                            White
                    )
            ) {

                Text(
                    text =
                        if (uiState.isLoading) {
                            "Logging in..."
                        } else {
                            "Login"
                        },

                    fontSize = 16.sp
                )
            }


            // =================================================
            // SPACE BEFORE VERSION
            // =================================================

            Spacer(
                modifier =
                    Modifier.height(75.dp)
            )


            // =================================================
            // VERSION
            // =================================================

            Text(
                text =
                    "App Version : v1.6",

                fontSize = 14.sp,

                color =
                    TextDark
            )
        }
    }
}


// =============================================================
// PREVIEW
// =============================================================

@Preview(
    showBackground = true,
    showSystemUi = true,

)
@Composable
fun LoginScreenPreview() {

    XpertAlertsTheme {

        LoginScreen(
            onLoginSuccess = {},
            onSettingsClick = {}
        )
    }
}