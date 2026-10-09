package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit = {}
) {

    var email by remember {
        mutableStateOf("")
    }

    var otp by remember {
        mutableStateOf("")
    }

    var newPassword by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var otpSent by remember {
        mutableStateOf(false)
    }

    var passwordReset by remember {
        mutableStateOf(false)
    }

    var newPasswordVisible by remember {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }


    // =========================================================
    // PASSWORD VALIDATION
    // =========================================================

    val hasMinLength = newPassword.length >= 8

    val hasUppercase =
        newPassword.any { it.isUpperCase() }

    val hasLowercase =
        newPassword.any { it.isLowerCase() }

    val hasNumber =
        newPassword.any { it.isDigit() }

    val hasSpecial =
        newPassword.any {
            !it.isLetterOrDigit()
        }

    val passwordStrength =
        listOf(
            hasMinLength,
            hasUppercase,
            hasLowercase,
            hasNumber,
            hasSpecial
        ).count { it }

    val passwordsMatch =
        newPassword.isNotEmpty() &&
                newPassword == confirmPassword


    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Reset Password")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,
                            contentDescription =
                                "Back"
                        )
                    }
                }
            )
        }

    ) { innerPadding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp),

            verticalArrangement =
                Arrangement.Top
        ) {

            // =================================================
            // HEADER
            // =================================================

            Text(
                text = "Forgot your password?",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    "Enter your registered email address to receive an OTP and reset your password.",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )


            // =================================================
            // EMAIL
            // =================================================

            OutlinedTextField(

                value = email,

                onValueChange = {
                    email = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Email Address")
                },

                leadingIcon = {

                    Icon(
                        imageVector =
                            Icons.Default.Email,
                        contentDescription =
                            null
                    )
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Email
                    ),

                singleLine = true,

                enabled = !otpSent
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            // =================================================
            // SEND OTP
            // =================================================

            if (!otpSent) {

                Button(

                    onClick = {

                        // UI ONLY
                        // API/OTP integration later

                        otpSent = true
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),

                    enabled =
                        email.isNotBlank()
                ) {

                    Text("Send OTP")
                }
            }


            // =================================================
            // OTP + PASSWORD SECTION
            // =================================================

            if (otpSent) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant
                        )
                ) {

                    Column(

                        modifier =
                            Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "OTP Verification",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Enter the OTP sent to your registered email address.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )


                // =================================================
                // OTP
                // =================================================

                OutlinedTextField(

                    value = otp,

                    onValueChange = {

                        if (
                            it.length <= 6 &&
                            it.all { char ->
                                char.isDigit()
                            }
                        ) {
                            otp = it
                        }
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Enter OTP")
                    },

                    singleLine = true,

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text = "Didn't receive the OTP? Resend OTP",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
                )


                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )


                // =================================================
                // NEW PASSWORD
                // =================================================

                OutlinedTextField(

                    value = newPassword,

                    onValueChange = {
                        newPassword = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("New Password")
                    },

                    singleLine = true,

                    visualTransformation =
                        if (newPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),

                    trailingIcon = {

                        IconButton(

                            onClick = {

                                newPasswordVisible =
                                    !newPasswordVisible
                            }

                        ) {

                            Icon(

                                imageVector =
                                    if (newPasswordVisible)
                                        Icons.Default.VisibilityOff
                                    else
                                        Icons.Default.Visibility,

                                contentDescription =
                                    "Show password"
                            )
                        }
                    }
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                // =================================================
                // PASSWORD STRENGTH
                // =================================================

                if (newPassword.isNotEmpty()) {

                    Text(
                        text = "Password strength",
                        style =
                            MaterialTheme
                                .typography
                                .labelLarge
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    LinearProgressIndicator(

                        progress = {
                            passwordStrength / 5f
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    PasswordRequirement(
                        text = "At least 8 characters",
                        satisfied = hasMinLength
                    )

                    PasswordRequirement(
                        text = "One uppercase letter",
                        satisfied = hasUppercase
                    )

                    PasswordRequirement(
                        text = "One lowercase letter",
                        satisfied = hasLowercase
                    )

                    PasswordRequirement(
                        text = "One number",
                        satisfied = hasNumber
                    )

                    PasswordRequirement(
                        text = "One special character",
                        satisfied = hasSpecial
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )


                // =================================================
                // CONFIRM PASSWORD
                // =================================================

                OutlinedTextField(

                    value = confirmPassword,

                    onValueChange = {
                        confirmPassword = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Confirm New Password")
                    },

                    singleLine = true,

                    isError =
                        confirmPassword.isNotEmpty() &&
                                !passwordsMatch,

                    visualTransformation =
                        if (confirmPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),

                    trailingIcon = {

                        IconButton(

                            onClick = {

                                confirmPasswordVisible =
                                    !confirmPasswordVisible
                            }

                        ) {

                            Icon(

                                imageVector =
                                    if (confirmPasswordVisible)
                                        Icons.Default.VisibilityOff
                                    else
                                        Icons.Default.Visibility,

                                contentDescription =
                                    "Show password"
                            )
                        }
                    }
                )


                if (
                    confirmPassword.isNotEmpty() &&
                    !passwordsMatch
                ) {

                    Text(
                        text =
                            "Passwords do not match",

                        color =
                            MaterialTheme
                                .colorScheme
                                .error,

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                // =================================================
                // RESET PASSWORD
                // =================================================

                Button(

                    onClick = {

                        // UI ONLY
                        // API integration later

                        passwordReset = true
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),

                    enabled =
                        otp.length == 6 &&
                                passwordStrength == 5 &&
                                passwordsMatch
                ) {

                    Text("Reset Password")
                }


                // =================================================
                // SUCCESS MESSAGE
                // =================================================

                if (passwordReset) {

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    Card(

                        modifier =
                            Modifier.fillMaxWidth(),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme
                                        .colorScheme
                                        .primaryContainer
                            )
                    ) {

                        Row(

                            modifier =
                                Modifier.padding(16.dp),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.CheckCircle,
                                contentDescription =
                                    null
                            )

                            Spacer(
                                modifier =
                                    Modifier.padding(
                                        horizontal = 6.dp
                                    )
                            )

                            Text(
                                text =
                                    "Password reset successfully.",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}


// =============================================================
// PASSWORD REQUIREMENT
// =============================================================

@Composable
private fun PasswordRequirement(
    text: String,
    satisfied: Boolean
) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(

            imageVector =
                Icons.Default.CheckCircle,

            contentDescription =
                null,

            tint =
                if (satisfied)
                    MaterialTheme
                        .colorScheme
                        .primary
                else
                    MaterialTheme
                        .colorScheme
                        .outline
        )

        Spacer(
            modifier =
                Modifier.padding(
                    horizontal = 4.dp
                )
        )

        Text(
            text = text,
            style =
                MaterialTheme
                    .typography
                    .bodySmall
        )
    }
}


// =============================================================
// PREVIEW
// =============================================================

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ChangePasswordScreenPreview() {

    XpertAlertsTheme {

        ChangePasswordScreen()
    }
}