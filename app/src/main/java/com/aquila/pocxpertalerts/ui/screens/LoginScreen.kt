package com.aquila.pocxpertalerts.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.viewmodel.login.LoginViewModel


// ============================================================
// COLORS
// ============================================================

private val XpertPurple =
    Color(0xFF4B248C)

private val XpertOrange =
    Color(0xFFED741C)

private val BackgroundColor =
    Color(0xFFF7F7FA)

private val TextDark =
    Color(0xFF202124)

private val TextGray =
    Color(0xFF777777)

private val FieldBackground =
    Color.White

private val BorderGray =
    Color(0xFFD5D5D5)


// ============================================================
// LOGIN SCREEN
// ============================================================

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {

    // --------------------------------------------------------
    // UI STATE
    // --------------------------------------------------------

    val uiState by viewModel.uiState.collectAsState()


    // --------------------------------------------------------
    // PASSWORD VISIBILITY
    // --------------------------------------------------------

    var passwordVisible by remember {
        mutableStateOf(false)
    }


    // --------------------------------------------------------
    // LOGIN SUCCESS
    // --------------------------------------------------------




    // ========================================================
    // ROOT
    // ========================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = 22.dp)
        ) {


            // =================================================
            // SETTINGS BUTTON
            // =================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 24.dp,
                        end = 2.dp
                    ),

                horizontalArrangement =
                    Arrangement.End
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White),

                    contentAlignment =
                        Alignment.Center
                ) {

                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(44.dp)
                    ) {

                        Image(
                            painter = painterResource(
                                id = R.drawable.gear
                            ),

                            contentDescription =
                                "Settings",

                            modifier =
                                Modifier.size(25.dp)
                        )
                    }
                }
            }


            // =================================================
            // LOGO
            // =================================================

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Image(
                painter = painterResource(
                    id = R.drawable.xpertalerts_splash_logo
                ),

                contentDescription =
                    "Xpert Alerts Logo",

                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .padding(
                        horizontal = 35.dp
                    )
            )


            // =================================================
            // WELCOME
            // =================================================

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Welcome Back",

                modifier =
                    Modifier.fillMaxWidth(),

                textAlign =
                    TextAlign.Center,

                fontSize = 27.sp,

                fontWeight =
                    FontWeight.Bold,

                color = TextDark
            )


            Spacer(
                modifier = Modifier.height(6.dp)
            )


            Text(
                text =
                    "Sign in to continue to Xpert Alerts",

                modifier =
                    Modifier.fillMaxWidth(),

                    textAlign =
                        TextAlign.Center,

                fontSize = 14.sp,

                color = TextGray
            )


            // =================================================
            // USER ID LABEL
            // =================================================

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Text(
                text = "User ID",

                fontSize = 13.sp,

                fontWeight =
                    FontWeight.Medium,

                color = TextDark,

                modifier =
                    Modifier.padding(
                        start = 4.dp,
                        bottom = 7.dp
                    )
            )


            // =================================================
            // USER ID FIELD
            // =================================================

            OutlinedTextField(

                value =
                    uiState.userId,

                onValueChange = {
                    viewModel.updateUserId(it)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),

                singleLine = true,

                placeholder = {

                    Text(
                        text =
                            "Enter your User ID",

                        color =
                            TextGray,

                        fontSize = 14.sp
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
                            Modifier.size(22.dp)
                    )
                },

                isError =
                    uiState.userIdError != null,

                shape =
                    RoundedCornerShape(12.dp),

                colors =
                    androidx.compose.material3
                        .OutlinedTextFieldDefaults
                        .colors(

                            focusedBorderColor =
                                XpertPurple,

                            unfocusedBorderColor =
                                if (
                                    uiState.userIdError != null
                                ) {
                                    Color.Red
                                } else {
                                    BorderGray
                                },

                            errorBorderColor =
                                Color.Red,

                            focusedContainerColor =
                                FieldBackground,

                            unfocusedContainerColor =
                                FieldBackground,

                            errorContainerColor =
                                FieldBackground,

                            cursorColor =
                                XpertPurple
                        )
            )


            // =================================================
            // USER ID ERROR
            // =================================================

            if (uiState.userIdError != null) {

                Text(
                    text =
                        uiState.userIdError!!,

                    color =
                        Color.Red,

                    fontSize = 12.sp,

                    modifier =
                        Modifier.padding(
                            start = 4.dp,
                            top = 5.dp
                        )
                )
            }


            // =================================================
            // PASSWORD LABEL
            // =================================================

            Spacer(
                modifier = Modifier.height(
                    if (uiState.userIdError != null) {
                        14.dp
                    } else {
                        18.dp
                    }
                )
            )

            Text(
                text = "Password",

                fontSize = 13.sp,

                fontWeight =
                    FontWeight.Medium,

                color = TextDark,

                modifier =
                    Modifier.padding(
                        start = 4.dp,
                        bottom = 7.dp
                    )
            )


            // =================================================
            // PASSWORD FIELD
            // =================================================

            OutlinedTextField(

                value =
                    uiState.password,

                onValueChange = {
                    viewModel.updatePassword(it)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),

                singleLine = true,

                placeholder = {

                    Text(
                        text =
                            "Enter your password",

                        color =
                            TextGray,

                        fontSize = 14.sp
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
                            Modifier.size(22.dp)
                    )
                },


                // ------------------------------------------------
                // SHOW / HIDE PASSWORD
                // ------------------------------------------------

                trailingIcon = {

                    IconButton(
                        onClick = {

                            passwordVisible =
                                !passwordVisible
                        }
                    ) {

                        Image(
                            painter =
                                painterResource(

                                    id =
                                        if (
                                            passwordVisible
                                        ) {
                                            R.drawable.ic_eye_show
                                        } else {
                                            R.drawable.ic_eye_hide
                                        }
                                ),

                            contentDescription =
                                if (
                                    passwordVisible
                                ) {
                                    "Hide password"
                                } else {
                                    "Show password"
                                },

                            modifier =
                                Modifier.size(22.dp)
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
                    RoundedCornerShape(12.dp),

                colors =
                    androidx.compose.material3
                        .OutlinedTextFieldDefaults
                        .colors(

                            focusedBorderColor =
                                XpertPurple,

                            unfocusedBorderColor =
                                if (
                                    uiState.passwordError != null
                                ) {
                                    Color.Red
                                } else {
                                    BorderGray
                                },

                            errorBorderColor =
                                Color.Red,

                            focusedContainerColor =
                                FieldBackground,

                            unfocusedContainerColor =
                                FieldBackground,

                            errorContainerColor =
                                FieldBackground,

                            cursorColor =
                                XpertPurple
                        )
            )


            // =================================================
            // PASSWORD ERROR
            // =================================================

            if (uiState.passwordError != null) {

                Text(
                    text =
                        uiState.passwordError!!,

                    color =
                        Color.Red,

                    fontSize = 12.sp,

                    modifier =
                        Modifier.padding(
                            start = 4.dp,
                            top = 5.dp
                        )
                )
            }


            // =================================================
            // SERVER / API ERROR
            // =================================================

            if (
                uiState.errorMessage != null &&
                uiState.userIdError == null &&
                uiState.passwordError == null
            ) {

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Text(
                    text =
                        uiState.errorMessage!!,

                    color =
                        Color.Red,

                    fontSize = 12.sp,

                    modifier =
                        Modifier.padding(
                            start = 4.dp
                        )
                )
            }


            // =================================================
            // LOGIN BUTTON
            // =================================================

            Spacer(
                modifier = Modifier.height(26.dp)
            )

            Button(

                onClick = {

                    onLoginClick(
                        uiState.userId,
                        uiState.password
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                shape =
                    RoundedCornerShape(12.dp),

                colors =
                    ButtonDefaults.buttonColors(

                        containerColor =
                            XpertPurple,

                        contentColor =
                            Color.White,

                        disabledContainerColor =
                            XpertPurple.copy(
                                alpha = 0.6f
                            )
                    ),

                enabled =
                    !uiState.isLoading
            ) {

                Text(

                    text =
                        if (uiState.isLoading) {
                            "LOGGING IN..."
                        } else {
                            "LOGIN"
                        },

                    fontSize = 17.sp,

                    fontWeight =
                        FontWeight.Bold,

                    letterSpacing =
                        0.5.sp
                )
            }


            // =================================================
            // SECURITY MESSAGE
            // =================================================

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.Center,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(
                            XpertOrange
                        )
                )

                Spacer(
                    modifier =
                        Modifier.width(7.dp)
                )

                Text(
                    text =
                        "Secure access to your alerts",

                    fontSize = 12.sp,

                    color =
                        TextGray
                )
            }


            // =================================================
            // VERSION
            // =================================================

            Spacer(
                modifier = Modifier.height(55.dp)
            )

            Text(
                text = "Version 1.0",

                modifier =
                    Modifier.fillMaxWidth(),

                textAlign =
                    TextAlign.Center,

                fontSize = 13.sp,

                color =
                    TextGray
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLoginClick = { _, _ -> },
        onSettingsClick = {}
    )
}