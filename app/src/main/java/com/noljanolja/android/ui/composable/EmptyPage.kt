package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EmptyPage(
    message: String,
    icon: (@Composable () -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        icon?.let {
            icon.invoke()
            Spacer(modifier = Modifier.height(10.dp))
        }
        Text(
            text = message,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun EmptyPagePreview() {
    EmptyPage("No contacts found")
}