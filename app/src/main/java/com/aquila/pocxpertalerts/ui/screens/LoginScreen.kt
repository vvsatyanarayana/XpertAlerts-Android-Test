package com.aquila.pocxpertalerts.ui.screens

import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.domain.model.User
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
    onLoginSuccess: (User) -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext

    val settingsDataStore = remember(context) {
        SettingsDataStore(context)
    }

    val loginFactory = remember(settingsDataStore) {
        LoginViewModelFactory(settingsDataStore)
    }

    val loginViewModel: LoginViewModel = viewModel(
        factory = loginFactory
    )

    val uiState by loginViewModel.uiState.collectAsState()

    val deviceId = remember {
        Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ).orEmpty()
    }

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            uiState.user?.let { user ->
                onLoginSuccess(user)
            }

            loginViewModel.clearLoginSuccess()
        }
    }

    LoginScreenContent(
        userId = uiState.userId,
        password = uiState.password,
        userIdError = uiState.userIdError,
        passwordError = uiState.passwordError,
        errorMessage = uiState.errorMessage,
        successMessage = uiState.successMessage,
        isLoading = uiState.isLoading,
        onUserIdChange = loginViewModel::updateUserId,
        onPasswordChange = loginViewModel::updatePassword,
        onLoginClick = {
            loginViewModel.login(deviceId = deviceId)
        },
        onSettingsClick = onSettingsClick
    )
}

@Composable
private fun LoginScreenContent(
    userId: String,
    password: String,
    userIdError: String?,
    passwordError: String?,
    errorMessage: String?,
    successMessage: String?,
    isLoading: Boolean,
    onUserIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var passwordVisible by remember {
        mutableStateOf(false)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        val compactScreen = maxHeight < 700.dp

        val logoHeight = if (compactScreen) {
            112.dp
        } else {
            150.dp
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 32.dp,
                    vertical = 38.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Settings button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Image(
                            painter = painterResource(
                                R.drawable.gear
                            ),
                            contentDescription = "Settings",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(
                        if (compactScreen) 8.dp else 16.dp
                    )
                )

                // Application logo
                Image(
                    painter = painterResource(
                        R.drawable.xpertalerts_splash_logo
                    ),
                    contentDescription = "Xpert Alerts Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(logoHeight)
                        .padding(horizontal = 8.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(
                    modifier = Modifier.height(
                        if (compactScreen) 16.dp else 24.dp
                    )
                )

                // User ID
                OutlinedTextField(
                    value = userId,
                    onValueChange = onUserIdChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Enter User ID",
                            fontSize = 14.sp,
                            color = TextDark
                        )
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(
                                R.drawable.user
                            ),
                            contentDescription = "User ID",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    isError = userIdError != null,
                    shape = RoundedCornerShape(16.dp),
                    colors = loginTextFieldColors()
                )

                FieldMessage(
                    message = userIdError,
                    isError = true
                )

                Spacer(Modifier.height(12.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Enter Password",
                            fontSize = 14.sp,
                            color = TextDark
                        )
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(
                                R.drawable.lock
                            ),
                            contentDescription = "Password",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Image(
                                painter = painterResource(
                                    if (passwordVisible) {
                                        R.drawable.ic_eye_show
                                    } else {
                                        R.drawable.ic_eye_hide
                                    }
                                ),
                                contentDescription = if (passwordVisible) {
                                    "Hide password"
                                } else {
                                    "Show password"
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    isError = passwordError != null,
                    shape = RoundedCornerShape(16.dp),
                    colors = loginTextFieldColors()
                )

                FieldMessage(
                    message = passwordError,
                    isError = true
                )

                FieldMessage(
                    message = errorMessage,
                    isError = true
                )

                FieldMessage(
                    message = successMessage,
                    isError = false
                )

                Spacer(
                    modifier = Modifier.height(
                        if (compactScreen) 20.dp else 28.dp
                    )
                )

                // Login button
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = XpertOrange,
                        contentColor = White,
                        disabledContainerColor =
                            XpertOrange.copy(alpha = 0.65f),
                        disabledContentColor = White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = "Logging in…",
                            fontSize = 16.sp
                        )
                    } else {
                        Text(
                            text = "Login",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                // Application version
                Text(
                    text = "App Version : v1.6",
                    fontSize = 13.sp,
                    color = TextDark
                )

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun loginTextFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = XpertOrange,
        unfocusedBorderColor = BorderGray,
        errorBorderColor = Color(0xFFD32F2F),
        focusedContainerColor = White,
        unfocusedContainerColor = White,
        errorContainerColor = White,
        cursorColor = XpertOrange
    )

@Composable
private fun FieldMessage(
    message: String?,
    isError: Boolean
) {
    if (!message.isNullOrBlank()) {
        Text(
            text = message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    top = 4.dp,
                    end = 4.dp
                ),
            fontSize = 12.sp,
            color = if (isError) {
                Color(0xFFD32F2F)
            } else {
                Color(0xFF218838)
            }
        )
    }
}

// Preview: Standard mobile
@Preview(
    name = "Login - Phone",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=852dp,dpi=440"
)
@Composable
private fun LoginScreenPhonePreview() {
    XpertAlertsTheme {
        LoginScreenContent(
            userId = "",
            password = "",
            userIdError = null,
            passwordError = null,
            errorMessage = null,
            successMessage = null,
            isLoading = false,
            onUserIdChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onSettingsClick = {}
        )
    }
}

// Preview: Small mobile
@Preview(
    name = "Login - Small Phone",
    showBackground = true,
    device = "spec:width=320dp,height=640dp,dpi=320"
)
@Composable
private fun LoginScreenSmallPhonePreview() {
    XpertAlertsTheme {
        LoginScreenContent(
            userId = "demo.user",
            password = "password",
            userIdError = null,
            passwordError = null,
            errorMessage = null,
            successMessage = null,
            isLoading = false,
            onUserIdChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onSettingsClick = {}
        )
    }
}