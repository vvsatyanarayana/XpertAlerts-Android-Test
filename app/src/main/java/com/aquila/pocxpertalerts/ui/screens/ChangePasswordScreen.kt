
package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.components.AppTopBar
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.IconGreen
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange

@Composable
fun ChangePasswordScreen(
    loginId: String = "",
    onBackClick: () -> Unit = {},
    isLoading: Boolean = false,
    message: String? = null,
    isSuccess: Boolean = false,
    onSaveClick: (
        loginId: String,
        oldPassword: String,
        newPassword: String
    ) -> Unit = { _, _, _ -> }
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AppTopBar(
                title = "Change Password",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(innerPadding)
                .padding(
                    horizontal = 16.dp,
                    vertical = 20.dp
                ),
            verticalArrangement = Arrangement.Top
        ) {

            // Read-only Login ID
            OutlinedTextField(
                value = loginId,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Login ID",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                },
                label = {
                    Text(
                        text = "Login ID",
                        fontSize = 13.sp
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = XpertOrange,
                    unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark,
                    cursorColor = XpertOrange
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Old Password
            PasswordInput(
                value = oldPassword,
                onValueChange = {
                    oldPassword = it
                    errorMessage = ""
                },
                placeholder = "Old Password",
                passwordVisible = oldPasswordVisible,
                onVisibilityChange = {
                    oldPasswordVisible = !oldPasswordVisible
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // New Password
            PasswordInput(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = ""
                },
                placeholder = "New Password",
                passwordVisible = newPasswordVisible,
                onVisibilityChange = {
                    newPasswordVisible = !newPasswordVisible
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Confirm Password
            PasswordInput(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                placeholder = "Re-enter Your Password",
                passwordVisible = confirmPasswordVisible,
                onVisibilityChange = {
                    confirmPasswordVisible = !confirmPasswordVisible
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Save button
            Button(
                onClick = {
                    errorMessage = when {
                        loginId.isBlank() ->
                            "Please enter your Login ID"

                        oldPassword.isBlank() ->
                            "Please enter your old password"

                        newPassword.isBlank() ->
                            "Please enter your new password"

                        confirmPassword.isBlank() ->
                            "Please re-enter your new password"

                        newPassword != confirmPassword ->
                            "Passwords do not match"

                        oldPassword == newPassword ->
                            "New password must differ from old password"

                        newPassword.length < 6 ->
                            "Password must be at least 6 characters"

                        newPassword.length > 16 ->
                            "Password should not exceed 16 characters"

                        newPassword.none { it.isDigit() } ->
                            "Password must contain at least one digit"

                        newPassword.none { !it.isLetterOrDigit() } ->
                            "Password must contain a special character"

                        else -> ""
                    }

                    if (errorMessage.isEmpty()) {
                        onSaveClick(
                            loginId.trim(),
                            oldPassword,
                            newPassword
                        )
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = XpertOrange,
                    contentColor = White,
                    disabledContainerColor = XpertOrange.copy(alpha = 0.6f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
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

            // Validation, API error, or success message
            val displayedMessage = errorMessage.ifBlank {
                message.orEmpty()
            }

            if (displayedMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = displayedMessage,
                    color = if (isSuccess && errorMessage.isBlank()) {
                        IconGreen
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ------------------------------------------------------------
// REUSABLE PASSWORD FIELD
// ------------------------------------------------------------

@Composable
private fun PasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    passwordVisible: Boolean,
    onVisibilityChange: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 14.sp,
                color = TextGray
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(
                onClick = onVisibilityChange
            ) {
                Icon(
                    imageVector = if (passwordVisible) {
                        Icons.Default.Visibility
                    } else {
                        Icons.Default.VisibilityOff
                    },
                    contentDescription = if (passwordVisible) {
                        "Hide password"
                    } else {
                        "Show password"
                    },
                    tint = TextGray
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = XpertOrange,
            unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark,
            cursorColor = XpertOrange
        )
    )
}

// ------------------------------------------------------------
// PREVIEW
// ------------------------------------------------------------

@Preview(
    name = "Change Password Screen",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=873dp,dpi=440"
)
@Composable
private fun ChangePasswordScreenPreview() {
    XpertAlertsTheme {
        ChangePasswordScreen(
            loginId = "satyanarayana@example.com",
            onBackClick = {},
            isLoading = false,
            message = null,
            isSuccess = false,
            onSaveClick = { _, _, _ -> }
        )
    }
}
