package com.noljanolja.android.features.home.require_login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noljanolja.android.R
import com.noljanolja.android.common.composable.PrimaryButton

@Composable
fun RequireLoginBottomSheet(
    viewModel: RequireLoginViewModel = hiltViewModel(),
    onGoToLogin: () -> Unit
) {
    // TODO : Check verify if need after
    val hasUser = false
    val buttonText: String
    val description: String
    if (hasUser) {
        buttonText = stringResource(id = R.string.require_verify_button)
        description = stringResource(id = R.string.require_verify_description)
    } else {
        buttonText = stringResource(id = R.string.require_login_button)
        description = stringResource(id = R.string.require_login_description)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 51.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(id = R.string.require_login_title),
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
        PrimaryButton(
            modifier = Modifier
                .width(245.dp)
                .height(50.dp),
            text = buttonText,
            shape = RoundedCornerShape(25.dp),
            contentColor = MaterialTheme.colorScheme.secondary,
            onClick = onGoToLogin
        )
    }
}
