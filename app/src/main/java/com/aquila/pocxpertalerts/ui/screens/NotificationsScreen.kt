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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import com.aquila.pocxpertalerts.ui.theme.XpertPurple


@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {

        // =====================================================
        // TOP BAR
        // =====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(White)
                .padding(horizontal = 8.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(48.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = XpertPurple,
                    modifier = Modifier.size(26.dp)
                )
            }


            Text(
                text = "Notifications",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = XpertPurple
            )
        }


        // =====================================================
        // CONTENT
        // =====================================================

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // =================================================
                // NOTIFICATION ICON
                // =================================================

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = XpertOrange.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(24.dp)
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(
                            id = R.drawable.ic_alert_list_bel
                        ),
                        contentDescription = "Notifications",
                        modifier = Modifier.size(42.dp)
                    )
                }


                Spacer(
                    modifier = Modifier.height(18.dp)
                )


                // =================================================
                // TITLE
                // =================================================

                Text(
                    text = "Notifications",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )


                Spacer(
                    modifier = Modifier.height(6.dp)
                )


                // =================================================
                // DESCRIPTION
                // =================================================

                Text(
                    text = "Your notifications will appear here.",
                    fontSize = 14.sp,
                    color = TextGray
                )
            }
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
fun NotificationsScreenPreview() {

    XpertAlertsTheme {

        NotificationsScreen(
            onBackClick = {}
        )
    }
}