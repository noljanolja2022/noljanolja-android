package com.noljanolja.android.features.auth.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.R

@Composable
fun ColumnScope.VerifyEmail() {
    Spacer(modifier = Modifier.weight(1F))
    Image(painter = painterResource(id = R.drawable.ic_check_circle), contentDescription = null)
    Spacer(modifier = Modifier.height(14.dp))
    Text(
        stringResource(id = R.string.auth_identity_complete),
        style = TextStyle(
            fontWeight = FontWeight.W700,
            fontSize = 16.sp,
        ),
    )
    Spacer(modifier = Modifier.weight(1F))
}
