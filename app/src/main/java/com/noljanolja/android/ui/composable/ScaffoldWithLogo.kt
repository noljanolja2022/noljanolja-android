package com.noljanolja.android.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithLogo(
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    Scaffold() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .then(modifier)
                .padding(it)
                .padding(bottom = 50.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .width(188.dp)
                    .height(176.dp)
            )
            content?.invoke()
        }
    }
}