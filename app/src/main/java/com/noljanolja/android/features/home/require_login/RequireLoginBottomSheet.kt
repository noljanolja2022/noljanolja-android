package com.noljanolja.android.features.home.require_login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noljanolja.android.common.composable.RoundedButton

@Composable
fun RequireLoginBottomSheet(
    viewModel: RequireLoginViewModel = hiltViewModel(),
    onGoToLogin: () -> Unit
) {
    val hasUser by viewModel.hasUser.collectAsState()
    val description: String = if (hasUser) {
        "Verify email to play\nUse a variety of services!"
    } else {
        "Log in to play\nUse a variety of services!"
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 51.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Let's Play Start the service",
            color = MaterialTheme.colorScheme.secondary,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.W700
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = description,
            style = TextStyle(
                color = MaterialTheme.colorScheme.outline,
                fontSize = 16.sp
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(42.dp))
        RoundedButton(
            modifier = Modifier
                .width(245.dp)
                .height(50.dp),
            text = "Let's Play Log in",
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary,
            )
        ) {
            onGoToLogin.invoke()
        }
    }
}
