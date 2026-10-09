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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {}
) {

    var userId by remember {
        mutableStateOf("User ID")
    }

    var name by remember {
        mutableStateOf("Xpert Alerts User")
    }

    var email by remember {
        mutableStateOf("user@example.com")
    }

    var phone by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {

            TopAppBar(

                title = {
                    Text("Profile")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,
                            contentDescription = "Back"
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
            // PROFILE HEADER
            // =================================================

            Card(

                modifier = Modifier
                    .fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .primaryContainer
                )
            ) {

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Person,

                        contentDescription =
                            "Profile",

                        modifier = Modifier
                            .padding(end = 16.dp)
                    )

                    Column {

                        Text(
                            text = name,
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text = "Xpert Alerts User",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium
                        )
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            // =================================================
            // USER ID
            // =================================================

            OutlinedTextField(

                value = userId,

                onValueChange = {
                    userId = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("User ID")
                },

                leadingIcon = {

                    Icon(
                        imageVector =
                            Icons.Default.Person,
                        contentDescription =
                            null
                    )
                },

                singleLine = true,

                readOnly = true
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            // =================================================
            // NAME
            // =================================================

            OutlinedTextField(

                value = name,

                onValueChange = {
                    name = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Name")
                },

                leadingIcon = {

                    Icon(
                        imageVector =
                            Icons.Default.Person,
                        contentDescription =
                            null
                    )
                },

                singleLine = true
            )


            Spacer(
                modifier = Modifier.height(16.dp)
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
                    Text("Email")
                },

                leadingIcon = {

                    Icon(
                        imageVector =
                            Icons.Default.Email,
                        contentDescription =
                            null
                    )
                },

                singleLine = true,

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Email
                    )
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            // =================================================
            // PHONE
            // =================================================

            OutlinedTextField(

                value = phone,

                onValueChange = {
                    phone = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Phone Number")
                },

                leadingIcon = {

                    Icon(
                        imageVector =
                            Icons.Default.Phone,
                        contentDescription =
                            null
                    )
                },

                singleLine = true,

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Phone
                    )
            )


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            Text(
                text = "Profile information will be connected to the server during API integration.",
                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )
        }
    }
}


// ============================================================
// PREVIEW
// ============================================================

@Preview(
    showBackground = true,
    showSystemUi = true,

    )
@Composable
private fun ProfileScreenPreview() {

    XpertAlertsTheme {

        ProfileScreen()
    }
}