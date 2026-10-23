
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ChangePasswordOrange = Color(0xFFF47716)
private val ChangePasswordBorder = Color(0xFF777777)
private val ChangePasswordHint = Color(0xFF777777)
private val ChangePasswordText = Color(0xFF555555)
private val ChangePasswordFieldBackground = Color(0xFFF7F7F7)

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
){

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(ChangePasswordOrange),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Change Password",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.size(44.dp))
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
                .padding(top = 14.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = loginId,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                placeholder = {
                    Text(
                        text = "Login ID",
                        fontSize = 12.sp,
                        color = ChangePasswordHint,
                        maxLines = 1
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedContainerColor = ChangePasswordFieldBackground,
                    unfocusedContainerColor = ChangePasswordFieldBackground,
                    cursorColor = ChangePasswordOrange
                ),
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = ChangePasswordText
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(13.dp))

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
                    .height(45.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChangePasswordOrange,
                    contentColor = Color.White,
                    disabledContainerColor = ChangePasswordOrange.copy(
                        alpha = 0.6f
                    )
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Save",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            val displayedMessage = errorMessage.ifBlank {
                message.orEmpty()
            }

            if (displayedMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = displayedMessage,
                    color = if (isSuccess && errorMessage.isBlank()) {
                        Color(0xFF2E7D32)
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

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
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 12.sp,
                color = ChangePasswordHint,
                maxLines = 1
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(50.dp),
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(
                onClick = onVisibilityChange,
                modifier = Modifier.size(36.dp)
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
                    tint = ChangePasswordHint,
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ChangePasswordBorder,
            unfocusedBorderColor = ChangePasswordBorder,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = ChangePasswordOrange
        ),
        textStyle = MaterialTheme.typography.bodySmall.copy(
            fontSize = 12.sp,
            color = ChangePasswordText
        )
    )
}
